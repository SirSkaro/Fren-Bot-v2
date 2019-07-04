package skaro.frenbot.commands.impl;

import java.util.List;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.Command;
import skaro.frenbot.receivers.Receiver;

public class PingCommand implements Command {

	private Receiver receiver;
	
	@Override
	public Mono<Message> execute(Message message) {
		return receiver.process(null, message);
	}

	@Override
	public void setArguments(List<String> arguments) {

	}

	@Override
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

}
