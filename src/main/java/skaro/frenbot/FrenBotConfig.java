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
import discord4j.core.event.domain.message.MessageCreateEvent;
import skaro.frenbot.commands.Command;
import skaro.frenbot.commands.CommandFactory;
import skaro.frenbot.commands.PointRewardCommand;
import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.impl.CommandFactoryImpl;
import skaro.frenbot.commands.impl.FixedRewardCommand;
import skaro.frenbot.commands.impl.PingCommand;
import skaro.frenbot.commands.parsers.ArgumentParser;
import skaro.frenbot.commands.parsers.ObjectParser;
import skaro.frenbot.commands.parsers.RegexParser;
import skaro.frenbot.commands.parsers.TextParser;
import skaro.frenbot.invokers.Invoker;
import skaro.frenbot.invokers.MessageCreateInvoker;
import skaro.frenbot.receivers.PingReceiver;
import skaro.frenbot.receivers.PointAwardReceiver;

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

		Invoker messageCreateInvoker = getMessageCreateInvoker();
		client.getEventDispatcher().on(MessageCreateEvent.class)
		.map(event -> event.getMessage())
		.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
		.flatMap(message -> messageCreateInvoker.respond(message))
		.subscribe(event -> System.out.println("event handled"));

		return client;
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Map<String, Command> getAllCommands() {
		Map<String, Command> commands = new HashMap<>();
		commands.put("ping", this.getPingCommand());
		
		return commands;
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public CommandFactory getCommandLibrary() {
		return new CommandFactoryImpl(getAllCommands());
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ObjectParser<Argument> getArgumentParser() {
		return new ArgumentParser();
	}
	
	@Bean
	@Autowired
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TextParser getTextParser(@Value("${discord.prefix}") String prefix) {
		return new RegexParser(prefix);
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PointRewardCommand getPointRewardCommand() {
		PointRewardCommand command = new FixedRewardCommand();
		command.setReceiver(new PointAwardReceiver());
		return command;
	}
	
	@Bean
	public PingReceiver getPingReceiver() {
		return new PingReceiver();
	}
	
	@Bean
	public PingCommand getPingCommand() {
		PingCommand command = new PingCommand();
		command.setReceiver(getPingReceiver());
		return command;
	}
	
	@Bean
	public MessageCreateInvoker getMessageCreateInvoker() {
		return new MessageCreateInvoker();
	}
	
}
