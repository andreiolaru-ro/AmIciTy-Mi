package base;

import base.Environment;

import agent.AgentID;

public class Message<T> implements Comparable<Message<T>>
{
	public enum Type
	{
		DATA, // contains facts with data with content. this structure is
				// mandatory
		REQUEST, // contains facts with data
		INFORM, // contains an array of facts
	}

	// in request and data messages, the agents in Facts will not be used (for
	// now)

	protected int		step;
	protected int		sequence;
	protected AgentID	from;
	protected Type		type;
	protected T			content;

	@SuppressWarnings("hiding")
	public Message(AgentID from, Type type, T content)
	{
		this.step = Environment.getStep();
		this.sequence = Environment.getSequence();
		this.from = from;
		this.type = type;
		this.content = content;
	}

	public AgentID getFrom()
	{
		return from;
	}

	public Type getType()
	{
		return type;
	}

	public boolean isFuture()
	{
		return (step >= Environment.getStep());
	}

	@Override
	public int compareTo(Message<T> m)
	{
		if (m == null)
			throw new NullPointerException();
		if (from == null)
			return -1; // prioritize messages from the exterior (human users)
		if (type.ordinal() != m.type.ordinal())
			return type.ordinal() - m.type.ordinal();
		// TODO return something based on pressure; do not calculate interest
		// here!
		if (step != m.step)
			return this.step - m.step;
		return (this.sequence - m.sequence);
	}

	public T getContents()
	{
		return content;
	}

	@Override
	public String toString()
	{
		String stringContent = content.toString();
		return "[" + from + ":" + type + ":" + stringContent + "]";
	}
}
