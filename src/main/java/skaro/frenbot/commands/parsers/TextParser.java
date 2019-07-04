package skaro.frenbot.commands.parsers;

import java.util.Optional;

import discord4j.core.object.entity.Message;

public interface TextParser {

	Optional<ParsedText> parseMessageContent(Message message);
	
}
