package P2PAgent;

import agent.AgentID;
import base.Command;

public class CommandP2P extends Command
{
	
	private Item item = null;
	private AgentID agentID = null;
	
	public CommandP2P(CommandP2P.Action action, AgentID agentID, Item item, int time)
	{
		super(action, time);
		this.setItem(item);
		this.setAgentID(agentID);
	}
	public Item getItem()
	{
		return item;
	}

	public void setItem(Item item)
	{
		this.item = item;
	}
	public AgentID getAgentID()
	{
		return agentID;
	}
	public void setAgentID(AgentID agentID)
	{
		this.agentID = agentID;
	}
}
