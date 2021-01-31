package skaro.frenbot.receivers;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.arguments.PointAwardArgument;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;
import skaro.pokeaimpi.sdk.resource.Badge;
import skaro.pokeaimpi.sdk.resource.NewAwardList;

public class PointAwardReceiver implements Receiver {

	@Autowired
	private PokeAimPIService apiService;
	@Autowired
	private DiscordService discordService;

	@Override
	public Mono<Message> process(Argument argument, Message message) {
		PointAwardArgument awardArgument = (PointAwardArgument)argument;

		return discordService.notifyRequestRecieved(message)
				.then(discordService.getUserById(awardArgument.getUserDiscordId()))
				.flatMap(user -> apiService.addPointsToUser(user, awardArgument.getAmount())
						.flatMap(newAwards -> processAwards(newAwards, awardArgument, user))
						.flatMap(messageSpec -> discordService.replyToMessage(message, messageSpec)));
	}
	
	private Mono<Consumer<MessageCreateSpec>> processAwards(NewAwardList newAwards, PointAwardArgument pointAward,  Member user) {
		List<Badge> sortedBadges = newAwards.getBadges();
		
		return discordService.assignBadgeRoles(user, sortedBadges)
				.then(Mono.just(createConfirmationMessage(newAwards, pointAward, user)))
				.map(embedConsumer -> (MessageCreateSpec spec) -> spec.setEmbed(embedConsumer));
	}
	
	private Consumer<EmbedCreateSpec> createConfirmationMessage(NewAwardList newAwards, PointAwardArgument pointAward, Member user) {
		String rewardTitle = String.format(":white_check_mark: Rewarded %d points to %s", pointAward.getAmount(), user.getDisplayName() );
		Consumer<EmbedCreateSpec> embedSpec = spec -> spec.setTitle(rewardTitle);
		return embedSpec.andThen(spec -> spec.setColor(Color.GREEN))
				.andThen(addBadgesToMessage(embedSpec, newAwards, user));
	}
	
	private Consumer<EmbedCreateSpec> addBadgesToMessage(Consumer<EmbedCreateSpec> embedSpec, NewAwardList newAwards, Member user) {
		List<Badge> sortedBadges = newAwards.getBadges();
		if(sortedBadges.isEmpty()) {
			return embedSpec;
		}
		
		Badge mostValuedBadge = sortedBadges.get(sortedBadges.size() - 1);
		return embedSpec.andThen(spec -> spec.setThumbnail(mostValuedBadge.getImageUri()))
				.andThen(spec -> spec.setDescription(createBadgeList(sortedBadges, user)));
	}
	
	private String createBadgeList(List<Badge> sortedBadges, Member user) {
		StringBuilder builder = new StringBuilder();
		builder.append("and "+ user.getDisplayName()+" got " + (sortedBadges.size() > 1 ? "badges" : "a badge") + "! :tada:" );
		for (Badge badge : sortedBadges) {
			builder.append("\n:small_orange_diamond:");
			builder.append(String.format("the **__%s__** badge for earning **__%d__** points", badge.getTitle(), badge.getPointThreshold()));
		}
		
		return builder.toString();
	}

}
