package scenario;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import P2PAgent.CommandP2P;
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

		// create agents
		// index started at 0
		for (int i = 0; i < numberAgents; i++) {
			AgentID id = new AgentID(new Integer(i).toString());
			agents.put(id, new P2PAgent(null, id));
		}

		// create neighbors for each agents
		for (AgentID currentAgentId : agents.keySet()) {
			
			Set<AgentID> contactsCurrentAgent = new TreeSet<AgentID>();
			
			int numberContacts = AbstractScenario.rand().nextInt(numberContactsMax+1);
			while(numberContacts < numberContactsMin){
				numberContacts = AbstractScenario.rand().nextInt(numberContactsMax+1);
			}
			
			for (int j = 0; j < numberContacts; j++) {
				AgentID idContact = new AgentID(new Integer(AbstractScenario.rand().nextInt(numberAgents)).toString());
				// verification if it's not the current agent and it's not already put in the set
				while (currentAgentId.hashCode() == idContact.hashCode() 
						|| contactsCurrentAgent.add(idContact) == false) {
					idContact = new AgentID(new Integer(AbstractScenario.rand().nextInt(numberAgents)).toString());
				}
				contactsCurrentAgent.add(idContact);
			}
			contacts.put(currentAgentId, contactsCurrentAgent) ;
		}
		
		
		// Map path for a node -> node
		// useful for the foreach implementation
		 Map<String, ScenarioNode> parameters;
		 
		// create items wanted for an agent
		 Iterator<XMLNode> wantedItems = scenario.getRoot().getFirstNode("timeline").getFirstNode("items")
				 .getNodeIterator("wanted");
		 while (wantedItems.hasNext()) {

			 XMLNode item = wantedItems.next();
			 parameters = getParametersPath(item, "");
			 List<Map<String, String>> values = generateValues(parameters);
			 System.out.println("wanted");
			 for(Map<String, String> value : values)
				 System.out.println(value);
//				 command.put(value.get("idAgent"), )			 
		 }
		 
		 
		// create items possessed for an agent
		 Iterator<XMLNode> possessedItems = scenario.getRoot().getFirstNode("timeline").getFirstNode("items")
				 .getNodeIterator("possessed");
		 
		 while (possessedItems.hasNext()) {
			 XMLNode item = possessedItems.next();
			 parameters = getParametersPath(item, "");
			 List<Map<String, String>> values = generateValues(parameters);
			 System.out.println("possessed");
			 for(Map<String, String> value : values)
				 System.out.println(value);
		 }	 
		
		
	}
	
	public Map<AgentID, Set<AgentID>> getContacts()
	{
		return contacts;
	}


	public static void main(String[] args) {
		P2PScenario p2pScenario = new P2PScenario("scenarios/p2pScenario.xml");
	}

}
