package skaro.frenbot.messaging;

import java.io.Serializable;

import skaro.frenbot.receivers.dtos.BadgeDTO;

public class BadgeEventMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	private BadgeDTO badge;
	private BadgeEventType eventType;
	
	public BadgeDTO getBadge() {
		return badge;
	}
	
	public void setBadge(BadgeDTO badge) {
		this.badge = badge;
	}
	public BadgeEventType getEventType() {
		return eventType;
	}
	public void setEventType(BadgeEventType eventType) {
		this.eventType = eventType;
	}
	
}
