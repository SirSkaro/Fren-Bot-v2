package skaro.frenbot.receivers.services;

import java.util.List;
import java.util.Optional;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.dtos.BadgeAwardDTO;
import skaro.frenbot.receivers.dtos.BadgeDTO;
import skaro.frenbot.receivers.dtos.NewAwardsDTO;
import skaro.frenbot.receivers.dtos.UserProgressDTO;

public interface PokeAimPIService {

	Mono<NewAwardsDTO> addPointsToUser(Member user, int points);
	Mono<UserProgressDTO> getUserProgress(Member user);
	Flux<BadgeAwardDTO> getUserBadges(Member user);
	Mono<BadgeAwardDTO> awardBadge(Member user, Role role);
	Mono<List<BadgeAwardDTO>> getAllAwards();
	Optional<BadgeDTO> getMostValuedRankedBadge(List<BadgeDTO> badges);
	
}
