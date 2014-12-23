/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru. See the AUTHORS file for more information.
 * 
 * This file is part of AmIciTy-Mi.
 * 
 * AmIciTy-Mi is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * AmIciTy-Mi is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with AmIciTy-Mi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package KCAAgent;

import java.util.ArrayList;
import java.util.Collection;

import logging.Logger;
import base.Environment;
import base.Message;
import base.agent.AbstractAgent;
import base.agent.AgentID;
import base.agent.Location;
import base.graphics.UpdateListener;
import base.scenario.KCAScenario;

public class EnvironmentKCA extends Environment<SimulationKCA, KCAAgent> {

	/*
	 * = new Command[] { // agent, fact, pressure new
	 * Command(Command.Action.INJECT, new AgentID(KCA.NX / 2, KCA.NY / 2), new
	 * Fact(null, data[0]).setPressure(+0.3f).setPersistence(0.1f)), new
	 * Command(Command.Action.INJECT, new AgentID(KCA.NX - 1, 0), new Fact(null,
	 * data[1]).setPressure(+0.2f).setPersistence(0.1f)), new
	 * Command(Command.Action.INJECT, new AgentID(KCA.NX - 1, KCA.NY - 1), new
	 * Fact(null, data[2]).setPressure(+0.2f).setPersistence(0.1f)), new
	 * Command(Command.Action.WAIT, 20), new Command(Command.Action.INJECT, new
	 * AgentID(0, KCA.NY - 1), new Fact(null, data[3]).setPressure(+0.5f)), new
	 * Command(Command.Action.INJECT, new AgentID(KCA.NX / 2, KCA.NY / 2), new
	 * Fact(null, data[4]).setPressure(+0.5f)), // new
	 * Command(Command.Action.INJECT, new AgentID(NX / 2 + 1, NY / 2), new
	 * Fact(null, data[5]).setPressure(+0.5)), // new
	 * Command(Command.Action.INJECT, new AgentID(NX / 2, NY / 2 + 1), new
	 * Fact(null, data[6]).setPressure(+0.5)), // new
	 * Command(Command.Action.INJECT, new AgentID(NX / 2 + 1, NY / 2 + 1), new
	 * Fact(null, data[7]).setPressure(+0.5)), // new
	 * Command(Command.Action.WAIT, 20), // new Command(Command.Action.INJECT,
	 * new AgentID(NX / 2 + 1, 0), new Fact(null, data[8]).setPressure(+1.0)),
	 * // new Command(Command.Action.REQUEST, new AgentID(1, NY / 2), 8, 1), };
	 */

	// private static int CAPACITY = Logix.agentCapacity;

	Collection<UpdateListener>	listeners	= new ArrayList<UpdateListener>();

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
		for (AgentID agent : agents.keySet()) {
			if (agents.get(agent).getLocation() == null) {
				System.out.println(agent);
			}
		}

		double dist = Double.POSITIVE_INFINITY;
		AgentID id = null;
		for (AgentID agent : agents.keySet()) {
			if (agents.get(agent).getLocation() == null) {
				System.out.println(agent);
			}
			double d = agents.get(agent).getLocation().getDistance(location);
			if (d < dist && !agents.get(agent).isPause()) {
				dist = d;
				id = agent;
			}
		}
		// the whole map could be in pause, id could be null
		if (id != null)
			agents.get(id).receiveMessage(message);

		return id;
	}

	@Override
	@SuppressWarnings("hiding")
	public KCAAgent cellAt(double x, double y) {
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

	// FIXME parent is only used here, do we really need this?
	public void produce(Message<?> msg) {
		parent.doReply(msg);
	}

	/*
	 * public static int getCAPACITY() { return CAPACITY; }
	 * 
	 * public static void setCAPACITY(int cAPACITY) { CAPACITY = cAPACITY; }
	 */
}
