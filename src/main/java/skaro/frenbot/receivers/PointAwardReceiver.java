package skaro.frenbot.receivers;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.arguments.PointAmountArgument;

public class PointAwardReceiver implements Receiver {

	@Override
	public Mono<Message> process(Argument argument, Message message) {
		PointAmountArgument amount = (PointAmountArgument)argument;
		System.out.println("rewarded " + amount.getAmount() + " points");
		return Mono.empty();
	}

}
