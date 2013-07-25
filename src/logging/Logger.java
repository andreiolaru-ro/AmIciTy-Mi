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

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class Logger implements LogListener {
	private static final int						STEPS_SHOWN	= 5;

	private SortedMap<Integer, SortedSet<LogEntry>>	entries		= new TreeMap<Integer, SortedSet<LogEntry>>();
	// private SortedSet<LogEntry> entries =
	// Collections.synchronizedSortedSet(new TreeSet<LogEntry>());
	// private Collection<UpdateListener> listeners = new
	// ArrayList<UpdateListener>();
	private Log.Level								level;
	private Integer									lastStep	= null;

	@Override
	public void add(LogEntry entry) {
		if ((lastStep == null) || (lastStep.intValue() < entry.getStep())) {
			// must create a new step
			lastStep = new Integer(entry.getStep());
			entries.put(lastStep, Collections.synchronizedSortedSet(new TreeSet<LogEntry>()));
			while (entries.size() > STEPS_SHOWN)
				entries.remove(entries.firstKey());
		}
		if (entries.containsKey(new Integer(entry.getStep())))
			entries.get(new Integer(entry.getStep())).add(entry);
		// update();
		// if(entry.getLevel().compareTo(level) <= 0)
		// {
		// updateEntriesStr();
		// }
	}

	@Override
	public void remove(LogEntry entry) {
		if (entries.containsKey(new Integer(entry.getStep())))
			entries.get(new Integer(entry.getStep())).remove(entry);
		// update();
		// if(entry.getLevel().compareTo(level) <= 0)
		// {
		// updateEntriesStr();
		// }
	}

	// public void update()
	// {
	// for(UpdateListener ul : listeners)
	// {
	// ul.update();
	// }
	// }

	public void addLog(Log log) {
		log.addListener(this);
	}

	public void removeLog(Log log) {
		log.removeListener(this);
	}

	public void setLevel(Log.Level value) {
		if (level != value) {
			level = value;
			// updateEntriesStr();
		}
	}

	// private void updateEntriesStr()
	// {
	// entriesStr = "";
	// synchronized(entries)
	// {
	// for(LogEntry entry : entries)
	// {
	// if(entry.getLevel().compareTo(level) <= 0)
	// {
	// entriesStr += entry.toString() + "\n";
	// }
	// }
	// }
	// }

	// public String getEntries()
	// {
	// return entriesStr;
	// }

	public String printEntries() {
		StringBuffer buffer = new StringBuffer();
		for (Map.Entry<Integer, SortedSet<LogEntry>> stepSet : entries.entrySet())
			for (LogEntry entry : stepSet.getValue()) {
				buffer.append(entry.print());
				buffer.append('\n');
			}
		return buffer.toString();
	}
}
