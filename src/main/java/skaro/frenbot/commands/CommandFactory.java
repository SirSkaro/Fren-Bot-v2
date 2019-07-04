package skaro.frenbot.commands;

import java.util.Optional;

import skaro.frenbot.commands.parsers.ParsedText;

public interface CommandFactory {

	Optional<Command> getCommandFor(ParsedText parsedText) throws Exception;

}