package skaro.frenbot.commands.parsers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParsedText {

	private String command;
	private String arguments;
	
	public ParsedText(String command, String arguments) {
		this.command = command;
		this.arguments = arguments;
	}

	public String getCommand() {
		return command;
	}

	public String getArguments() {
		return arguments;
	}
	
	public List<String> getArgumentsList() {
		return Arrays.stream(arguments.split("\\s+"))
				.map(argument -> argument.trim())
				.collect(Collectors.toList());
	}
	
}
