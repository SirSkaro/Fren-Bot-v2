package skaro.frenbot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;
import skaro.frenbot.invokers.MessageCreateInvoker;

@SpringBootApplication
public class FrenBot {

	public static void main(String[] args) {
		SpringApplication.run(FrenBot.class, args);
	}

	@Bean
	@Order(value = Ordered.LOWEST_PRECEDENCE)
	public CommandLineRunner registerEventCreateListener(DiscordClient discordClient, MessageCreateInvoker invoker) throws Exception {
		return args -> discordClient.getEventDispatcher().on(MessageCreateEvent.class)
				.map(event -> event.getMessage())
				.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
				.flatMap(message -> invoker.respond(message))
				.onErrorResume(throwable -> Mono.empty())
				.subscribe(event -> System.out.println("event handled"));
	}
	
	@Bean
	@Order(value = Ordered.HIGHEST_PRECEDENCE)
	public CommandLineRunner login(DiscordClient discordClient) throws Exception {
		return args -> discordClient.login().block(); 
		
	}
	
}
