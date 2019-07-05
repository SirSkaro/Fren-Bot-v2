package skaro.frenbot.invokers;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.CommandFactory;
import skaro.frenbot.commands.PointRewardCommand;
import skaro.frenbot.commands.parsers.TextParser;

public class MessageCreateInvoker implements Invoker {

	@Autowired
	CommandFactory factory;
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
				.flatMap(parsedText -> factory.getCommand(parsedText.getCommand())
						.map(command -> command.execute(message, parsedText.getArgumentsList())))
				.orElseGet(Mono::empty);
		
	}
	

}
