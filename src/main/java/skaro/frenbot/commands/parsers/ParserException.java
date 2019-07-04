package skaro.frenbot.commands.parsers;

public class ParserException extends RuntimeException {

	private static final long serialVersionUID = 8887248364227285285L;
	
	public ParserException(String arguments) {
		super("Could not parse the string: "+ arguments);
	}
	
	public ParserException(String[] arguments) {
		super("Could not parse the string: "+ String.join(" ", arguments));
	}

}
