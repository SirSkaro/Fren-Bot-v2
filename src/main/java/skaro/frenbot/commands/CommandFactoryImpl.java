package skaro.frenbot.commands;

import java.util.Map;
import java.util.Optional;

public class CommandFactoryImpl implements CommandFactory {

	private Map<String, Command> allCommands;
	
	public CommandFactoryImpl(Map<String, Command> availableCommands) {
		allCommands = availableCommands;
	}
	
	@Override
	public Optional<Command> getCommand(String commandName) {
		if(!allCommands.containsKey(commandName)) {
			return Optional.empty();
		}
		
		Command command = allCommands.get(commandName);
		return Optional.of(command);
	}
	
}
