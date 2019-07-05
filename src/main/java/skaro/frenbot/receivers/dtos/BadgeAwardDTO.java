package skaro.frenbot.receivers.dtos;

import java.util.Date;

public class BadgeAwardDTO {
	
	private UserDTO user;
	private BadgeDTO badge;
	private Date awardDate;
	
	public UserDTO getUser() {
		return user;
	}
	public void setUser(UserDTO user) {
		this.user = user;
	}
	public BadgeDTO getBadge() {
		return badge;
	}
	public void setBadge(BadgeDTO badge) {
		this.badge = badge;
	}
	public Date getAwardDate() {
		return awardDate;
	}
	public void setAwardDate(Date awardDate) {
		this.awardDate = awardDate;
	}
	
}
