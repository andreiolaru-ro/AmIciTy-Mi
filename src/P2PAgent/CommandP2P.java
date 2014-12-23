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

import base.Command;
import base.agent.AgentID;

public class CommandP2P extends Command {

	private Item	item	= null;
	private AgentID	agentID	= null;

	public CommandP2P(CommandP2P.Action action, AgentID agentID, Item item, int time) {
		super(action, time);
		this.setItem(item);
		this.setAgentID(agentID);
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public AgentID getAgentID() {
		return agentID;
	}

	public void setAgentID(AgentID agentID) {
		this.agentID = agentID;
	}
}
