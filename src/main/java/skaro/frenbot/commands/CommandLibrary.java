package skaro.frenbot.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandLibrary {

	private Map<String, Command> commands;
	
	public CommandLibrary(List<Command> supportedCommands) {
		commands = new HashMap<>();
		
		for (Command command : supportedCommands) {
			commands.put(command.getName(), command);
		}
	}
	
	public Optional<Command> getCommand(String commandName) {
		return Optional.ofNullable(commands.get(commandName));
	}
	
}
