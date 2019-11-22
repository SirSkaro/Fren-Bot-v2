package skaro.frenbot.aspects;

import java.awt.Color;
import java.util.function.Consumer;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.Forbidden;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.services.DiscordService;

@Aspect
@Component
public class ReactorReceiverErrorHandleAspect {
	
	@Autowired
	private DiscordService discordService;

	@SuppressWarnings("unchecked")
	@Around("execution(reactor.core.publisher.Mono<discord4j.core.object.entity.Message> skaro.frenbot.receivers.*Receiver.process(..))")
	public Object APIServiceExceptionAdvice (ProceedingJoinPoint joinPoint) throws Throwable {
		Message userMessage = (Message)joinPoint.getArgs()[1];
		Mono<Message> apiResponse = (Mono<Message>)joinPoint.proceed();
		return apiResponse.onErrorResume(error -> createAPIErrorMessage(userMessage, error));
	}
	
	private Mono<Message> createAPIErrorMessage(Message userMessage, Throwable error) {
		Consumer<MessageCreateSpec> messageSpec;
		
		if(isApiRejection(error)) {
			messageSpec = createBadRequestErrorMessage();
		} else {
			messageSpec = createGenericErrorMessage(error);
		}
		
		error.printStackTrace();
		return discordService.replyToMessage(userMessage, messageSpec);
	}
	
	private boolean isApiRejection(Throwable error) {
		return (error instanceof BadRequest)
				|| (error instanceof Forbidden);
	}
	
	private Consumer<MessageCreateSpec> createBadRequestErrorMessage() {
		Consumer<EmbedCreateSpec> embedSpec = (EmbedCreateSpec spec) -> spec.setTitle(":no_entry: Request Denied")
				.setDescription("You may not make this request. Check documentation for more information.")
				.setColor(Color.RED);
		return (MessageCreateSpec spec) -> spec.setEmbed(embedSpec);
	}
	
	private Consumer<MessageCreateSpec> createGenericErrorMessage(Throwable error) {
		Consumer<EmbedCreateSpec> embedSpec = (EmbedCreateSpec spec) -> spec.setTitle(":thermometer_face: Uh oh! An external error occured")
				.setDescription("Please report to an admin that a "+ error.getClass().getSimpleName() + " occured")
				.setColor(Color.BLACK);
		return (MessageCreateSpec spec) -> spec.setEmbed(embedSpec);
	}
	
}
