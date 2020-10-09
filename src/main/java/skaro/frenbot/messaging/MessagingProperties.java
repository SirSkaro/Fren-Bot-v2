package skaro.frenbot.messaging;

import javax.validation.constraints.NotNull;

public class MessagingProperties {
	@NotNull
	private String badges;

	public String getBadges() {
		return badges;
	}

	public void setBadges(String badges) {
		this.badges = badges;
	}
}
