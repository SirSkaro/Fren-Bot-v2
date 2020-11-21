package skaro.frenbot;

import discord4j.common.util.Snowflake;

public class DiscordRoleDivider {
	
	private Snowflake topDivider;
	private Snowflake bottomDivider;
	
	public DiscordRoleDivider(Snowflake topDivider, Snowflake bottomDivider) {
		this.topDivider = topDivider;
		this.bottomDivider = bottomDivider;
	}

	public Snowflake getTopDivider() {
		return topDivider;
	}

	public Snowflake getBottomDivider() {
		return bottomDivider;
	}
	
}
