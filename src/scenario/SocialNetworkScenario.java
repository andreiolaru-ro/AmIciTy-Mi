package scenario;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import socialNetwork.CommandSocialNetwork;
import socialNetwork.SocialNetworkAgent;
import KCAAgent.KCAAgent;
import KCAAgent.Logix;
import XMLParsing.XMLTree.XMLNode;
import agent.AgentID;
import agent.Location;

public class SocialNetworkScenario extends AbstractLocationScenario<SocialNetworkAgent, CommandSocialNetwork>{


	private final static String			SCHEMA_FILE_NAME	= "schemas/agent/socialNetworkSchema.xsd";

	public SocialNetworkScenario(String scenarioFileName) {
		super(SCHEMA_FILE_NAME, scenarioFileName);
		
		/********************************** creation of agents ************************************/
		// coordinates are randomly generated, 
		// actually, Strings are Double : need to parse them into double

		Iterator<XMLNode> iAgents = scenario.getRoot().getFirstNode("map").getNodeIterator("agent");

		while (iAgents.hasNext()) {

			XMLNode currentAgentLocation = iAgents.next();
			List<String> coordinatesX = ((ScenarioNode) currentAgentLocation.getFirstNode("location").getFirstNode("x")).getParameters()
					.getValues();
			List<String> coordinatesY = ((ScenarioNode) currentAgentLocation.getFirstNode("location").getFirstNode("y")).getParameters()
					.getValues();

			for (String stringX : coordinatesX) {
				double doubleX = Double.parseDouble(stringX);
				for (String stringY : coordinatesY) {
					double doubleY = Double.parseDouble(stringY);
					AgentID id = new AgentID(doubleX, doubleY);
					agents.put(id, new SocialNetworkAgent(id, new Location(doubleX, doubleY)));
				}
			}
		}

		/***************************************** pause area ****************************************/
		Iterator<XMLNode> selections = scenario.getRoot().getFirstNode("timeline").getNodeIterator("selection");

		while (selections.hasNext()) {
			XMLNode selection = selections.next();
			Double xCenter = (Double) selection.getFirstNode("area").getFirstNode("centerCoordinates").getFirstNode("x").getValue() ;
			Double yCenter = (Double) selection.getFirstNode("area").getFirstNode("centerCoordinates").getFirstNode("y").getValue() ;
			Location center = new Location(xCenter.doubleValue(), yCenter.doubleValue());

			String function = selection.getFirstNode("area").getFirstNode("areaFunction").getFirstNode("function").getValue().toString();
			int step =  ((Double) selection.getFirstNode("time").getValue()).intValue() ;

			Set<AgentID> a = getAgentInArea(function, center, step);

			for(AgentID aId : a){
				System.out.println(aId);
			}
		}

	}


	public static void main(String[] args) {
		SocialNetworkScenario social = new SocialNetworkScenario("scenarios/socialNetworkScenario.xml");
	}
}
