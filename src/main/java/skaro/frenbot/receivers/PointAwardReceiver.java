package skaro.frenbot.receivers;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.arguments.PointAmountArgument;
import skaro.frenbot.receivers.dtos.BadgeDTO;
import skaro.frenbot.receivers.dtos.DTOBuilder;
import skaro.frenbot.receivers.dtos.NewAwardsDTO;
import skaro.frenbot.receivers.dtos.PointsDTO;

public class PointAwardReceiver implements Receiver {
	
	@Autowired
	private RestTemplate restTemplate;
	@Value("${pokeaimpi.base}")
	private String baseURI;
	
	@Override
	public Mono<Message> process(Argument argument, Message message) {
		PointsDTO pointsToReward = createRequest((PointAmountArgument)argument);
		
		return Mono.justOrEmpty(message.getAuthor())
				.map(author -> addPointsToUser(author, pointsToReward))
				.filter(newAwards -> !newAwards.getBadges().isEmpty())
				.flatMap(newAwards -> createAwardMessage(newAwards, message))
				.flatMap(messageSpec -> message.getChannel()
						.flatMap(channel -> channel.createMessage(messageSpec)));
	}
	
	private NewAwardsDTO addPointsToUser(User author, PointsDTO pointsToReward) {
		String endpoint = constructEndpoint(author);
		return restTemplate.postForObject(endpoint, pointsToReward, NewAwardsDTO.class);
	}
	
	private PointsDTO createRequest(PointAmountArgument pointsToReward) {
		return DTOBuilder.of(PointsDTO::new)
				.with(PointsDTO::setAmount, pointsToReward.getAmount())
				.build();
	}
	
	private String constructEndpoint(User user) {
		return String.format("%s/user/discord/%d/points/add", baseURI, user.getId().asLong());
	}
	
	private Mono<Consumer<MessageCreateSpec>> createAwardMessage(NewAwardsDTO newAwards, Message message) {
		List<BadgeDTO> sortedBadges = sortAscending(newAwards.getBadges());
		BadgeDTO mostValuedBadge = sortedBadges.get(sortedBadges.size() - 1);
		
		return message.getGuild()
				.flatMap(guild -> getRoleForBadge(mostValuedBadge, guild)
						.map(role -> createAwardEmbed(sortedBadges, message.getAuthor().get(), role)))
				.map(embedConsumer -> (MessageCreateSpec spec) -> spec.setEmbed(embedConsumer));
	}
	
	private Consumer<EmbedCreateSpec> createAwardEmbed(List<BadgeDTO> sortedBadges, User author, Role role) {
		BadgeDTO mostValuedBadge = sortedBadges.get(sortedBadges.size() - 1);
		
		Consumer<EmbedCreateSpec> embedSpec = spec -> spec.setThumbnail(mostValuedBadge.getImageUri());
		embedSpec = embedSpec.andThen(spec -> spec.setDescription(createDescription(sortedBadges, author)));
		embedSpec = embedSpec.andThen(spec -> spec.setTitle((sortedBadges.size() > 1 ? "Badges Awarded" : "Badge Awarded") + "! :tada:"));
		embedSpec = embedSpec.andThen(spec -> spec.setColor(role.getColor()));
		
		return embedSpec;
	}
	
	private List<BadgeDTO> sortAscending(List<BadgeDTO> badges) {
		badges.sort( (BadgeDTO badge1, BadgeDTO badge2) -> badge1.getPointThreshold().compareTo(badge2.getPointThreshold()) );
		return badges;
	}
	
	private String createDescription(List<BadgeDTO> badges, User user) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Congratulations "+ user.getUsername() +"! You've earned:");
		for (BadgeDTO badge : badges) {
			builder.append("\n:small_orange_diamond:");
			builder.append(String.format("the **__%s__** badge for earning **__%d__** points", badge.getTitle(), badge.getPointThreshold()));
		}
		
		return builder.toString();
	}
	
	private Mono<Role> getRoleForBadge(BadgeDTO badge, Guild guild) {
		return guild.getRoleById(Snowflake.of(badge.getDiscordRoleId()));
	}

}
