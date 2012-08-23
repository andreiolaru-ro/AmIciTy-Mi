package P2PAgent;
import agent.AgentID;
import base.Environment;
import base.Message;

public class MessageP2P<T> extends Message<T>  implements Comparable<MessageP2P<T>>
{
	public enum Type
	{
		REQUEST_ITEM,
		SEND_ITEM, // send the items requested
		SEND_LOCATION,// send the locations of the items requested
		ASK_LOCATION;// ask the location of an item for an other agent
	}
	
	private Type type;


	public MessageP2P(AgentID from, Type type, T content)
	{
		super();
		this.step = Environment.getStep();
		this.sequence = Environment.getSequence();
		this.from = from;
		this.type=type;
		this.content = content;
	}

	public Type getType()
	{
		return type;
	}

	@Override
	public int compareTo(MessageP2P<T> m)
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

}

