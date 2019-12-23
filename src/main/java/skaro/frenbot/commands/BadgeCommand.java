package skaro.frenbot.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.BadgeArgument;
import skaro.frenbot.commands.parsers.ObjectParser;
import skaro.frenbot.receivers.Receiver;

public class BadgeCommand implements Command {

	private Receiver reciver;
	@Autowired
	private ObjectParser parser;
	
	@Override
	public Mono<Message> execute(Message message, List<String> arguments) {
		BadgeArgument argument = parser.parse(arguments, BadgeArgument.class);
		return reciver.process(argument, message);
	}

	@Override
	public void setReceiver(Receiver receiver) {
		this.reciver = receiver;
	}

}
