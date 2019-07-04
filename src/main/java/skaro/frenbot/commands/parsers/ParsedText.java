package skaro.frenbot.commands.parsers;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsedText {

	private String command;
	private String arguments;
	
	public static Optional<ParsedText> of(String prefix, String input) {
		
		String patternRegex = String.format("(%s)([a-zA-Z]+)[\\s]+([a-zA-Z\\-]*)", prefix);
		Pattern expectedPattern = Pattern.compile(patternRegex);
		Matcher matcher = expectedPattern.matcher(input);
		int commandGroupNumber = 2;
		int argumentGroupNumber = 3;
		
		if(!matcher.find()) {
			return Optional.empty();
		}

		try {
			ParsedText result = new ParsedText();
			result.command = matcher.group(commandGroupNumber);
			result.arguments = matcher.group(argumentGroupNumber);
			return Optional.of(result);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public String getCommand() {
		return command;
	}

	public String getArguments() {
		return arguments;
	}
	
}
