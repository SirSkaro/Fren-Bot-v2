package skaro.frenbot.commands.parsers;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.kohsuke.args4j.CmdLineParser;

import skaro.frenbot.commands.arguments.Argument;

public class ArgumentParser implements ObjectParser {
	
	@Override
	public <T extends Argument> T parse(List<String> arguments, Class<T> clazz) throws ParserException  {
		CmdLineParser parser = null;
		try {
			T argument = clazz.getConstructor().newInstance();
			parser = new CmdLineParser(argument);
			parser.parseArgument(arguments);
			
			return argument;
		} catch (Exception e) {
			throw new ParserException(arguments, getUsageMessage(parser));
		}
	}
	
	private String getUsageMessage(CmdLineParser parser) {
		if(parser == null) {
			return "no usage help available : an error may have occured";
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		parser.printUsage(stream);
		return new String(stream.toByteArray());
	}
	
}
