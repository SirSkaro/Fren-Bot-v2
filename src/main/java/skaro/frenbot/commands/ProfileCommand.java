package skaro.frenbot.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.arguments.UserArgument;
import skaro.frenbot.commands.parsers.ObjectParser;
import skaro.frenbot.receivers.Receiver;

public class ProfileCommand implements Command {

	private Receiver receiver;
	@Autowired
	private ObjectParser parser;
	
	@Override
	public Mono<Message> execute(Message message, List<String> arguments) {
		Argument userToView = inferUserArgument(message, arguments);
		return receiver.process(userToView, message);
	}

	@Override
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	private Argument inferUserArgument(Message message, List<String> arguments) {
		if(arguments.isEmpty()) {
			User author = message.getAuthor().get();
			return new UserArgument(author.getMention());
		} else {
			return parser.parse(arguments, UserArgument.class);
		}
	}
	
}
