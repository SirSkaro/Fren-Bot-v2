package skaro.frenbot.commands;

import java.util.List;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.Receiver;

public class PingCommand implements Command {

	private Receiver receiver;
	
	@Override
	public Mono<Message> execute(Message message, List<String> arguments) {
		return receiver.process(null, message);
	}

	@Override
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

}
