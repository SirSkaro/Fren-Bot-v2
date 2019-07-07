package skaro.frenbot.commands.parsers;

import java.util.List;

public interface ObjectParser<T> {

	T parse(List<String> arguments, Class<T> clazz) throws ParserException;
	
}
