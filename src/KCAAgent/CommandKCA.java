package KCAAgent;

import agent.AgentID;
import agent.Location;
import base.Command;
import base.PauseCommand;

public class CommandKCA extends PauseCommand {

	private Location	location	= null;
	private Fact		fact		= null;

	
	


	// inject fact
	@SuppressWarnings("hiding")
	public CommandKCA(Command.Action action, Location location, Fact fact, int time) {
		super(action, null, time);
		this.location = location;
		this.fact = fact;
	}

	@SuppressWarnings("hiding")
	public CommandKCA(CommandKCA.Action action, Location location, Fact fact) {
		this(action, location, fact, 0);
	}
	
	// move
	@SuppressWarnings("hiding")
	public CommandKCA(Command.Action action, KCAAgent agent, Location location, int time) {
		super(action, agent, time);
		this.location = location;
	}

	// pause
	public CommandKCA(Action action, KCAAgent agent, int time) {
		super(action, agent, time);
	}


	@SuppressWarnings("hiding")
	public CommandKCA(CommandKCA.Action action, int ms) {
		super(action, null, ms);
	}

	@SuppressWarnings("hiding")
	public void setFact(Fact fact) {
		this.fact = fact;
	}

	public Fact getFact() {
		return fact;
	}

	@SuppressWarnings("hiding")
	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return "CommandKCA [location=" + location + ", fact=" + fact + ", time=" + time + ", action=" + action + ", agent=" + agent +"]";
	}
	
}
