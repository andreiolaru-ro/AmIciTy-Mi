package KCAAgent;

import graphics.UpdateListener;

import java.util.ArrayList;
import java.util.Collection;

import base.Environment;
import base.Message;

import logging.Logger;
import scenario.KCAScenario;
import agent.AbstractAgent;
import agent.AgentID;
import agent.Location;

public class EnvironmentKCA extends Environment<SimulationKCA, KCAAgent> {	
	
 /* = new Command[] {
	// agent, fact, pressure
	new Command(Command.Action.INJECT, new AgentID(KCA.NX / 2, KCA.NY / 2), new Fact(null, data[0]).setPressure(+0.3f).setPersistence(0.1f)),
	new Command(Command.Action.INJECT, new AgentID(KCA.NX - 1, 0), new Fact(null, data[1]).setPressure(+0.2f).setPersistence(0.1f)),
	new Command(Command.Action.INJECT, new AgentID(KCA.NX - 1, KCA.NY - 1), new Fact(null, data[2]).setPressure(+0.2f).setPersistence(0.1f)),
	new Command(Command.Action.WAIT, 20),
	new Command(Command.Action.INJECT, new AgentID(0, KCA.NY - 1), new Fact(null, data[3]).setPressure(+0.5f)),
	new Command(Command.Action.INJECT, new AgentID(KCA.NX / 2, KCA.NY / 2), new Fact(null, data[4]).setPressure(+0.5f)),
//	new Command(Command.Action.INJECT, new AgentID(NX / 2 + 1, NY / 2), new Fact(null, data[5]).setPressure(+0.5)),
//	new Command(Command.Action.INJECT, new AgentID(NX / 2, NY / 2 + 1), new Fact(null, data[6]).setPressure(+0.5)),
//	new Command(Command.Action.INJECT, new AgentID(NX / 2 + 1, NY / 2 + 1), new Fact(null, data[7]).setPressure(+0.5)),
//	new Command(Command.Action.WAIT, 20),
//	new Command(Command.Action.INJECT, new AgentID(NX / 2 + 1, 0), new Fact(null, data[8]).setPressure(+1.0)),
//	new Command(Command.Action.REQUEST, new AgentID(1, NY / 2), 8, 1),	
}; */

	
	// public because they are used for drawing
	public double x;
	public double y;
	public double width;
	public double height;

	
	//private static int CAPACITY = Logix.agentCapacity;

	
	Collection<UpdateListener> listeners = new ArrayList<UpdateListener>();
	
	@SuppressWarnings("hiding")
	public EnvironmentKCA(SimulationKCA parent, KCAScenario scenario) {
		this.parent = parent;
		this.x = scenario.getX();
		this.y = scenario.getY();
		this.width = scenario.getWidth();
		this.height = scenario.getHeight();

		logger = new Logger();

		agents = scenario.getAgents();
		selected = new ArrayList<AbstractAgent>();
		
		for (KCAAgent agent : agents.values()) {
			agent.setParent(this);
		}
		for (KCAAgent agent : agents.values()) {
			agent.updateNeighbors(false);
		}
	}

	public AgentID inject(Location location, Message<?> message) {
		double dist = Double.POSITIVE_INFINITY;
		AgentID id = null;
		for (AgentID agent : agents.keySet()) {
			double d = agents.get(agent).getLocation().getDistance(location);
			if (d < dist) {
				dist = d;
				id = agent;
			}
		}
		assert agents.containsKey(id) : id;
		agents.get(id).receiveMessage(message);
		return id;
	}
	
	public KCAAgent cellAt(@SuppressWarnings("hiding") double x, @SuppressWarnings("hiding") double y) {
		Location loc = new Location(x, y);
		KCAAgent res = null;
		double minDist = Double.POSITIVE_INFINITY;
		for (KCAAgent cell : agents.values()) {
			double dist = loc.getDistance(cell.getLocation());
			if (dist < minDist) {
				minDist = dist;
				res = cell;
			}
		}
		return res;
	}
 
	//FIXME parent is only used here, do we really need this?
	public void produce(Message<?> msg) {
		parent.doReply(msg);
	}

	/*public static int getCAPACITY() {
		return CAPACITY;
	}

	public static void setCAPACITY(int cAPACITY) {
		CAPACITY = cAPACITY;
	}*/
}

