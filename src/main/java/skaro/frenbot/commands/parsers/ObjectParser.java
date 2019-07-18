package skaro.frenbot.commands.parsers;

import java.util.List;

import skaro.frenbot.commands.arguments.Argument;

public interface ObjectParser {

	<T extends Argument> T parse(List<String> arguments, Class<T> clazz) throws ParserException;
	
}
