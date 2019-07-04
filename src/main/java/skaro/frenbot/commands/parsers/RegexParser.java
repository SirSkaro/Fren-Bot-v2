package skaro.frenbot.commands.parsers;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParser implements TextParser {

	private String prefix;
	
	public RegexParser(String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public Optional<ParsedText> parseMessageContent(String messageContent) {
		String patternRegex = String.format("(%s)([a-zA-Z]+)[\\s]+([a-zA-Z\\-]*)", prefix);
		Pattern expectedPattern = Pattern.compile(patternRegex);
		Matcher matcher = expectedPattern.matcher(messageContent);
		int commandGroupNumber = 2;
		int argumentGroupNumber = 3;
		
		if(!matcher.find()) {
			return Optional.empty();
		}

		try {
			String command = matcher.group(commandGroupNumber);
			String arguments = matcher.group(argumentGroupNumber);
			ParsedText result = new ParsedText(command, arguments);
			return Optional.of(result);
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
}
