package skaro.frenbot.commands.parsers;

import java.util.Optional;

public interface TextParser {

	Optional<ParsedText> parseMessageContent(String messageContent);
	
}
