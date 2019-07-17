package skaro.frenbot.aspects;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.services.DiscordService;

@Aspect
@Component
public class PermissionAspect {
	
	@Autowired
	DiscordService discordService;

	@Around("execution(reactor.core.publisher.Mono<discord4j.core.object.entity.Message> (@skaro.frenbot.aspects.RequireDiscordPermission skaro.frenbot.commands.*Command).execute(..))")
	public Object requireDiscordPermission(ProceedingJoinPoint joinPoint) throws Throwable {
		RequireDiscordPermission requiredPermission = getAnnotation(joinPoint);
		Permission permission = requiredPermission.permission();
		Message userMessage = (Message)joinPoint.getArgs()[0];
		
		return Mono.just(userMessage)
			.filterWhen(message -> discordService.authorHasPermission(message, permission))
			.flatMap(message -> proceed(joinPoint))
			.switchIfEmpty(sendPermissionDeniedMessage(userMessage, permission));
	}
	
	private RequireDiscordPermission getAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)(joinPoint.getSignature());
        Method method = signature.getMethod();
        Class<?> declaringClass = method.getDeclaringClass();
        return declaringClass.getAnnotation(RequireDiscordPermission.class);
	}
	
	@SuppressWarnings("unchecked")
	private Mono<Message> proceed(ProceedingJoinPoint joinPoint) {
		try {
			Object result = joinPoint.proceed(joinPoint.getArgs());
			return (Mono<Message>)result;
		} catch (Throwable e) {
			return Mono.empty();
		}
	}
	
	private Mono<Message> sendPermissionDeniedMessage(Message message, Permission permission) {
		Consumer<EmbedCreateSpec> embedSpec = spec -> spec.setColor(Color.RED)
				.setTitle(":no_entry: You need " + permission.name() + " permissions for this command");
		
		Consumer<MessageCreateSpec> messageSpec = (MessageCreateSpec spec) -> spec.setEmbed(embedSpec);
		return discordService.replyToMessage(message, messageSpec);
	}
	
}
