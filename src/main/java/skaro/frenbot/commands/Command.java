package skaro.frenbot.commands;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface Command {

	public Mono<Message> execute(String arguments);
	public String getName();
	
}
