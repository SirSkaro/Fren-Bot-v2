package skaro.frenbot.receivers;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.arguments.UserArgument;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;
import skaro.pokeaimpi.sdk.resource.Badge;
import skaro.pokeaimpi.sdk.resource.BadgeAwardRecord;

public class ProfileReceiver implements Receiver {

	@Autowired
	PokeAimPIService apiService;
	@Autowired
	DiscordService discordService;
	
	@Override
	public Mono<Message> process(Argument argument, Message message) {
		UserArgument userArgument = (UserArgument)argument;
		
		return discordService.getUserById(userArgument.getUserDiscordId())
				.flatMap(user -> apiService.getUserBadges(user).collectList()
						.flatMap(userAwards -> formatProfile(user, userAwards)))
				.flatMap(reply -> discordService.replyToMessage(message, reply));
	}
	
	private Mono<Consumer<MessageCreateSpec>> formatProfile(Member user, List<BadgeAwardRecord> awards) {
		return getColorOfHighestRankBadge(awards)
				.map(color -> formatProfile(user, awards, color))
				.map(embedConsumer -> (MessageCreateSpec spec) -> spec.setEmbed(embedConsumer));
	}
	
	private Consumer<EmbedCreateSpec> formatProfile(Member user, List<BadgeAwardRecord> awards, Color color) {
		DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
		
		String earnedAwards = awards.stream()
				.filter(award -> award.getBadge().getCanBeEarnedWithPoints())
				.map(award -> String.format(":small_blue_diamond: `%s` - %s", award.getBadge().getTitle(), dateFormat.format(award.getAwardDate())))
				.collect(Collectors.joining("\n"));
		
		String nonEarnedAwards = awards.stream()
				.filter(award -> !award.getBadge().getCanBeEarnedWithPoints())
				.map(award -> String.format(":small_orange_diamond: `%s` - %s", award.getBadge().getTitle(), dateFormat.format(award.getAwardDate())))
				.collect(Collectors.joining("\n"));
		
		return spec -> spec.setThumbnail(user.getAvatarUrl())
				.setColor(color)
				.setTitle(user.getDisplayName() + "'s Profile")
				.addField("Rank Badges", earnedAwards.isEmpty() ? "None" : earnedAwards, false)
				.addField("Badges", nonEarnedAwards.isEmpty() ? "None" : nonEarnedAwards, false)
				.setFooter("joined "+dateFormat.format(Date.from(user.getJoinTime())), null);
	}
	
	private Mono<Color> getColorOfHighestRankBadge(List<BadgeAwardRecord> awards) {
		List<Badge> badges = awards.stream()
				.map(award -> award.getBadge())
				.collect(Collectors.toList());

		return Mono.justOrEmpty(apiService.getMostValuedRankedBadge(badges)) 
				.flatMap(badge -> discordService.getRoleForBadge(badge))
				.map(role -> role.getColor())
				.defaultIfEmpty(Color.BLACK);
	}

}
