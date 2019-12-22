package skaro.frenbot;

import java.util.HashMap;
import java.util.Map;

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
import skaro.frenbot.commands.Command;
import skaro.frenbot.commands.CommandFactory;
import skaro.frenbot.commands.CommandFactoryImpl;
import skaro.frenbot.commands.parsers.ArgumentParser;
import skaro.frenbot.commands.parsers.ObjectParser;
import skaro.frenbot.commands.parsers.RegexParser;
import skaro.frenbot.commands.parsers.TextParser;
import skaro.frenbot.invokers.MessageCreateInvoker;

@Configuration
public class FrenBotConfig {
	
	@Autowired
	CommandConfig commandConfig;

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

		return client;
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Map<String, Command> getAllCommands() {
		Map<String, Command> commands = new HashMap<>();
		commands.put("ping", commandConfig.getPingCommand());
		commands.put("preward", commandConfig.getPointAwardCommand());
		commands.put("progress", commandConfig.getProgressCommand());
		commands.put("rps", commandConfig.getRPSCommand());
		commands.put("breward", commandConfig.getBadgeAwardCommand());
		commands.put("profile", commandConfig.getProfileCommand());
		
		return commands;
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public CommandFactory getCommandLibrary() {
		return new CommandFactoryImpl(getAllCommands());
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ObjectParser getArgumentParser() {
		return new ArgumentParser();
	}
	
	@Bean
	@Autowired
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TextParser getTextParser(@Value("${discord.prefix}") String prefix) {
		return new RegexParser(prefix);
	}
	
	@Bean
	public MessageCreateInvoker getMessageCreateInvoker() {
		return new MessageCreateInvoker();
	}
	
}
