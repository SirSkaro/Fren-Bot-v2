package skaro.frenbot.commands;

import java.util.List;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.Receiver;

public interface Command {

	Mono<Message> execute(Message message, List<String> arguments);
	void setReceiver(Receiver receiver);
	
}
