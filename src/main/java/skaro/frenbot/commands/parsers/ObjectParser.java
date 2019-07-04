package skaro.frenbot.commands.parsers;

import java.util.Optional;

public interface ObjectParser<T> {

	public T parse(String[] arguments, Class<T> clazz) throws ParserException;
	public Optional<String> getCommandName(String arguments);
	
}
