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

import KCAAgent.KCAAgent;
import agent.AbstractAgent;

public class PauseCommand extends Command {

	protected AbstractAgent	agent	= null;

	@SuppressWarnings("hiding")
	public PauseCommand(Command.Action action, KCAAgent agent, int time) {
		super(action, time);
		this.agent = agent;
	}

	public AbstractAgent getAgent() {
		return agent;
	}

	public void setAgent(AbstractAgent agent) {
		this.agent = agent;
	}

	@Override
	public String toString() {
		return "PauseCommand [agent=" + agent + "]";
	}

}
