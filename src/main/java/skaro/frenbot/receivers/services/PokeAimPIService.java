package skaro.frenbot.receivers.services;

import java.util.List;
import java.util.Optional;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokeaimpi.sdk.resource.Badge;
import skaro.pokeaimpi.sdk.resource.BadgeAwardRecord;
import skaro.pokeaimpi.sdk.resource.NewAwardList;
import skaro.pokeaimpi.sdk.resource.UserProgress;

public interface PokeAimPIService {

	Mono<NewAwardList> addPointsToUser(Member user, int points);
	Mono<UserProgress> getUserProgress(Member user);
	Flux<BadgeAwardRecord> getUserBadges(Member user);
	Mono<BadgeAwardRecord> awardBadge(Member user, Role role);
	Mono<List<BadgeAwardRecord>> getAllAwards();
	Optional<Badge> getMostValuedRankedBadge(List<Badge> badges);
	Mono<Badge> getBadge(Long roleId);
	Flux<Badge> getBadges();
	
}
