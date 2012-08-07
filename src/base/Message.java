package base;

import KCAAgent.EnvironmentKCA;
import agent.AgentID;

public abstract class Message<T>
{

	// in request and data messages, the agents in Facts will not be used (for
	// now)

	protected int		step;
	protected int		sequence;
	protected AgentID	from;
	protected T			content;

	public AgentID getFrom()
	{
		return from;
	}


	public boolean isFuture()
	{
		return (step >= Environment.getStep());
	}

	public T getContents()
	{
		return content;
	}

	@Override
	public abstract String toString();




}
