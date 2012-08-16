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
	/**
	 * Number of times agent has been paused. For example, an agent could be paused in two different areas. 
	 * When the first area is unpaused, agent is still in pause because the second area is not not yet unpaused.
	 */
	protected int  pause ;
	protected abstract void agentPrint();
	public abstract void step() throws Exception;
	protected abstract void sendMessage(AgentID to, Message<?> msg);
	public abstract void receiveMessage(Message<?> message);
	
	
	
	public AbstractAgent(AgentID id) {
		super();
		this.id = id;
		this.pause = 0 ;
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
		return (pause > 0) ;
	}

	public void unpause() {
		this.pause--;
	}
	
	public void pause() {
		this.pause++ ;
	}
	
	public void resetPause() {
		this.pause = 0 ;
	}
	
}
