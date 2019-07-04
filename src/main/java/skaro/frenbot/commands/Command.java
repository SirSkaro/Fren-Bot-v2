package skaro.frenbot.commands;

import java.util.List;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface Command {

	Mono<Message> execute();
	void setArguments(List<String> arguments);
	String getName();
	
}
