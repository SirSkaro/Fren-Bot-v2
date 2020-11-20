package skaro.frenbot.receivers;

import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;
import skaro.pokeaimpi.sdk.resource.Badge;
import skaro.pokeaimpi.sdk.resource.UserProgress;

public class ProgressReceiver implements Receiver {

	@Autowired
	private PokeAimPIService apiService;
	@Autowired
	private DiscordService discordService;
	
	@Override
	public Mono<Message> process(Argument argument, Message message) {
		return discordService.notifyRequestRecieved(message)
				.then(discordService.getAuthor(message)
				.flatMap(author -> apiService.getUserProgress(author)
					.flatMap(userProgress -> formatMessage(userProgress, author))
					.flatMap(messageSpec -> discordService.replyToMessage(message, messageSpec))));
	}
	
	private Mono<Consumer<MessageCreateSpec>> formatMessage(UserProgress userProgress, Member user) {
		Badge currentBadge = userProgress.getCurrentHighestBadge();
		
		if(currentBadge != null) {
			return discordService.getRoleForBadge(currentBadge)
					.map(role -> formatMessageEmbed(userProgress, user, role))
					.map(embedSpec -> (MessageCreateSpec spec) -> spec.setEmbed(embedSpec));
		}
		
		Consumer<MessageCreateSpec> messageSpec = spec -> spec.setEmbed(formatMessageEmbed(userProgress, user));
		return Mono.just(messageSpec);
	}
	
	private Consumer<EmbedCreateSpec> formatMessageEmbed(UserProgress userProgress, Member user) {
		String authorIconURI = userProgress.getCurrentHighestBadge() != null ? userProgress.getCurrentHighestBadge().getImageUri() : null;
		return spec -> spec.setAuthor(user.getDisplayName(), null, authorIconURI)
				.setDescription(createProgressBar(userProgress));
	}
	
	private Consumer<EmbedCreateSpec> formatMessageEmbed(UserProgress userProgress, Member user, Role role) {
		return formatMessageEmbed(userProgress, user)
				.andThen(spec -> spec.setColor(role.getColor()));
	}
	
	private String createProgressBar(UserProgress userProgress) {
		boolean hasAnyBadge = userProgress.getCurrentHighestBadge() != null;
		boolean hasBadgeToEarn = userProgress.getNextBadge() != null;

		String barTitle, barFooter;
		int percentage;
		
		if(hasAnyBadge && hasBadgeToEarn) {
			percentage = calculatePercentage(userProgress);
			barTitle = String.format(" %s ==► %s ", userProgress.getCurrentHighestBadge().getTitle(), userProgress.getNextBadge().getTitle());
			barFooter = String.format(" %d/%d points (%d%%) ", userProgress.getCurrentPoints(), userProgress.getNextBadge().getPointThreshold(), percentage);
		} else if (!hasAnyBadge && hasBadgeToEarn) {
			percentage = calculatePercentage(userProgress);
			barTitle = String.format(" Progress to %s ", userProgress.getNextBadge().getTitle());
			barFooter = String.format(" %d/%d points (%d%%) ", userProgress.getCurrentPoints(), userProgress.getNextBadge().getPointThreshold(), percentage);
		} else if (hasAnyBadge && !hasBadgeToEarn) {
			barTitle = " You've earned every badge! ";
			barFooter = String.format(" %s points ", userProgress.getCurrentPoints());
			percentage = 100;
		} else {
			barTitle = " No progress to report ";
			barFooter = String.format(" %s points ", userProgress.getCurrentPoints());
			percentage = 0;
		}
		
		return formatProgressBar(barTitle, barFooter, percentage);
	}
	
	private String formatProgressBar(String barTitle, String barFooter, int percent)
	{
		String bar = createBar(percent);
		return createBarFrame(barTitle, barFooter, bar);
	}
	
	private String createBar(int percent)
	{
		int maxBarLength = 32;
		int itr;
		int barLength = (int)(maxBarLength * ((double)percent / 100.0));
		StringBuilder builder = new StringBuilder();
		
		for(itr = 0; itr < barLength; itr++)
			builder.append("█");
		
		if(barLength < maxBarLength)
		{
			builder.append("►");
			itr++;
		}
		
		for(; itr < maxBarLength; itr++)
			builder.append(" ");
		
		
		return builder.toString();
	}
	
	private String createBarFrame(String barTitle, String barFooter, String bar) {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("`╔%s╗`\n", StringUtils.center(barTitle, bar.length() + 2, "═")));
		builder.append("`╠╣"+ bar +"╠╣`\n");
		builder.append(String.format("`╚%s╝`", StringUtils.center(barFooter, bar.length() + 2, "═")));
		
		return builder.toString();
	}
	
	private int calculatePercentage(UserProgress userProgress) {
		int currentPoints = userProgress.getCurrentPoints();
		int neededPoints = userProgress.getNextBadge().getPointThreshold();
		return ((int)Math.floor(100.0 * currentPoints / neededPoints));
	}
	
}
