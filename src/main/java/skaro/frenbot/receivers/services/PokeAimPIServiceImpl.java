package skaro.frenbot.receivers.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.dtos.BadgeAwardDTO;
import skaro.frenbot.receivers.dtos.BadgeDTO;
import skaro.frenbot.receivers.dtos.DTOBuilder;
import skaro.frenbot.receivers.dtos.NewAwardsDTO;
import skaro.frenbot.receivers.dtos.PointsDTO;
import skaro.frenbot.receivers.dtos.UserProgressDTO;

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
		NewAwardsDTO result = restTemplate.postForObject(endpoint, request, NewAwardsDTO.class);
		return Mono.just(result)
				.doOnNext(newAwards -> newAwards.getBadges().sort((BadgeDTO badge1, BadgeDTO badge2) -> badge1.getPointThreshold().compareTo(badge2.getPointThreshold())));
	}

	@Override
	public Mono<UserProgressDTO> getUserProgress(Member user) {
		String endpoint = String.format("%s/user/discord/%d/progress", baseURI, user.getId().asLong());
		UserProgressDTO result = restTemplate.getForObject(endpoint, UserProgressDTO.class);
		return Mono.just(result);
	}

	@Override
	public Flux<BadgeAwardDTO> getUserBadges(Member user) {
		String endpoint = String.format("%s/award?userDiscordId=%d", baseURI, user.getId().asLong());
		ResponseEntity<List<BadgeAwardDTO>> result = restTemplate.exchange(endpoint, HttpMethod.GET, null, new ParameterizedTypeReference<List<BadgeAwardDTO>>() {});
		
		return Flux.fromIterable(result.getBody());
	}

	@Override
	public Mono<BadgeAwardDTO> awardBadge(Member user, Role role) {
		String endpoint = String.format("%s/award/discord/user/%d/role/%d", baseURI, user.getId().asLong(), role.getId().asLong());
		BadgeAwardDTO result = restTemplate.postForObject(endpoint, null, BadgeAwardDTO.class);
		return Mono.just(result);
	}

	@Override
	public Mono<List<BadgeAwardDTO>> getAllAwards() {
		String endpoint = String.format("%s/award", baseURI);
		ResponseEntity<List<BadgeAwardDTO>> result = restTemplate.exchange(endpoint, HttpMethod.GET, null, new ParameterizedTypeReference<List<BadgeAwardDTO>>() {});
		return Mono.just(result.getBody());
	}
	
	@Override
	public Optional<BadgeDTO> getMostValuedRankedBadge(List<BadgeDTO> badges) {
		return badges.stream().max(Comparator.comparing(BadgeDTO::getPointThreshold));
	}

}
