package skaro.frenbot.receivers;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.arguments.PointAmountArgument;

public class PointAwardReceiver implements Receiver {

	@Override
	public Mono<Message> process(Argument argument, Message message) {
		return Mono.just(argument)
				.cast(PointAmountArgument.class)
				.doOnNext(amount -> System.out.println("rewarded " + amount.getAmount() + " points"))
				.flatMap(amount -> Mono.empty());
	}

}
