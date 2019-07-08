package skaro.frenbot.commands.parsers;

import java.util.List;

public class ParserException extends RuntimeException {

	private static final long serialVersionUID = 8887248364227285285L;
	
	public ParserException(String arguments, Exception e) {
		super("Exception "+ e.getClass().getSimpleName() +" - Could not parse the string: "+ arguments);
	}
	
	public ParserException(List<String> arguments, Exception e) {
		super("Exception "+ e.getClass().getSimpleName() +" - Could not parse the string: "+ String.join(",", arguments));
	}

}
