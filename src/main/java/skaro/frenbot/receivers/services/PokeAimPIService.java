package skaro.frenbot.receivers.services;

import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.dtos.NewAwardsDTO;
import skaro.frenbot.receivers.dtos.PointsDTO;

public interface PokeAimPIService {

	Mono<NewAwardsDTO> addPointsToUser(Member user, PointsDTO points);
	
}
