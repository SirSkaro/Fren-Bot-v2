package skaro.frenbot.aspects;

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
		Consumer<EmbedCreateSpec> embedSpec = (EmbedCreateSpec spec) -> spec.setTitle(":thermometer_face: Uh oh! An external error occured")
					.setDescription("Tell Sir Skaro that a "+ error.getClass().getSimpleName() + " occured - even though it's Mort's fault");
		Consumer<MessageCreateSpec> messageSpec = (MessageCreateSpec spec) -> spec.setEmbed(embedSpec);
		
		return discordService.replyToMessage(userMessage, messageSpec);
	}
	
}
