package skaro.frenbot.receivers;

import java.awt.Color;
import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.arguments.BadgeAwardArgument;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;
import skaro.pokeaimpi.sdk.resource.Badge;
import skaro.pokeaimpi.sdk.resource.BadgeAwardRecord;

public class BadgeAwardReceiver implements Receiver {

	@Autowired
	private PokeAimPIService apiService;
	@Autowired
	private DiscordService discordService;
	
	@Override
	public Mono<Message> process(Argument argument, Message message) {
		BadgeAwardArgument awardArgument = (BadgeAwardArgument)argument;
		
		return discordService.getUserById(awardArgument.getUserDiscordId())
			.flatMap(user -> discordService.getRoleByName(awardArgument.getBadgeTitle())
					.flatMap(role -> awardBadge(user, role))
					.map(award -> formatResponse(award, user)))
			.switchIfEmpty(formatNoSuchRoleResponse(awardArgument.getBadgeTitle()))
			.flatMap(reply -> discordService.replyToMessage(message, reply));
	}
	
	private Mono<BadgeAwardRecord> awardBadge(Member user, Role role) {
		return apiService.awardBadge(user, role)
			.flatMap(award -> discordService.assignBadgeRoles(user, Arrays.asList(award.getBadge()))
					.then(Mono.just(award)));
	}
	
	private Consumer<MessageCreateSpec> formatResponse(BadgeAwardRecord badgeAward, Member user) {
		Badge badge = badgeAward.getBadge();
		String rewardTitle = String.format(":white_check_mark: Rewarded the %s badge to %s", badge.getTitle(), user.getDisplayName());
		Consumer<EmbedCreateSpec> embedSpec = spec -> spec.setTitle(rewardTitle)
				.setColor(Color.GREEN)
				.setThumbnail(badge.getImageUri())
				.addField("Badge description", badge.getDescription(), true);
		
		return (MessageCreateSpec spec) -> spec.setEmbed(embedSpec);
	}
	
	private Mono<Consumer<MessageCreateSpec>> formatNoSuchRoleResponse(String roleName) {
		String noRoleDescription = String.format("There's no role in the server named \"%s\". Check your spelling and try again?", roleName);
		Consumer<EmbedCreateSpec> embedSpec = (EmbedCreateSpec spec) -> spec.setTitle(":no_entry: We don't have that role")
				.setDescription(noRoleDescription)
				.setColor(Color.RED);
		return Mono.just((MessageCreateSpec spec) -> spec.setEmbed(embedSpec));
	}

}
