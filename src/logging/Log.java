package logging;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import scenario.KCAScenario;

import KCAAgent.KCAAgent;
import base.Environment;

public class Log
{
	public enum Level {
		SEVERE, WARNING, INFO, FINE, FINER,
	}
	
	private int					MAX_SIZE	= 50;
	private KCAAgent				owner;
	private Queue<LogEntry>		entries		= new LinkedList<LogEntry>();
	private Set<LogListener>	listeners	= new HashSet<LogListener>();
	
	@SuppressWarnings("hiding")
	public Log(KCAAgent owner)
	{
		this.owner = owner;
	}
	
	public void addEntry(Level level, String msg, Object... data)
	{
		LogEntry entry = new LogEntry(owner, Environment.getStep(), level, msg, data);
		entries.add(entry);
		for(LogListener listener : listeners)
		{
			listener.add(entry);
		}
		
		if(KCAScenario.rand().nextDouble() < 0.01)
			// clean
			while(entries.size() > MAX_SIZE)
				entries.poll();
	}
	
	public void addListener(LogListener listener)
	{
		for(LogEntry entry : entries)
		{
			listener.add(entry);
		}
		listeners.add(listener);
	}
	
	public void removeListener(LogListener listener)
	{
		for(LogEntry entry : entries)
		{
			listener.remove(entry);
		}
		listeners.remove(listener);
	}
	
	public void le(String msg, Object... data)
	{
		addEntry(Level.SEVERE, msg, data);
	}
	
	public void lw(String msg, Object... data)
	{
		addEntry(Level.WARNING, msg, data);
	}
	
	public void li(String msg, Object... data)
	{
		addEntry(Level.INFO, msg, data);
	}
	
	public void lf(String msg, Object... data)
	{
		addEntry(Level.FINE, msg, data);
	}
	
	public void lf2(String msg, Object... data)
	{
		addEntry(Level.FINER, msg, data);
	}
}
