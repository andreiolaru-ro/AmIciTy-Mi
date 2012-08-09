package P2PAgent;

import logging.Logger;
import scenario.P2PScenario;
import agent.AgentID;
import base.Environment;


public class EnvironmentP2P extends Environment<SimulationP2P, P2PAgent>
{
	private SimulationP2P parent;
	
	public EnvironmentP2P(SimulationP2P parent, P2PScenario scenario)
	{
		this.parent=parent;
		logger = new Logger();
		
		agents= scenario.getAgents();
		
		// we initialize the agents
		for(P2PAgent agent : agents.values()){
			agent.setParent(this);
			agent.setContacts(scenario.getContacts().get(agent.getId()));
		}
	}

	public AgentID injectItem(AgentID agentID, Item item)
	{
		agents.get(agentID).getItems().add(item);
		return agentID;
	}
	
	public AgentID injectItemWanted(AgentID agentID, Item itemWanted)
	{
		agents.get(agentID).getItemsWanted().add(itemWanted);
		return agentID;
	}
}
