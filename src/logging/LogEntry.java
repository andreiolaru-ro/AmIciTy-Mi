package logging;

import agent.AbstractAgent;

public class LogEntry implements Comparable<LogEntry>
{
	private static String	REPLACE_STRING	= "~";
	private static int	REPLACE_STRING_LEN	= REPLACE_STRING.length();
	private static long		crtTime			= 0;
	
	private AbstractAgent			owner;
	private int				step;
	private Log.Level		level;
	private String			msg;
	private Object[]		data;
	private long			time;
	
	private StringBuffer	entry			= null;
	
	@SuppressWarnings("hiding")
	public LogEntry(AbstractAgent owner2, int step, Log.Level level, String msg, Object... data)
	{
		this.owner = owner2;
		this.step = step;
		this.level = level;
		this.msg = msg;
		this.data = data;
		this.time = crtTime++;
	}
	
	public StringBuffer print()
	{
		if(entry == null)
		{
			entry = new StringBuffer();
			int indexInMsg = 0;
			entry.append(owner);
			entry.append(" - ");
			for(Object obj : data)
			{
				int index2 = msg.indexOf(REPLACE_STRING, indexInMsg);
				if(index2 > -1)
				{
					entry.append(msg.substring(indexInMsg, index2));
					indexInMsg = index2 + REPLACE_STRING_LEN;
				}
				entry.append((obj == null) ? "null" : obj.toString());
			}
			entry.append(msg.substring(indexInMsg));
		}
		return entry;
	}
	
	@Override
	public String toString()
	{
		return this.print().toString();
	}
	
	@Override
	public int compareTo(LogEntry le)
	{
		if(this.step != le.step)
		{
			return this.step - le.step;
		}
		else if(this.owner != le.owner)
		{
			if(this.owner == null)
			{
				return -1;
			}
			else if(le.owner == null)
			{
				return 1;
			}
			else
			{
				return this.owner.getId().compareTo(le.owner.getId());
			}
		}
		else
		{
			return (int)(this.time - le.time);
		}
	}
	
	public Log.Level getLevel()
	{
		return level;
	}
	
	public int getStep()
	{
		return step;
	}
}
