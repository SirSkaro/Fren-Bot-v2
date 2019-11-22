package skaro.frenbot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.PrivateChannel;
import reactor.core.publisher.Mono;
import skaro.frenbot.invokers.MessageCreateInvoker;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;

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
				.filterWhen(message -> message.getChannel().map(channel -> !(channel instanceof PrivateChannel)))
				.flatMap(message -> invoker.respond(message))
				.onErrorResume(throwable -> Mono.empty())
				.subscribe(event -> System.out.println("event handled"));
	}
	
	@Bean
	@Order(value = Ordered.LOWEST_PRECEDENCE)
	public CommandLineRunner registerMemberJoinListener(DiscordClient discordClient, DiscordService discordService, PokeAimPIService apiService) {
		return args -> discordClient.getEventDispatcher().on(MemberJoinEvent.class)
				.map(event -> event.getMember())
				.flatMap(member -> apiService.getUserBadges(member)
						.map(award -> award.getBadge())
						.collectList()
						.flatMap(badges -> discordService.assignBadgeRoles(member, badges)))
				.onErrorResume(throwable -> Mono.empty())
				.subscribe(arg -> System.out.println("member join handled"));
	}
	
	@Bean
	@Order(value = Ordered.HIGHEST_PRECEDENCE)
	public CommandLineRunner login(DiscordClient discordClient) throws Exception {
		return args -> discordClient.login().block(); 
	}
	
}
