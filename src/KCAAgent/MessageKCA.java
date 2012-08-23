package KCAAgent;

import java.util.Collection;

import agent.AgentID;
import base.Environment;
import base.Message;

public class MessageKCA extends Message<Collection<Fact>> implements Comparable<MessageKCA>
{
	public enum Type
	{
		DATA, // contains facts with data with content. this structure is
				// mandatory
		REQUEST, // contains facts with data
		INFORM, // contains an array of facts
	}
	
	private Type type;
	

	public MessageKCA(AgentID from, Type type, Collection<Fact> content)
	{
		super();
		this.step = Environment.getStep();
		this.sequence = Environment.getSequence();
		this.from = from;
		this.type=type;
		this.content = content;
	}
	
	@Override
	public int compareTo(MessageKCA m)
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

	@Override
	public String toString()
	{
		String stringContent = content.toString();
		return "[" + from + ":" + type + ":" + stringContent + "]";
	}

	public Type getType()
	{
		// TODO Auto-generated method stub
		return type;
	}




}
