package skaro.frenbot.receivers;

import java.awt.Color;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.arguments.BadgeArgument;
import skaro.frenbot.receivers.dtos.BadgeDTO;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;

public class BadgeReceiver implements Receiver {

	@Autowired
	private PokeAimPIService apiService;
	@Autowired
	private DiscordService discordService;
	
	@Override
	public Mono<Message> process(Argument argument, Message message) {
		BadgeArgument badgeArgument = (BadgeArgument)argument;
		
		return discordService.getRoleByName(badgeArgument.getBadgeName())
				.flatMap(role -> apiService.getBadge(role.getId().asLong())
						.map(badge -> formatResponse(badge, role)))
				.defaultIfEmpty(formatNoRoleFoundResponse(badgeArgument))
				.flatMap(reply -> discordService.replyToMessage(message, reply));
	}
	
	private Consumer<MessageCreateSpec> formatResponse(BadgeDTO badge, Role role) {
		String threshold = badge.getCanBeEarnedWithPoints() ?
				badge.getPointThreshold().toString() + " points"
				: "Reward only" ;
		
		Consumer<EmbedCreateSpec> embedSpec = spec -> spec.setDescription(badge.getDescription())
				.setThumbnail(badge.getImageUri())
				.setAuthor(badge.getTitle(), null, badge.getImageUri())
				.setColor(role.getColor())
				.setFooter(threshold, null);
		
		return (MessageCreateSpec spec) -> spec.setEmbed(embedSpec);
	}
	
	private Consumer<MessageCreateSpec> formatNoRoleFoundResponse(BadgeArgument badgeArgument) {
		String noRoleDescription = String.format("There's no role in the server named \"%s\". Check your spelling and try again?", badgeArgument.getBadgeName());
		Consumer<EmbedCreateSpec> embedSpec = (EmbedCreateSpec spec) -> spec.setTitle(":no_entry: We don't have that role")
				.setDescription(noRoleDescription)
				.setColor(Color.RED);
		return (MessageCreateSpec spec) -> spec.setEmbed(embedSpec);
	}

}
