package skaro.frenbot.receivers.services;

import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.dtos.NewAwardsDTO;

public interface PokeAimPIService {

	Mono<NewAwardsDTO> addPointsToUser(Member user, int points);
	
}
