package skaro.frenbot.api;

public class RoleQuery {

	private boolean filterReservedRoles;

	public boolean shouldFilterReservedRoles() {
		return filterReservedRoles;
	}
	public void setFilterReservedRoles(boolean filterReserved) {
		this.filterReservedRoles = filterReserved;
	}
	
}
