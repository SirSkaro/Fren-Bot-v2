package skaro.frenbot.commands.parsers;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import discord4j.core.object.entity.Message;

public class RegexParser implements TextParser {

	private String prefix;
	
	public RegexParser(String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public Optional<ParsedText> parseMessageContent(Message message) {
		String patternRegex = String.format("(%s)([a-zA-Z]+)[\\s]+([a-zA-Z\\-]*)", prefix);
		Pattern expectedPattern = Pattern.compile(patternRegex);
		int commandGroupNumber = 2;
		int argumentGroupNumber = 3;
		
		return message.getContent()
			.map(messageContent -> expectedPattern.matcher(messageContent))
			.filter(matcher -> matcher.find())
			.flatMap(matcher -> assembleParsedText(matcher, commandGroupNumber, argumentGroupNumber));
	}
	
	private Optional<ParsedText> assembleParsedText(Matcher matcher, int commandGroupNumber, int argumentGroupNumber) {
		try {
			String command = matcher.group(commandGroupNumber);
			String arguments = matcher.group(argumentGroupNumber);
			return Optional.of(new ParsedText(command, arguments));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
}
