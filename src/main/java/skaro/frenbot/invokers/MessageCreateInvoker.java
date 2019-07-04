package skaro.frenbot.invokers;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.CommandLibrary;
import skaro.frenbot.commands.PointRewardCommand;
import skaro.frenbot.commands.parsers.ArgumentParser;

public class MessageCreateInvoker implements Invoker {

	@Autowired
	CommandLibrary library;
	@Autowired
	ArgumentParser parser;
	@Autowired
	PointRewardCommand rewardCommand;
	
	@Override
	public Mono<Message> respond(String input) {
		return respondToCommand(input)
				.switchIfEmpty(rewardCommand.rewardPointsForMessage(input));
	}
	
	private Mono<Message> respondToCommand(String input) {
		return parser.getCommandName(input)
				.flatMap(commandName -> library.getCommand(commandName))
				.map(command -> command.execute(input))
				.orElse(Mono.empty());
	}

}
