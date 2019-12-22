package skaro.frenbot.aspects;

import java.awt.Color;
import java.util.function.Consumer;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;
import skaro.frenbot.commands.arguments.UsageHelp;
import skaro.frenbot.commands.parsers.FlagDescriptionPair;
import skaro.frenbot.commands.parsers.ParserException;
import skaro.frenbot.receivers.services.DiscordService;

@Aspect
@Component
public class ReactorCommandErrorHandleAspect {

	@Autowired
	private DiscordService discordService;
	
	@Around("execution(reactor.core.publisher.Mono<discord4j.core.object.entity.Message> skaro.frenbot.commands.*Command.execute(..))")
	public Object parseExceptionAdvice (ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			return joinPoint.proceed();
		} catch(ParserException parserException) {
			Message userMessage = (Message)joinPoint.getArgs()[0];
			return createUsageHelpMessage(userMessage, parserException);
		}
	}
	
	private Mono<Message> createUsageHelpMessage(Message message, ParserException parserException) {
		Consumer<MessageCreateSpec> messageSpec = (MessageCreateSpec spec) -> spec.setEmbed(composeEmbed(parserException.getUsageHint()));
		return discordService.replyToMessage(message, messageSpec);
	}
	
	private Consumer<EmbedCreateSpec> composeEmbed(UsageHelp usage) {
		StringBuilder exampleUsage = new StringBuilder("example: __command ");
		Consumer<EmbedCreateSpec> embedSpec = spec -> spec.setColor(Color.RED)
				.setTitle(":x: Incorrect command usage");
		
		for(FlagDescriptionPair pair : usage.getFlags()) {
			embedSpec = embedSpec.andThen(spec -> spec.addField(pair.getFlag(), pair.getDescription(), false));
			exampleUsage.append(pair.getFlag() + " ");
		}
		
		return embedSpec.andThen(spec -> spec.setDescription(exampleUsage.toString() + "__"));
	}
	
}
