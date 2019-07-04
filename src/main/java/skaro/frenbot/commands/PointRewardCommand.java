package skaro.frenbot.commands;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.Receiver;

public interface PointRewardCommand {

	Mono<Message> rewardPointsForMessage(Message message);
	void setReceiver(Receiver receiver);
	
}
