package skaro.frenbot.commands.parsers;

import java.util.Optional;

import org.kohsuke.args4j.CmdLineParser;

import skaro.frenbot.commands.arguments.Argument;

public class ArgumentParser implements ObjectParser<Argument> {
	
	private String prefix;
	
	public ArgumentParser(String prefix) {
		this.prefix = prefix;
	}
	
	public Argument parse(String[] arguments, Class<Argument> clazz) throws ParserException {
		try {
			Argument argument = clazz.getConstructor().newInstance();
			CmdLineParser parser = new CmdLineParser(argument);
			parser.parseArgument(arguments);
			
			return argument;
		} catch (Exception e) {
			throw new ParserException(arguments);
		}
	}

	@Override
	public Optional<String> getCommandName(String arguments) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
