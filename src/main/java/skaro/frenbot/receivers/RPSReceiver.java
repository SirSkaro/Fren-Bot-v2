package skaro.frenbot.receivers;

import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.receivers.dtos.BadgeDTO;
import skaro.frenbot.receivers.dtos.NewAwardsDTO;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;
import skaro.frenbot.receivers.services.RPSOption;
import skaro.frenbot.receivers.services.RPSService;

public class RPSReceiver implements Receiver {

	@Autowired
	private RPSService rpsService;
	@Autowired
	private DiscordService discordService;
	@Autowired
	private PokeAimPIService apiService;
	
	private Member player1, player2;
	private RPSOption player1Option, player2Option;
	
	@Override
	public Mono<Message> process(Argument argument, Message message) {
		return discordService.notifyRequestRecieved(message)
			.then(discordService.getAuthor(message))
			.publishOn(Schedulers.single())
			.flatMap(author -> setPlayer1(author, message)
					.switchIfEmpty(Mono.defer(() -> setPlayer2(author, message))))
			.flatMap(responseMessage -> conductGame(responseMessage))
			.flatMap(result -> processResult(result))
			.flatMap(messageSpec -> discordService.replyToMessage(message, messageSpec));
	}
	
	private Mono<Message> setPlayer1(Member player, Message message) {
		if(player1 != null) {
			return Mono.empty();
		}
		
		player1 = player;
		player1Option = rpsService.getWeightedOption();
		return discordService.replyToMessage(message, player1ReadyMessage());
	}
	
	private Mono<Message> setPlayer2(Member player, Message message) {
		if(player1.getId().equals(player.getId())) {
			return discordService.replyToMessage(message, spec -> spec.setContent("You can't play against yourself :horse:")); 
		}
		player2 = player;
		player2Option = rpsService.getWeightedOption();
		return discordService.replyToMessage(message, player2ReadyMessage());
	}

	private void resetGame() {
		player1 = null;
		player2 = null;
		player1Option = null;
		player2Option = null;
	}

	private Consumer<MessageCreateSpec> player1ReadyMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%s has issued a challenge to play Rock-Paper-Scissors!", player1.getMention()));
		builder.append(String.format("\n%s got %s", player1.getDisplayName(), player1Option.getName()));
		Consumer<EmbedCreateSpec> embedSpec = spec -> spec.setDescription(builder.toString())
				.setImage(player1Option.getImageURL())
				.setColor(Color.RED)
				.setFooter("Use the the same command to accept their challenge!", null);
		
		return spec -> spec.setEmbed(embedSpec);
	}
	
	private Consumer<MessageCreateSpec> player2ReadyMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%s accepted %s's challenge!", player2.getMention(), player1.getMention()));
		builder.append(String.format("\n%s got %s", player2.getDisplayName(), player2Option.getName()));
		Consumer<EmbedCreateSpec> embedSpec = spec -> spec.setDescription(builder.toString())
				.setColor(Color.BLUE)
				.setImage(player2Option.getImageURL());
		
		return spec -> spec.setEmbed(embedSpec);
	}
	
	private Mono<RPSResult> conductGame(Message message) {
		if(player1 == null || player2 == null) {
			return Mono.empty();
		}
		
		Member winner;
		RPSOption winningOption;
		
		if(player1Option == player2Option) {
			resetGame();
			return discordService.replyToMessage(message, spec -> spec.setContent("It's a draw!"))
					.then(Mono.empty());
		} else if (player2Option.losesTo(player1Option)) {
			winner = player1;
			winningOption = player1Option;
		} else {
			winner = player2;
			winningOption = player2Option;
		}
		
		resetGame();
		return apiService.addPointsToUser(winner, winningOption.getPointsForWinning())
				.map(awards -> new RPSResult(winner, winningOption, awards));
	}
	
	private Mono<Consumer<MessageCreateSpec>> processResult(RPSResult result) {
		List<BadgeDTO> sortedBadges = result.getApiAwards().getBadges();
		
		return discordService.assignBadgeRoles(result.getWinner(), sortedBadges)
				.then(Mono.just(createVictoryMessage(result)))
				.map(embedConsumer -> (MessageCreateSpec spec) -> spec.setEmbed(embedConsumer));
	}
	
	private Consumer<EmbedCreateSpec> createVictoryMessage(RPSResult result) {
		String winnerName = result.getWinner().getDisplayName();
		String rewardTitle = String.format(":crossed_swords: %s", result.getWinningOption().getVictoryMessage(winnerName));
		Consumer<EmbedCreateSpec> embedSpec = spec -> spec.setTitle(rewardTitle);
		return embedSpec.andThen(spec -> spec.setColor(Color.GREEN))
				.andThen(addBadgesToMessage(embedSpec, result.getApiAwards(), result.getWinner()));
	}
	
	private Consumer<EmbedCreateSpec> addBadgesToMessage(Consumer<EmbedCreateSpec> embedSpec, NewAwardsDTO newAwards, Member user) {
		List<BadgeDTO> sortedBadges = newAwards.getBadges();
		if(sortedBadges.isEmpty()) {
			return embedSpec;
		}
		
		BadgeDTO mostValuedBadge = sortedBadges.get(sortedBadges.size() - 1);
		return embedSpec.andThen(spec -> spec.setThumbnail(mostValuedBadge.getImageUri()))
				.andThen(spec -> spec.setDescription(createBadgeList(sortedBadges, user)));
	}
	
	private String createBadgeList(List<BadgeDTO> sortedBadges, Member user) {
		StringBuilder builder = new StringBuilder();
		builder.append("and "+ user.getDisplayName()+" got " + (sortedBadges.size() > 1 ? "badges" : "a badge") + "! :tada:" );
		for (BadgeDTO badge : sortedBadges) {
			builder.append("\n:small_orange_diamond:");
			builder.append(String.format("the **__%s__** badge for earning **__%d__** points", badge.getTitle(), badge.getPointThreshold()));
		}
		
		return builder.toString();
	}
	
}


