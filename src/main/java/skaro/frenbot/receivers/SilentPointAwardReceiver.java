package skaro.frenbot.receivers;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.arguments.PointAwardArgument;
import skaro.frenbot.receivers.dtos.BadgeDTO;
import skaro.frenbot.receivers.dtos.NewAwardsDTO;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;

public class SilentPointAwardReceiver implements Receiver {
	
	@Autowired
	private PokeAimPIService apiService;
	@Autowired
	private DiscordService discordService;
	
	@Override
	public Mono<Message> process(Argument argument, Message message) {
		PointAwardArgument awardArgument = (PointAwardArgument)argument;
		
		return discordService.getUserById(awardArgument.getUserDiscordId())
				.flatMap(user -> apiService.addPointsToUser(user, awardArgument.getAmount())
						.filter(newAwards -> !newAwards.getBadges().isEmpty())
						.flatMap(newAwards -> processAwards(newAwards, user))
						.flatMap(rewardMessageSpec -> discordService.replyToMessage(message, rewardMessageSpec)));
	}
	
	private Mono<Consumer<MessageCreateSpec>> processAwards(NewAwardsDTO newAwards, Member user) {
		List<BadgeDTO> sortedBadges = newAwards.getBadges();
		BadgeDTO mostValuedBadge = sortedBadges.get(sortedBadges.size() - 1);
		
		return discordService.assignBadgeRoles(user, sortedBadges)
				.then(discordService.getRoleForBadge(mostValuedBadge))
				.map(role -> createAwardNotification(sortedBadges, user, role))
				.map(embedConsumer -> (MessageCreateSpec spec) -> spec.setEmbed(embedConsumer));
	}
	
	private Consumer<EmbedCreateSpec> createAwardNotification(List<BadgeDTO> sortedBadges, Member user, Role role) {
		BadgeDTO mostValuedBadge = sortedBadges.get(sortedBadges.size() - 1);
		
		Consumer<EmbedCreateSpec> embedSpec = spec -> spec.setThumbnail(mostValuedBadge.getImageUri());
		embedSpec = embedSpec.andThen(spec -> spec.setDescription(createDescription(sortedBadges, user)));
		embedSpec = embedSpec.andThen(spec -> spec.setTitle((sortedBadges.size() > 1 ? "Badges Awarded" : "Badge Awarded") + "! :tada:"));
		embedSpec = embedSpec.andThen(spec -> spec.setColor(role.getColor()));
		
		return embedSpec;
	}
	
	private String createDescription(List<BadgeDTO> badges, Member user) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Congratulations "+ user.getUsername() +"! You've earned:");
		for (BadgeDTO badge : badges) {
			builder.append("\n:small_orange_diamond:");
			builder.append(String.format("the **__%s__** badge for earning **__%d__** points", badge.getTitle(), badge.getPointThreshold()));
		}
		
		return builder.toString();
	}

}
