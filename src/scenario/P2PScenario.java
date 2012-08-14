package scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import P2PAgent.CommandP2P;
import P2PAgent.Item;
import P2PAgent.P2PAgent;
import XMLParsing.XMLTree.XMLNode;
import agent.AgentID;

public class P2PScenario extends AbstractScenario<P2PAgent, CommandP2P> {


	private final static String				SCHEMA_FILE_NAME	= "schemas/agent/p2pSchema.xsd";
	protected Map<AgentID, Set<AgentID>>	contacts			= new HashMap<AgentID, Set<AgentID>>();


	public P2PScenario(String scenarioFileName) {
		
		super(SCHEMA_FILE_NAME, scenarioFileName);
		int numberAgents = ((Double) scenario.getRoot().getFirstNode("agent")
				.getFirstNode("numberAgents").getValue()).intValue();

		int numberContactsMin = ((Double) scenario.getRoot().getFirstNode("agent")
				.getFirstNode("numberContactsMin").getValue()).intValue();

		int numberContactsMax = ((Double) scenario.getRoot().getFirstNode("agent")
				.getFirstNode("numberContactsMax").getValue()).intValue();

		List<AgentID> saveID = new ArrayList<AgentID>(numberAgents);
		// create agents
		// index started at 0
		for (int i = 0; i < numberAgents; i++) {
			AgentID id = new AgentID(new Integer(i).toString());
			agents.put(id, new P2PAgent(null, id));
			saveID.add(id);
		}



		// create neighbors for each agents
		for (AgentID currentAgentId : agents.keySet()) {

			Set<AgentID> contactsCurrentAgent = new TreeSet<AgentID>();

			int numberContacts = AbstractScenario.rand().nextInt(numberContactsMax+1);
			while(numberContacts < numberContactsMin){
				numberContacts = AbstractScenario.rand().nextInt(numberContactsMax+1);
			}

			for (int j = 0; j < numberContacts; j++) {
				AgentID idContact = saveID.get(new Integer(AbstractScenario.rand().nextInt(numberAgents)).intValue());
				// verification if it's not the current agent and it's not already put in the set
				while (currentAgentId.hashCode() == idContact.hashCode() 
						|| contactsCurrentAgent.add(idContact) == false) {
					idContact = saveID.get(new Integer(AbstractScenario.rand().nextInt(numberAgents)).intValue());
				}
				contactsCurrentAgent.add(idContact);
			}
			contacts.put(currentAgentId, contactsCurrentAgent) ;
		}


		// Map path for a node -> node
		// useful for the foreach implementation
		Map<String, ScenarioNode> parameters;


		// create items OWNED for an agent
		Iterator<XMLNode> possessedItems = scenario.getRoot().getFirstNode("timeline").getFirstNode("items")
				.getNodeIterator("possessed");
		// FIXME: cheat
		TreeMap<Integer, Item> prev = new TreeMap<Integer, Item>();
		while (possessedItems.hasNext()) {
			XMLNode item = possessedItems.next();
			parameters = getParametersPath(item, "");
			List<Map<String, String>> values = generateValues(parameters);
			Integer intItem;
			for(Map<String, String> value : values)
			{
				intItem = new Integer(new Double(Double.parseDouble(value.get("idItem"))).intValue());
				if(!prev.containsKey(intItem))
					prev.put(intItem, new Item(intItem.intValue()));
				
				commandset.add(new CommandP2P(CommandP2P.Action.INJECT_ITEM, new AgentID(new Integer(new Double(Double.parseDouble(value.get("idAgent"))).intValue()))
				, prev.get(intItem) 
				,new Double(Double.parseDouble(value.get("time"))).intValue())); 
			}
		}	 


		// create items WANTED for an agent
		Iterator<XMLNode> wantedItems = scenario.getRoot().getFirstNode("timeline").getFirstNode("items")
				.getNodeIterator("wanted");

		while (wantedItems.hasNext()) {
			XMLNode item = wantedItems.next();
			parameters = getParametersPath(item, "");
			List<Map<String, String>> values = generateValues(parameters);
			Integer intItem;
			for(Map<String, String> value : values)
			{
				intItem= new Integer(new Double(Double.parseDouble(value.get("idItem"))).intValue());
				if(!prev.containsKey(intItem))
				{
					prev.put(new Integer(intItem.intValue()), new Item(intItem.intValue()));
				}
				commandset.add(new CommandP2P(CommandP2P.Action.INJECT_ITEM_WANTED, new AgentID(new Integer(new Double(Double.parseDouble(value.get("idAgent"))).intValue()))
				, prev.get(intItem) 
				,new Double(Double.parseDouble(value.get("time"))).intValue())); 
			}
		}
		System.out.println(prev);

		commands = commandset.toArray(new CommandP2P[commandset.size()]);
	}

	public Map<AgentID, Set<AgentID>> getContacts()
	{
		return contacts;
	}

	public static void main(String[] args) {
		P2PScenario p2pScenario = new P2PScenario("scenarios/p2pScenario.xml");
	}

}
