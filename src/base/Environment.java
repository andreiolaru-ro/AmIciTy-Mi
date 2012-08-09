package base;

import graphics.UpdateListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import agent.AbstractAgent;
import agent.AgentID;

import logging.Logger;

public abstract class Environment<SIMULATION extends Simulation<?,?>, AGENT extends AbstractAgent>
{
	protected SIMULATION parent;
	protected Logger logger;
	protected static int step = 0;
	protected List<AbstractAgent> selected;
	Collection<UpdateListener> listeners = new ArrayList<UpdateListener>();
	protected Map<AgentID, AGENT> agents;
	// sequence (sub-step) number for messages, used for comparing messages
	protected static int sequence = 0;
	
	
	
	//FIXME shouldn't the cells be updating simultaneously?
	public void step() throws Exception {
		for (AGENT agent : agents.values()) {
			agent.step();
		}
		//doUpdate();

		step++;
		//sequence = 0;
	}
	
	public void addSelected(AbstractAgent agent) {
		selected.add(agent);
//		logger.addLog(agent.getLog());
	}
	
	public void removeSelected(AbstractAgent agent) {
		selected.remove(agent);
//		logger.removeLog(agent.getLog());
	}
	
	public List<AbstractAgent> getSelected() {
		return selected;
	}
	
	public Logger getLogger() {
		return logger;
	}

	public static int getStep()
	{
		return step;
	}
	
	public void addUpdateListener(UpdateListener ul) {
		listeners.add(ul);
	}
	
	public void removeUpdateListener(UpdateListener ul) {
		listeners.remove(ul);
	}
	
	public void doUpdate() {
		for (UpdateListener ul : listeners) {
			ul.update();
		}
	}

	public Collection<AGENT> getAgents() {
		return agents.values();
	}
	
	public static int getSequence() {
		return sequence++;
	}
}
