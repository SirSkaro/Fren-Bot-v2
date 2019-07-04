package skaro.frenbot.commands.parsers;

public interface ObjectParser<T> {

	T parse(String[] arguments, Class<T> clazz) throws ParserException;
	
}
