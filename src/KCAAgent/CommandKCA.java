package KCAAgent;

import base.Command;
import agent.Location;


public class CommandKCA extends Command
{

	private Location location = null;
	private Fact	fact	= null;

	
	@SuppressWarnings("hiding")
	public CommandKCA( CommandKCA.Action action,  Location location,  Fact fact,  int time)
	{
		this.action = action;
		this.location = location;
		this.fact = fact;
		this.time = time;
	}
	
	@SuppressWarnings("hiding")
	public CommandKCA(CommandKCA.Action action, Location location, Fact fact)
	{
		this(action, location, fact, 0);
	}
	
	@SuppressWarnings("hiding")
	public CommandKCA( CommandKCA.Action action, int ms)
	{
		this(action, null, null, ms);
	}
	
	@SuppressWarnings("hiding")
	public void setFact( Fact fact) {
		this.fact = fact;
	}

	public Fact getFact() {
		return fact;
	}

	@SuppressWarnings("hiding")
	public void setLocation( Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}
}

