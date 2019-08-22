package skaro.frenbot.commands;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.PointAwardArgument;
import skaro.frenbot.receivers.Receiver;

public class FixedRewardCommand implements PointListenerCommand {

	private Receiver receiver;

	@Override
	public Mono<Message> rewardPointsForMessage(Message message) {
		return message.getAuthor()
				.map(author -> author.getId().asString())
				.map(id -> new PointAwardArgument(1, id))
				.map(pointAmount -> receiver.process(pointAmount, message))
				.orElse(Mono.empty());
	}
	
	@Override
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

}
