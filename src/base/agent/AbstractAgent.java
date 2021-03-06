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
package base.agent;

import logging.Log;
import base.Message;
import base.measure.Measurable;

/**
 * 
 * abstract class to define a basic agent
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractAgent implements Measurable {
	protected AgentID	id;
	protected Log		log;
	/**
	 * Indicated if the agent is in pause in the simulation or not.
	 */
	protected boolean	pause;

	protected abstract void agentPrint();

	public abstract void step() throws Exception;

	protected abstract void sendMessage(AgentID to, Message<?> msg);

	public abstract void receiveMessage(Message<?> message);

	// graphical functions to select an agent on the grid
	public abstract boolean isSelected();

	public abstract void toggleSelected();

	public AbstractAgent(AgentID id) {
		super();
		this.id = id;
		this.pause = false;
		this.log = new Log(this);
	}

	public AgentID getId() {
		return id;
	}

	@SuppressWarnings("hiding")
	public void setId(AgentID id) {
		this.id = id;
	}

	public Log getLog() {
		return log;
	}

	public boolean isPause() {
		return pause;
	}

	public void unpause() {
		this.pause = false;
	}

	public void pause() {
		this.pause = true;
	}

}
