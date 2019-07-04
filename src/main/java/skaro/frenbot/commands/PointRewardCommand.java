package skaro.frenbot.commands;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface PointRewardCommand {

	Mono<Message> rewardPointsForMessage(Message message);
	
}
