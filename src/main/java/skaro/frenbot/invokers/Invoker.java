package skaro.frenbot.invokers;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface Invoker {

	public Mono<Message> respond(String input);
	
}
