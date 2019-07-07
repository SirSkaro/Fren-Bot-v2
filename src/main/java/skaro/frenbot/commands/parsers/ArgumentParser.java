package skaro.frenbot.commands.parsers;

import java.util.List;

import org.kohsuke.args4j.CmdLineParser;

import skaro.frenbot.commands.arguments.Argument;

public class ArgumentParser <T extends Argument> implements ObjectParser<T> {
	
	public T parse(List<String> arguments, Class<T> clazz) throws ParserException {
		try {
			T argument = clazz.getConstructor().newInstance();
			CmdLineParser parser = new CmdLineParser(argument);
			parser.parseArgument(arguments);
			
			return argument;
		} catch (Exception e) {
			throw new ParserException(arguments, e);
		}
	}

}
