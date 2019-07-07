package skaro.frenbot.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.PointAwardArgument;
import skaro.frenbot.commands.parsers.ObjectParser;
import skaro.frenbot.receivers.Receiver;

public class PointAwardCommand implements Command {

	private Receiver receiver;
	@Autowired
	ObjectParser parser;
	
	@Override
	public Mono<Message> execute(Message message, List<String> arguments) {
		PointAwardArgument argument = parser.parse(arguments, PointAwardArgument.class);
		return receiver.process(argument, message);
	}

	@Override
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

}
