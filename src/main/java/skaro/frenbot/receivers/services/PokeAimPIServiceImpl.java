package skaro.frenbot.receivers.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.dtos.BadgeDTO;
import skaro.frenbot.receivers.dtos.DTOBuilder;
import skaro.frenbot.receivers.dtos.NewAwardsDTO;
import skaro.frenbot.receivers.dtos.PointsDTO;

@Service
public class PokeAimPIServiceImpl implements PokeAimPIService {

	@Value("${pokeaimpi.base}")
	private String baseURI;
	@Autowired 
	private RestTemplate restTemplate;
	
	@Override
	public Mono<NewAwardsDTO> addPointsToUser(Member user, int points) {
		PointsDTO request = DTOBuilder.of(PointsDTO::new)
				.with(PointsDTO::setAmount, points)
				.build();
		
		String endpoint = String.format("%s/user/discord/%d/points/add", baseURI, user.getId().asLong());
		return Mono.just(restTemplate.postForObject(endpoint, request, NewAwardsDTO.class))
				.doOnNext(newAwards -> newAwards.getBadges().sort((BadgeDTO badge1, BadgeDTO badge2) -> badge1.getPointThreshold().compareTo(badge2.getPointThreshold())));
	}

}
