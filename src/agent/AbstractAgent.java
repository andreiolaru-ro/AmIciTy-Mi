package agent;


import java.util.Map;

import base.Message;



public abstract class AbstractAgent implements Measurable 
{
	protected AgentID id;
	protected int capacity;
	public Location location;
	protected Map<AgentID, AbstractAgent> neighbours;
	

	protected abstract void agentPrint();
	protected abstract void sendMessage(AgentID agId, Message msg);
	protected abstract void receiveMessage(Message msg);
	public abstract void step();
	
	protected void addNeighbour(AbstractAgent newNeighbour)
	{
		neighbours.put(newNeighbour.getId(), newNeighbour);
	}
	
	
	
	public int getCapacity()
	{
		return capacity;
	}
	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}
	protected Location getLocation()
	{
		return location;
	}
	protected void setLocation(Location location)
	{
		this.location = location;
	}
	public Map<AgentID, AbstractAgent> getNeighbours()
	{
		return neighbours;
	}
	public void setNeighbours(Map<AgentID, AbstractAgent> neighbours)
	{
		this.neighbours = neighbours;
	}
	public AgentID getId()
	{
		return id;
	}
	public void setId(AgentID id)
	{
		this.id = id;
	}

}