package skaro.frenbot.api.resources;

public class DiscordRole {
	private String id;
	private Integer position;
	private String name;
	private Integer color;
	private Integer assignedBadgeId;
	private Boolean isReserved;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getColor() {
		return color;
	}
	public void setColor(Integer color) {
		this.color = color;
	}
	public Integer getAssignedBadgeId() {
		return assignedBadgeId;
	}
	public void setAssignedBadgeId(Integer assignedBadgeId) {
		this.assignedBadgeId = assignedBadgeId;
	}
	public Boolean isReserved() {
		return isReserved;
	}
	public void setIsReserved(Boolean isReserved) {
		this.isReserved = isReserved;
	}
	
}
