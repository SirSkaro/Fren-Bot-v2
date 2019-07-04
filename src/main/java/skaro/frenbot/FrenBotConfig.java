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
import skaro.frenbot.commands.CommandFactory;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.parsers.ArgumentParser;
import skaro.frenbot.commands.parsers.ObjectParser;
import skaro.frenbot.commands.parsers.RegexParser;
import skaro.frenbot.commands.parsers.TextParser;

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
	public List<Class<Command>> getAllCommands() {
		List<Class<Command>> commands = new ArrayList<>();
		return commands;
	}
	
	@Bean
	@Autowired
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public CommandFactory getCommandLibrary(List<Class<Command>> allCommands) {
		return new CommandFactory(allCommands);
	}
	
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ObjectParser<Argument> getArgumentParser() {
		return new ArgumentParser();
	}
	
	@Autowired
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TextParser getTextParser(@Value("${discord.prefix}") String prefix) {
		return new RegexParser(prefix);
	}
	
}
