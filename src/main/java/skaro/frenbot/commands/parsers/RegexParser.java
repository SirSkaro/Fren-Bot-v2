package skaro.frenbot.commands.parsers;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import discord4j.core.object.entity.Message;

public class RegexParser implements TextParser {

	private Pattern expectedPattern;
	private int commandGroupNumber;
	private int argumentGroupNumber;
	
	public RegexParser(String prefix) {
		String patternRegex = String.format("(%s)([a-zA-Z]+)([ a-zA-Z0-9\\-<>@!]*)", prefix);
		expectedPattern = Pattern.compile(patternRegex);
		commandGroupNumber = 2;
		argumentGroupNumber = 3;
	}
	
	@Override
	public Optional<ParsedText> parseMessageContent(Message message) {
		return message.getContent()
			.map(messageContent -> expectedPattern.matcher(messageContent))
			.filter(matcher -> matcher.find())
			.flatMap(matcher -> assembleParsedText(matcher));
	}
	
	private Optional<ParsedText> assembleParsedText(Matcher matcher) {
		try {
			String command = matcher.group(commandGroupNumber);
			String arguments = matcher.group(argumentGroupNumber);
			return Optional.of(new ParsedText(command, arguments));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
}
