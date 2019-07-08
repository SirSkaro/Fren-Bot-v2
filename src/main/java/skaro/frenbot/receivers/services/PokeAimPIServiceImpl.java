package skaro.frenbot.receivers.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.dtos.NewAwardsDTO;
import skaro.frenbot.receivers.dtos.PointsDTO;

@Service
public class PokeAimPIServiceImpl implements PokeAimPIService {

	@Value("${pokeaimpi.base}")
	private String baseURI;
	@Autowired 
	private RestTemplate restTemplate;
	
	@Override
	public Mono<NewAwardsDTO> addPointsToUser(Member user, PointsDTO points) {
		String endpoint = String.format("%s/user/discord/%d/points/add", baseURI, user.getId().asLong());
		return Mono.just(restTemplate.postForObject(endpoint, points, NewAwardsDTO.class));
	}

}
