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

import java.util.Collection;
import java.util.LinkedList;

import agent.AgentID;

public class Action {
	enum ActionType {

		INFORM,

		FREE,

		// REQUEST,

	}

	ActionType	type;
	Fact		relatedFact	= null;
	// Data relatedData = null;
	AgentID		targetAgent	= null;

	public Action() {
		type = ActionType.FREE;
	}

	public Action(Fact fact, AgentID agent) {
		type = ActionType.INFORM;
		relatedFact = fact;
		targetAgent = agent;
	}

	// public Action(Data data, AgentID agent)
	// {
	// type = ActionType.REQUEST;
	// relatedData = data;
	// targetAgent = agent;
	// }

	public Collection<Action> toCollection() {
		Collection<Action> ret = new LinkedList<Action>();
		ret.add(this);
		return ret;
	}

	@Override
	public String toString() {
		String content = "";
		switch (type) {
		case FREE:
			content = "FREE";
			break;
		case INFORM:
			content = relatedFact + ">" + targetAgent;
			break;
		// case REQUEST:
		// content = relatedData + ">" + targetAgent;
		// break;
		}
		return type.toString() + "." + content;
	}
}
