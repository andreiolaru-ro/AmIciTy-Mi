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
package logging;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import base.Environment;
import base.agent.AbstractAgent;
import base.scenario.AbstractScenario;

public class Log {
	public enum Level {
		SEVERE, WARNING, INFO, FINE, FINER,
	}

	private int					MAX_SIZE	= 50;
	private AbstractAgent		owner;
	private Queue<LogEntry>		entries		= new LinkedList<LogEntry>();
	private Set<LogListener>	listeners	= new HashSet<LogListener>();

	@SuppressWarnings("hiding")
	public Log(AbstractAgent owner) {
		this.owner = owner;
	}

	public void addEntry(Level level, String msg, Object... data) {
		LogEntry entry = new LogEntry(owner, Environment.getStep(), level, msg, data);
		entries.add(entry);
		for (LogListener listener : listeners) {
			listener.add(entry);
		}

		if (AbstractScenario.rand().nextDouble() < 0.01)
			// clean
			while (entries.size() > MAX_SIZE)
				entries.poll();
	}

	public void addListener(LogListener listener) {
		for (LogEntry entry : entries) {
			listener.add(entry);
		}
		listeners.add(listener);
	}

	public void removeListener(LogListener listener) {
		for (LogEntry entry : entries) {
			listener.remove(entry);
		}
		listeners.remove(listener);
	}

	public void le(String msg, Object... data) {
		addEntry(Level.SEVERE, msg, data);
	}

	public void lw(String msg, Object... data) {
		addEntry(Level.WARNING, msg, data);
	}

	public void li(String msg, Object... data) {
		addEntry(Level.INFO, msg, data);
	}

	public void lf(String msg, Object... data) {
		addEntry(Level.FINE, msg, data);
	}

	public void lf2(String msg, Object... data) {
		addEntry(Level.FINER, msg, data);
	}
}
