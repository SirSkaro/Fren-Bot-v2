package skaro.frenbot.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import reactor.core.publisher.Mono;
import skaro.frenbot.aspects.RequireDiscordPermission;
import skaro.frenbot.commands.arguments.PointAwardArgument;
import skaro.frenbot.commands.parsers.ObjectParser;
import skaro.frenbot.receivers.Receiver;

@RequireDiscordPermission(permission = Permission.ADMINISTRATOR)
public class PointAwardCommand implements Command {

	private Receiver receiver;
	@Autowired
	private ObjectParser parser;
	
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
