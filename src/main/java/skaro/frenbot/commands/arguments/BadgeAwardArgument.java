package skaro.frenbot.commands.arguments;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

public class BadgeAwardArgument implements Argument {

	@Option(name="-badge", handler=StringArrayOptionHandler.class, metaVar="badge title", required=true, usage="the badge to award")
	private String[] badgeTitle;
	
	@Option(name="-user", metaVar="@user", required=true, usage="the user to award the badge to")
	private String userDiscordId;
	
	public BadgeAwardArgument() {
	}

	public String getBadgeTitle() {
		return String.join(" ", badgeTitle);
	}

	public String getUserDiscordId() {
		return userDiscordId.replaceAll("[^0-9]", "");
	}
	
}
