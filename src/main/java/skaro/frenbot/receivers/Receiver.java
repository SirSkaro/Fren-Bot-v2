package skaro.frenbot.receivers;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;

public interface Receiver {

	Mono<Message> process(Argument argument);
	
}
