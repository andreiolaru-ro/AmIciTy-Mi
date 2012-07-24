package agent;


import java.util.Map;

import logging.Log;

import base.Message;

/**
 * 
 * abstract class to define a basic agent 
 *
 */
public abstract class AbstractAgent implements Measurable 
{
	protected AgentID id; 
	protected Log log;
	protected abstract void agentPrint();
	public abstract void step() throws Exception;
	

	public AgentID getId()
	{
		return id;
	}
	public void setId(AgentID id)
	{
		this.id = id;
	}

	public Log getLog()
	{
		return log;
	}
	
}