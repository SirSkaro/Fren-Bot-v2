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
import skaro.frenbot.receivers.dtos.DTOBuilder;
import skaro.pokeaimpi.sdk.request.PointAmount;
import skaro.pokeaimpi.sdk.resource.Badge;
import skaro.pokeaimpi.sdk.resource.BadgeAwardRecord;
import skaro.pokeaimpi.sdk.resource.NewAwardList;
import skaro.pokeaimpi.sdk.resource.UserProgress;

@Service
public class PokeAimPIServiceImpl implements PokeAimPIService {

	@Value("${pokeaimpi.base}")
	private String baseURI;
	@Autowired 
	private RestTemplate restTemplate;
	
	@Override
	public Mono<NewAwardList> addPointsToUser(Member user, int points) {
		PointAmount request = DTOBuilder.of(PointAmount::new)
				.with(PointAmount::setAmount, points)
				.build();
		
		String endpoint = String.format("%s/user/discord/%d/points/add", baseURI, user.getId().asLong());
		NewAwardList result = restTemplate.postForObject(endpoint, request, NewAwardList.class);
		return Mono.just(result)
				.doOnNext(newAwards -> newAwards.getBadges().sort((Badge badge1, Badge badge2) -> badge1.getPointThreshold().compareTo(badge2.getPointThreshold())));
	}

	@Override
	public Mono<UserProgress> getUserProgress(Member user) {
		String endpoint = String.format("%s/user/discord/%d/progress", baseURI, user.getId().asLong());
		UserProgress result = restTemplate.getForObject(endpoint, UserProgress.class);
		return Mono.just(result);
	}

	@Override
	public Flux<BadgeAwardRecord> getUserBadges(Member user) {
		String endpoint = String.format("%s/award?userDiscordId=%d", baseURI, user.getId().asLong());
		ResponseEntity<List<BadgeAwardRecord>> result = restTemplate.exchange(endpoint, HttpMethod.GET, null, new ParameterizedTypeReference<List<BadgeAwardRecord>>() {});
		
		return Flux.fromIterable(result.getBody());
	}

	@Override
	public Mono<BadgeAwardRecord> awardBadge(Member user, Role role) {
		String endpoint = String.format("%s/award/discord/user/%d/role/%d", baseURI, user.getId().asLong(), role.getId().asLong());
		BadgeAwardRecord result = restTemplate.postForObject(endpoint, null, BadgeAwardRecord.class);
		return Mono.just(result);
	}

	@Override
	public Mono<List<BadgeAwardRecord>> getAllAwards() {
		String endpoint = String.format("%s/award", baseURI);
		ResponseEntity<List<BadgeAwardRecord>> result = restTemplate.exchange(endpoint, HttpMethod.GET, null, new ParameterizedTypeReference<List<BadgeAwardRecord>>() {});
		return Mono.just(result.getBody());
	}
	
	@Override
	public Optional<Badge> getMostValuedRankedBadge(List<Badge> badges) {
		return badges.stream().max(Comparator.comparing(Badge::getPointThreshold));
	}

	@Override
	public Mono<Badge> getBadge(Long roleId) {
		String endpoint = String.format("%s/badge/discord/%d", baseURI, roleId);
		try {
			ResponseEntity<Badge> result = restTemplate.getForEntity(endpoint, Badge.class);
			return Mono.just(result.getBody());
		} catch(Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Flux<Badge> getBadges() {
		String endpoint = String.format("%s/badge", baseURI);
		ResponseEntity<List<Badge>> result = restTemplate.exchange(endpoint, HttpMethod.GET, null, new ParameterizedTypeReference<List<Badge>>() {});
		return Flux.fromIterable(result.getBody());
	}

}
