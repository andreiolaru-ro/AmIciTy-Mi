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
		if(!agents.get(agentID).getItems().contains(item))
		{
			agents.get(agentID).getItems().add(item);
		}
		return agentID;
	}
	
	public AgentID injectItemWanted(AgentID agentID, Item itemWanted)
	{
		if((!agents.get(agentID).getItemsWanted().contains(itemWanted))&&(!agents.get(agentID).getItems().contains(itemWanted)))
		{
			agents.get(agentID).getItemsWanted().add(itemWanted);
		}
		return agentID;
	}
	
	public void check()
	{
		for(P2PAgent agent:agents.values())
		{
			System.out.println("id: "+agent.getId()+"contact: "+agent.getContacts()+"item: "+agent.getItems()+"itemwan: "+agent.getItemsWanted());
		}
	}
	
}

