package skaro.frenbot.commands.parsers;

import java.util.List;

public class ParserException extends RuntimeException {

	private static final long serialVersionUID = 8887248364227285285L;
	private String usageHint;
	
	public ParserException(String arguments, String usageHint) {
		super("Could not parse the string: "+ arguments);
		this.usageHint = usageHint;
	}
	
	public ParserException(List<String> arguments, String usageHint) {
		super("Could not parse the string: "+ String.join(",", arguments));
		this.usageHint = usageHint;
	}

	public String getUsageHint() {
		return usageHint;
	}
	
}
