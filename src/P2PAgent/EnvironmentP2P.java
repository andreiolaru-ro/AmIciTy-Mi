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
package P2PAgent;

import java.util.ArrayList;

import logging.Logger;
import base.Environment;
import base.agent.AbstractAgent;
import base.agent.AgentID;
import base.agent.Location;
import base.graphics.AbstractGridViewerWhitoutLocation;
import base.scenario.P2PScenario;

public class EnvironmentP2P extends Environment<SimulationP2P, P2PAgent> {

	public EnvironmentP2P(SimulationP2P parent, P2PScenario scenario) {
		this.parent = parent;
		logger = new Logger();
		selected = new ArrayList<AbstractAgent>();
		agents = scenario.getAgents();
		this.x = 0;
		this.y = 0;
		this.height = 31;
		this.width = 31;

		// we initialize the agents
		for (P2PAgent agent : agents.values()) {
			agent.setParent(this);
			agent.setContacts(scenario.getContacts().get(agent.getId()));
		}
	}

	public AgentID injectItem(AgentID agentID, Item item) {
		if (!agents.get(agentID).getItems().contains(item)) {
			agents.get(agentID).getItems().add(item);
			P2PAgent.setNumberItem(new Integer(P2PAgent.getNumberItem().intValue() + 1));
		}
		return agentID;
	}

	public AgentID injectItemWanted(AgentID agentID, Item itemWanted) {
		if ((!agents.get(agentID).getItemsWanted().contains(itemWanted))
				&& (!agents.get(agentID).getItems().contains(itemWanted))) {
			agents.get(agentID).getItemsWanted().add(itemWanted);
			P2PAgent.setNumberItemWanted(new Integer(P2PAgent.getNumberItem().intValue() + 1));
		}
		return agentID;
	}

	@Override
	public P2PAgent cellAt(double x, double y) {
		Location loc = new Location(x, y);
		P2PAgent res = null;
		double minDist = Double.POSITIVE_INFINITY;
		for (P2PAgent cell : agents.values()) {

			double dist = loc.getDistance(AbstractGridViewerWhitoutLocation.getLocationOnTheGrid()
					.get(cell));
			if (dist < minDist) {
				minDist = dist;
				res = cell;
			}
		}
		return res;
	}

}
