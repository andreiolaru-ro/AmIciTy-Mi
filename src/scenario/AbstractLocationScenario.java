package scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import XMLParsing.XMLTree.XMLNode;
import agent.AgentID;
import agent.Location;
import agent.LocationAgent;
import base.Command;
import expr.Expr;
import expr.Parser;
import expr.SyntaxException;
import expr.Variable;

/**
 * Agents in this scenario have a location. They can move and be paused or
 * unpaused.
 * 
 * @author Alexandre Hocquard
 * 
 * @param <A>
 *            Agents which have a {@link Location}
 * @param <C>
 *            Specific command class which extends {@link Command}
 */
public abstract class AbstractLocationScenario<A extends LocationAgent, C extends Command> extends
AbstractScenario<A, C> {

	private double									x;
	private double									y;
	private double									width;
	private double									height;

	/**
	 * Association between an AgentID, a step, and a boolean which could be
	 * called "inPause".
	 */
	protected Map<Integer, Map<AgentID, Boolean>>	pauseUnpauseEvents;

	protected AbstractLocationScenario(String schemaFileName, String scenarioFileName) {
		super(schemaFileName, scenarioFileName);
		parseMapFeatures();
	}

	/**
	 * Return a set of agents inside an area describing by a function and a
	 * center. This function is parsed with the library expr.jar.
	 * 
	 * @param function
	 *            function describing the area. This function is a
	 *            {@link String}. For example, a circle is on the form : x^2+y^2
	 *            < positiveInt
	 * @param center
	 *            {@link Location} of the center of the area
	 * @param step
	 *            Specify the time at which we apply the function. It's a
	 *            positive {@link Integer}. Actually, area could be in function
	 *            of the time, addition to x and y.
	 * @return a set of the {@link LocationAgent} which are inside the area
	 *         described by the function
	 */
	protected Set<AgentID> getAgentsInArea(String function, Location center, int step) {
		Set<AgentID> agentsInArea = new TreeSet<AgentID>();
		Expr expr;

		Variable variable_x = Variable.make("x");
		Variable variable_y = Variable.make("y");
		Variable variable_t = Variable.make("t");

		try {
			expr = Parser.parse(function);

			for (AgentID agentId : agents.keySet()) {

				A agent = agents.get(agentId);
				variable_x.setValue(agent.getLocation().getX() - center.getX());
				variable_y.setValue(agent.getLocation().getY() - center.getY());
				variable_t.setValue(step);

				// IN == 1.0, OUT == 0.0 (double)
				if (expr.value() != 0) {
					agentsInArea.add(agentId); // IN
				}
			}
		} catch (SyntaxException e) {
			System.err.println(e.explain());
		}
		return agentsInArea;
	}

	/**
	 * Default parsing of the size and coordinates of the map.
	 */
	protected void parseMapFeatures() {
		x = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("features")
				.getFirstNode("coordinates").getFirstNode("x").getValue()).doubleValue();
		y = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("features")
				.getFirstNode("coordinates").getFirstNode("y").getValue()).doubleValue();
		width = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("features")
				.getFirstNode("size").getFirstNode("width").getValue()).doubleValue();
		height = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("features")
				.getFirstNode("size").getFirstNode("height").getValue()).doubleValue();
	}

	/**
	 * Parse pause events. If an agent is unpause and pause at the same step,
	 * pause has the priority.
	 * 
	 * @return
	 */
	// FIXME : delete consecutive agents
	protected Map<Integer, Map<AgentID, Boolean>> parsePauseEvents() {
		Iterator<XMLNode> selections = scenario.getRoot().getFirstNode("timeline")
				.getNodeIterator("selection");

		//		Map<Map<AgentID, Integer>, Boolean> pauseUnpause = new HashMap<Map<AgentID, Integer>, Boolean>();
		Map<Integer, Map<AgentID, Boolean>> pauseUnpause = new HashMap<Integer, Map<AgentID,Boolean>>();

		while (selections.hasNext()) {
			
			XMLNode selection = selections.next();

			// functions parsing
			List<ScenarioFunction> functions = new ArrayList<ScenarioFunction>() ;
			Iterator<XMLNode> iteratorFunct = selection.getNodeIterator("area");
			
			while(iteratorFunct.hasNext()){
				XMLNode nodeFunction = iteratorFunct.next();
				String function = nodeFunction.getFirstNode("function").getValue().toString();
				double coordX = Double.parseDouble(nodeFunction.getFirstNode("centerCoordinates").getFirstNode("x").getValue().toString());
				double coordY = Double.parseDouble(nodeFunction.getFirstNode("centerCoordinates").getFirstNode("y").getValue().toString());
				boolean inside = Boolean.parseBoolean(nodeFunction.getFirstNode("inside").getValue().toString());
				functions.add(new ScenarioFunction(new Location(coordX, coordY), function, inside));
			}
			
			// duration
			int stepStart = ((Double) selection.getFirstNode("timeStart").getValue()).intValue();
			int stepEnd = nsteps;
			if (selection.getFirstNode("timeEnd") != null)
				stepEnd = ((Double) selection.getFirstNode("timeEnd").getValue()).intValue();
			

			// agents in area
			Set<AgentID> agentsArea = new TreeSet<AgentID>();
			Set<AgentID> agentsFormerArea = new TreeSet<AgentID>();

			// loop on every step : area could change in function of the
			// variable step
			for (int step = stepStart; step <= stepEnd; step++) {
				for(ScenarioFunction function : functions){
					Set<AgentID> newArea = getAgentsInArea(function.getFunction(), function.getCoordinates(), step);
					agentsArea = AbstractLocationScenario.mergeAreas(agentsArea, newArea, function.isInside());
				}

				
				// update new agents in the area
				for (AgentID aId : agentsArea) {
					Map<AgentID, Boolean> agentStep = new TreeMap<AgentID, Boolean>();
					Map<AgentID, Boolean> oldAgentStep = new TreeMap<AgentID, Boolean>();

					agentStep.put(aId, new Boolean(true));
					oldAgentStep.put(aId, new Boolean(false));

					if(pauseUnpause.containsKey(new Integer(step))){
						// remove first, anyway if it's true or false
						pauseUnpause.get(new Integer(step)).remove(aId);
						// then put true because PAUSE has the priority
						pauseUnpause.get(new Integer(step)).putAll(agentStep);
					}
					else{
						pauseUnpause.put(new Integer(step), agentStep);
					}

				}

				// update former agents if they are not anymore in the area
				for (AgentID id : agentsFormerArea) {
					if (!agentsArea.contains(id)) {
						Map<AgentID, Boolean> agentStep = new TreeMap<AgentID, Boolean>();
						agentStep.put(id, new Boolean(true));
						if(pauseUnpause.containsKey(new Integer(step))){
							if (!pauseUnpause.get(new Integer(step)).containsKey(id))
								pauseUnpause.get(new Integer(step)).putAll(agentStep);
						}
						else{
							pauseUnpause.put(new Integer(step), agentStep);
						}
					}
				}
				agentsFormerArea = agentsArea;
			}

			// at the end, unpause last agents paused
			for (AgentID id : agentsFormerArea) {
				Map<AgentID, Boolean> agentStep = new TreeMap<AgentID, Boolean>();
				agentStep.put(id, new Boolean(false));
				if(pauseUnpause.containsKey(new Integer(stepEnd+1))){
					if (!pauseUnpause.get(new Integer(stepEnd+1)).containsKey(id))
						pauseUnpause.get(new Integer(stepEnd+1)).putAll(agentStep);
				}
				else{
					pauseUnpause.put(new Integer(stepEnd+1), agentStep);
				}
			}
		}
		return pauseUnpause;
	}


	private static  Set<AgentID> mergeAreas(Set<AgentID> actualArea, Set<AgentID> areaToMerge, boolean inside) {
		Set<AgentID> newArea = new TreeSet<AgentID>(actualArea);
		
		for(AgentID agent : areaToMerge){
			if(!newArea.contains(agent) && inside)
				newArea.add(agent);
			else if(newArea.contains(agent) && !inside)
				newArea.remove(agent);
		}
		return newArea;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
}
