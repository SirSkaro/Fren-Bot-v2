package skaro.frenbot.commands.impl;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.PointRewardCommand;
import skaro.frenbot.commands.arguments.PointAmountArgument;
import skaro.frenbot.receivers.Receiver;

public class FixedRewardCommand implements PointRewardCommand {

	private Receiver receiver;

	@Override
	public Mono<Message> rewardPointsForMessage(Message message) {
		return message.getAuthor()
				.map(author -> author.getId().asString())
				.map(id -> new PointAmountArgument(1, id))
				.map(pointAmount -> receiver.process(pointAmount, message))
				.orElse(Mono.empty());
	}
	
	@Override
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

}
