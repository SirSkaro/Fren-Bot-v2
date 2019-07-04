package skaro.frenbot.commands.parsers;

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
	
}
