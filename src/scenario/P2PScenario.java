package scenario;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import P2PAgent.P2PAgent;
import P2PAgent.Item;

import agent.AgentID;


public class P2PScenario {

	Map<AgentID, P2PAgent> agents = new HashMap<AgentID, P2PAgent>();
	Map<AgentID,Set<AgentID>> contacts = new HashMap<AgentID, Set<AgentID>>();
	Map<AgentID, Set<Item>> items = new HashMap<AgentID, Set<Item>>();
	Map<AgentID, Set<Item>> itemsWanted = new HashMap<AgentID, Set<Item>>();
	
	public P2PScenario(String scenarioFileName)
	{
		//
	}
	
	public Map<AgentID, P2PAgent> getAgents(){
		assert agents != null;
		return agents;
	}
	
	public Map<AgentID, Set<AgentID>> getContacts(){
		assert contacts != null;
		return contacts;
	}
	
	public Map<AgentID, Set<Item>> getItems(){
		assert items != null;
		return items;
	}
	
	public Map<AgentID, Set<Item>>  getItemsWanted(){
		assert itemsWanted != null;
		return itemsWanted;
	}
	
	
	
}
