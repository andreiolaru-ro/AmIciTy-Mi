package agent;


public abstract class LocationAgent extends AbstractAgent {

	protected Location location;
	
	public LocationAgent(AgentID id, Location location) {
		super(id);
		this.location = location;
		
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	} 

	
	
}
