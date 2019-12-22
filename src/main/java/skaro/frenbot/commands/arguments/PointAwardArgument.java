package skaro.frenbot.commands.arguments;

import org.kohsuke.args4j.Option;

public class PointAwardArgument implements Argument {

	@Option(name="-points", metaVar="N", required=true, usage="the (positive) amount of points to award to a user")
	private Integer amount;
	
	@Option(name="-user", metaVar="@user", required=true, usage="the user to award points to")
	private String userDiscordId;
	
	public PointAwardArgument() {
		
	}
	
	public PointAwardArgument(int amount, String discordId) {
		this.amount = amount;
		this.userDiscordId = discordId;
	}

	public Integer getAmount() {
		return amount;
	}

	public String getUserDiscordId() {
		return userDiscordId.replaceAll("[^0-9]", "");
	}
	
}
