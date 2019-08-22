package skaro.frenbot.receivers.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProgressDTO {

	private UserDTO user;
	private BadgeDTO nextBadge;
	private BadgeDTO currentHighestBadge;
	private Integer pointsToNextReward;
	private Integer currentPoints;
	
	public UserDTO getUser() {
		return user;
	}
	public void setUser(UserDTO user) {
		this.user = user;
	}
	public BadgeDTO getNextBadge() {
		return nextBadge;
	}
	public void setNextBadge(BadgeDTO nextBadge) {
		this.nextBadge = nextBadge;
	}
	public BadgeDTO getCurrentHighestBadge() {
		return currentHighestBadge;
	}
	public void setCurrentHighestBadge(BadgeDTO currentHighestBadge) {
		this.currentHighestBadge = currentHighestBadge;
	}
	public Integer getPointsToNextReward() {
		return pointsToNextReward;
	}
	public void setPointsToNextReward(Integer pointToNextReward) {
		this.pointsToNextReward = pointToNextReward;
	}
	public Integer getCurrentPoints() {
		return currentPoints;
	}
	public void setCurrentPoints(Integer currentPoints) {
		this.currentPoints = currentPoints;
	}
	
}
