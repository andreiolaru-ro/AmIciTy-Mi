package agent;


import logging.Log;
import base.Message;

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
	/**
	 * Indicated if the agent is in pause in the scenario or not. 
	 */
	protected boolean  pause ;
	protected abstract void agentPrint();
	public abstract void step() throws Exception;
	protected abstract void sendMessage(AgentID to, Message<?> msg);
	public abstract void receiveMessage(Message<?> message);
	
	
	
	public AbstractAgent(AgentID id) {
		super();
		this.id = id;
		this.pause = false ;
	}
	
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
	
	public boolean isPause() {
		return pause ;
	}

	public void unpause() {
		this.pause = false;
	}
	
	public void pause() {
		this.pause = true ;
	}
	
}
