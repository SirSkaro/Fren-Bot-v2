package skaro.frenbot.invokers;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Message;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.CommandFactory;
import skaro.frenbot.commands.PointRewardCommand;
import skaro.frenbot.commands.parsers.TextParser;

public class MessageCreateInvoker implements Invoker {

	@Autowired
	CommandFactory library;
	@Autowired
	TextParser parser;
	@Autowired
	PointRewardCommand rewardCommand;
	
	@Override
	public Mono<Message> respond(Message message) {
		return respondToRequest(message)
				.switchIfEmpty(rewardCommand.rewardPointsForMessage(message));
	}
	
	private Mono<Message> respondToRequest(Message message) {
		return parser.parseMessageContent(message)
				.flatMap(parsedText -> {
					try { return library.createCommand(parsedText); }
					catch (Exception e) { throw Exceptions.propagate(e); }})
				.map(command -> command.execute())
				.orElse(Mono.empty());
	}

}
