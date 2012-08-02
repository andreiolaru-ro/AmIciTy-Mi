package base;

import java.util.Collection;

import KCAAgent.Fact;
import agent.AgentID;

public class Message implements Comparable<Message>
{
	public enum Type {
		DATA,		// contains facts with data with content. this structure is mandatory
		REQUEST,	// contains facts with data
		INFORM,		// contains an array of facts
	}
	// in request and data messages, the agents in Facts will not be used (for now)
	
	private int          				step;
	private int sequence;
	private AgentID						from;
	private Type						type;
	
	private Collection<Fact>			contentF;
	
	public Message(@SuppressWarnings("hiding") AgentID from, @SuppressWarnings("hiding") Type type, @SuppressWarnings("hiding") Collection<Fact> contentF)
	{
		this.step = Environment.getStep();
		this.sequence = Environment.getSequence();
		this.from = from;
		this.type = type;
		
		this.contentF = contentF;
	}
	
	public AgentID getFrom() {
		return from;
	}
	
	public Type getType() {
		return type;
	}
	
	public Collection<Fact> getFacts() {
		return contentF;
	}
	
	public boolean isFuture()
	{
		return (step >= Environment.getStep());
	}
	
	@Override
	public String toString()
	{
		String content = contentF.toString();
		return "[" + from + ":" + type + ":" + content + "]";
	}
	
	@Override
	public int compareTo(Message m)
	{
		if(m == null)
			throw new NullPointerException();
		if(from == null)
			return -1;		// prioritize messages from the exterior (human users)
		if(type.ordinal() != m.type.ordinal())
			return type.ordinal() - m.type.ordinal();
		// TODO return something based on pressure; do not calculate interest here!
		if(step != m.step)
			return this.step - m.step;
		return (this.sequence - m.sequence);
	}
}
