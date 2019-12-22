package skaro.frenbot.commands.parsers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.kohsuke.args4j.CmdLineParser;

import skaro.frenbot.commands.arguments.Argument;
import skaro.frenbot.commands.arguments.UsageHelp;

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
			throw new ParserException(arguments, new UsageHelp(getFlagsAndDescriptions(parser)));
		}
	}
	
	private List<FlagDescriptionPair> getFlagsAndDescriptions(CmdLineParser parser) {
		if(parser == null) {
			return new ArrayList<FlagDescriptionPair>();
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		parser.printUsage(stream);
		
		return Arrays.stream(new String(stream.toByteArray()).split("\n"))
			.map(flagAndDescription -> flagAndDescription.split(":"))
			.map(flagAndDescription -> new FlagDescriptionPair(flagAndDescription[0].trim(), flagAndDescription[1].trim()))
			.collect(Collectors.toList());
	}
	
}
