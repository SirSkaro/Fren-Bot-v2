package skaro.frenbot.receivers;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;

public class PingReceiver implements Receiver {

	@Override
	public Mono<Message> process(Argument argument, Message message) {
		return message.getChannel()
				.flatMap(channel -> channel.createMessage("Pong!"));
	}

}
