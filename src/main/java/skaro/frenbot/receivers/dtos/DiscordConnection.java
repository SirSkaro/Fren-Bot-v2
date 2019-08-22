package skaro.frenbot.receivers.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordConnection {
	private Long discordId;
	
	public DiscordConnection() {
		
	}
		
	public Long getDiscordId() {
		return discordId;
	}
	
	public void setDiscordId(Long discordId) {
		this.discordId = discordId;
	}
	
}
