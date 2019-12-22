package skaro.frenbot.commands.arguments;

import javax.validation.constraints.Pattern;

import org.kohsuke.args4j.Option;

public class UserArgument implements Argument {

	@Option(name="-user", metaVar="@user", required=true, usage="the user to get information about")
	@Pattern(regexp="<[@#!]*[0-9]+>")
	private String userDiscordId;
	
	public UserArgument() {
		
	}
	
	public UserArgument(String discordId) {
		this.userDiscordId = discordId;
	}
	
	public String getUserDiscordId() {
		return this.userDiscordId.replaceAll("[^0-9]", "");
	}
	
}
