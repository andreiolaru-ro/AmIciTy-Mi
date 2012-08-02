package agent;


import base.Message;
import logging.Log;

/**
 * 
 * abstract class to define a basic agent 
 *
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractAgent implements Measurable 
{
	protected AgentID id; 
	protected Log log;
	protected abstract void agentPrint();
	public abstract void step() throws Exception;
	protected abstract void sendMessage(AgentID to, Message<?> msg);
	public abstract void receiveMessage(Message<?> message);
	
	public AgentID getId()
	{
		return id;
	}
	@SuppressWarnings("hiding")
	public void setId(AgentID id)
	{
		this.id = id;
	}

	public Log getLog()
	{
		return log;
	}
	
	
}