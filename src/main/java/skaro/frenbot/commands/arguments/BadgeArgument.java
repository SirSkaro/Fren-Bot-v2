package skaro.frenbot.commands.arguments;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

public class BadgeArgument implements Argument {

	@Option(name="-badge", metaVar="badge", handler=StringArrayOptionHandler.class, required=true, usage="the name of the Discord role associated with the badge")
	private String[] badgeName;
	
	public String getBadgeName() {
		return String.join(" ", badgeName);
	}
	
}
