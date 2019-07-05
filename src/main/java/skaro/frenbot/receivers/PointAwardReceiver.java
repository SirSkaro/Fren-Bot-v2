package skaro.frenbot.receivers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.arguments.PointAmountArgument;
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
		
		PointAmountArgument pointAmount = (PointAmountArgument)argument;
		
		PointsDTO pointsToReward = DTOBuilder.of(PointsDTO::new)
				.with(PointsDTO::setAmount, pointAmount.getAmount())
				.build();
		
		String endpoint = String.format("%s/user/discord/%d/points/add", baseURI, message.getAuthor().get().getId().asLong());
		
		return Mono.just(restTemplate.postForObject(endpoint, pointsToReward, NewAwardsDTO.class))
				.doOnNext(awards -> System.out.println(awards.getUser().getSocialProfile().getDiscordConnection().getDiscordId() 
						+ " now has " + awards.getUser().getPoints() + " points"))
				.flatMap(amount -> Mono.empty());
	}

}
