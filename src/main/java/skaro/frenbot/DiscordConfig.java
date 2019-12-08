package skaro.frenbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import discord4j.core.object.util.Snowflake;

@Configuration
public class DiscordConfig {

	private Snowflake serverSnowflake;
	private DiscordRoleDivider earnedBadgeDivider;
	private DiscordRoleDivider badgeDivider;
	
	@Autowired
	public void setServerSnowflake(@Value("${discord.guildId}") Long guildId) {
		this.serverSnowflake = Snowflake.of(guildId);
	}
	public Snowflake getServerSnowflake() {
		return serverSnowflake;
	}
	
	@Autowired
	public void setEarnedBadgeDivider(
			@Value("${discord.earnedBadgeTopDividerRole}") Long topDividerId,
			@Value("${discord.earnedBadgeBottomDividerRole}") Long bottomDividerId) {
		earnedBadgeDivider = new DiscordRoleDivider(Snowflake.of(topDividerId), Snowflake.of(bottomDividerId));
	}
	public DiscordRoleDivider getEarnedBadgeDivider() {
		return earnedBadgeDivider;
	}
	
	@Autowired
	public void setBadgeDivider(
			@Value("${discord.badgeTopDividerRole}") Long topDividerId,
			@Value("${discord.badgeBottomDividerRole}") Long bottomDividerId) {
		badgeDivider = new DiscordRoleDivider(Snowflake.of(topDividerId), Snowflake.of(bottomDividerId));
	}
	public DiscordRoleDivider getBadgeDivider() {
		return badgeDivider;
	}
	
	
}
