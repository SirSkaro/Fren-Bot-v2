package skaro.frenbot.commands.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import skaro.frenbot.commands.Command;
import skaro.frenbot.commands.CommandFactory;
import skaro.frenbot.commands.parsers.ParsedText;

public class CommandFactoryImpl implements CommandFactory {

	private Map<String, Class<Command>> commands;
	
	public CommandFactoryImpl(Map<String, Class<Command>> supportedCommands) {
		commands = supportedCommands;
	}
	
	@Override
	public Optional<Command> getCommandFor(ParsedText parsedText) throws Exception {
		if(!commands.containsKey(parsedText.getCommand())) {
			return Optional.empty();
		}
		
		Command command = commands.get(parsedText.getCommand()).getConstructor().newInstance();
		command.setArguments(argumentsToList(parsedText.getArguments()));
		return Optional.of(command);
	}
	
	private List<String> argumentsToList(String arguments) {
		return Arrays.asList(arguments.split(" "));
	}
	
}
