package skaro.frenbot.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import skaro.frenbot.commands.parsers.ParsedText;

public class CommandFactory {

	private Map<String, Class<Command>> commands;
	
	public CommandFactory(List<Class<Command>> supportedCommands) {
		commands = new HashMap<>();
		
		for (Class<Command> command : supportedCommands) {
			commands.put(command.getName(), command);
		}
	}
	
	public Optional<Command> createCommand(ParsedText parsedText) throws Exception {
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
