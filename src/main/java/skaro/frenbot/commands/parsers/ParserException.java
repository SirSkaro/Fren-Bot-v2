package skaro.frenbot.commands.parsers;

import java.util.List;

import skaro.frenbot.commands.arguments.UsageHelp;

public class ParserException extends RuntimeException {

	private static final long serialVersionUID = 8887248364227285285L;
	private UsageHelp usageHelp;
	
	public ParserException(List<String> arguments, UsageHelp usage) {
		super("Could not parse the string: "+ String.join(",", arguments));
		this.usageHelp = usage;
	}

	public UsageHelp getUsageHint() {
		return usageHelp;
	}
	
}
