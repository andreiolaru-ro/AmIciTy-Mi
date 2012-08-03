package base;

import KCAAgent.Fact;
import agent.Location;


public class Command
{
	public static enum Action {
		INJECT, REQUEST, SNAPSHOT
	}
	
	private Command.Action	action	= null;
	private Location location = null;
	private Fact	fact	= null;
	private int		time;
	
	public Command(@SuppressWarnings("hiding") Command.Action action, @SuppressWarnings("hiding") Location location, @SuppressWarnings("hiding") Fact fact, @SuppressWarnings("hiding") int time)
	{
		this.action = action;
		this.location = location;
		this.fact = fact;
		this.time = time;
	}
	
	public Command(@SuppressWarnings("hiding") Command.Action action, @SuppressWarnings("hiding") Location location, @SuppressWarnings("hiding") Fact fact)
	{
		this(action, location, fact, 0);
	}
	
	public Command(@SuppressWarnings("hiding") Command.Action action, int ms)
	{
		this(action, null, null, ms);
	}

	public void setTime(@SuppressWarnings("hiding") int time) {
		this.time = time;
	}

	public int getTime() {
		return time;
	}

	public void setAction(@SuppressWarnings("hiding") Command.Action action) {
		this.action = action;
	}

	public Command.Action getAction() {
		return action;
	}

	public void setFact(@SuppressWarnings("hiding") Fact fact) {
		this.fact = fact;
	}

	public Fact getFact() {
		return fact;
	}

	public void setLocation(@SuppressWarnings("hiding") Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}
}