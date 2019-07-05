package skaro.frenbot.commands;

import java.util.Optional;

public interface CommandFactory {

	Optional<Command> getCommand(String commandName);

}