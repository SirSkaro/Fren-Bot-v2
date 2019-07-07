package skaro.frenbot.commands.parsers;

import java.util.List;

import org.kohsuke.args4j.CmdLineParser;

public class ArgumentParser implements ObjectParser {
	
	@Override
	public <T> T parse(List<String> arguments, Class<T> clazz) throws ParserException  {
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
