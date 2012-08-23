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
	 * Indicated if the agent is in pause in the simulation or not. 
	 */
	protected boolean  pause ;
	protected abstract void agentPrint();
	public abstract void step() throws Exception;
	protected abstract void sendMessage(AgentID to, Message<?> msg);
	public abstract void receiveMessage(Message<?> message);
	
	//graphical functions to select an agent on the grid
	public abstract boolean isSelected();
	public abstract void toggleSelected();	
	
	public AbstractAgent(AgentID id) {
		super();
		this.id = id;
		this.pause = false ;
		this.log = new Log(this);
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
