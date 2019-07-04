package skaro.frenbot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import skaro.frenbot.commands.Command;
import skaro.frenbot.commands.CommandLibrary;
import skaro.frenbot.commands.parsers.ArgumentParser;

@Configuration
public class FrenBotConfig {

	@Bean
	public RestTemplate getRestTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	@Autowired
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public DiscordClient getDiscordClient(@Value("${discord.token}") String token) {
		DiscordClient client = new DiscordClientBuilder(token).build();

		client.getEventDispatcher().on(ReadyEvent.class)
		.subscribe(ready -> System.out.println("Logged in as " + ready.getSelf().getUsername()));

		client.getEventDispatcher().on(MessageCreateEvent.class)
		.subscribe(event -> {
			Message message = event.getMessage();
			if (message.getContent().map("!ping"::equals).orElse(false)) {
				message.getChannel().block().createMessage("Pong!").block();
			}
		});

		return client;
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public List<Command> getAllCommands() {
		List<Command> commands = new ArrayList<>();
		return commands;
	}
	
	@Bean
	@Autowired
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public CommandLibrary getCommandLibrary(List<Command> allCommands) {
		return new CommandLibrary(allCommands);
	}
	
	@Autowired
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ArgumentParser getArgumentParser(@Value("${discord.prefix}") String prefix) {
		return new ArgumentParser(prefix);
	}
	
}
