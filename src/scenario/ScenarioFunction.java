package scenario;

import agent.Location;

public class ScenarioFunction {

	Location coordinates;
	String function;
	boolean inside;
	
	public ScenarioFunction(Location coordinates, String function, boolean inside) {
		super();
		this.coordinates = coordinates;
		this.function = function;
		this.inside = inside;
	}

	public Location getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Location coordinates) {
		this.coordinates = coordinates;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public boolean isInside() {
		return inside;
	}

	public void setInside(boolean inside) {
		this.inside = inside;
	}
	
}
