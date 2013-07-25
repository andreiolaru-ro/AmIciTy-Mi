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
package base;

import graphics.UpdateListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import logging.Logger;
import agent.AbstractAgent;
import agent.AgentID;

public abstract class Environment<SIMULATION extends Simulation<?, ?>, AGENT extends AbstractAgent> {
	protected SIMULATION			parent;
	protected Logger				logger;
	protected static int			step		= 0;
	protected List<AbstractAgent>	selected;
	Collection<UpdateListener>		listeners	= new ArrayList<UpdateListener>();
	protected Map<AgentID, AGENT>	agents;
	// sequence (sub-step) number for messages, used for comparing messages
	protected static int			sequence	= 0;

	// public because they are used for drawing
	public double					x;
	public double					y;
	public double					width;
	public double					height;

	// FIXME shouldn't the cells be updating simultaneously?
	public void step() throws Exception {
		for (AGENT agent : agents.values()) {
			agent.step();
		}

		doUpdate(); // update UI

		step++;
		sequence = 0;
	}

	public void addSelected(AbstractAgent agent) {
		selected.add(agent);
		logger.addLog(agent.getLog());
	}

	public void removeSelected(AbstractAgent agent) {
		selected.remove(agent);
		logger.removeLog(agent.getLog());
	}

	public List<AbstractAgent> getSelected() {
		return selected;
	}

	public Logger getLogger() {
		return logger;
	}

	public static int getStep() {
		return step;
	}

	public void addUpdateListener(UpdateListener ul) {
		listeners.add(ul);
	}

	public void removeUpdateListener(UpdateListener ul) {
		listeners.remove(ul);
	}

	public void doUpdate() {
		for (UpdateListener ul : listeners) {
			ul.update();
		}
	}

	public Collection<AGENT> getAgents() {
		return agents.values();
	}

	public AGENT getAgent(int idAgent) {
		return agents.get(new AgentID(new Integer(idAgent)));
	}

	public static int getSequence() {
		return sequence++;
	}

	public abstract AGENT cellAt(double x, double y);
}
