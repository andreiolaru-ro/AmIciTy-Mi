package scenario;

import java.util.Set;
import java.util.TreeSet;

import agent.AgentID;
import agent.Location;
import agent.LocationAgent;
import base.Command;
import expr.Expr;
import expr.Parser;
import expr.SyntaxException;
import expr.Variable;

/**
 * Agents in this scenario have a location. They can move and be paused or unpaused.
 * @author Alexandre Hocquard
 *
 * @param <A> Agents which have a {@link Location}
 * @param <C> Specific command class which extends {@link Command}
 */
public abstract class AbstractLocationScenario<A extends LocationAgent, C extends Command> extends AbstractScenario<A, C> {

	private double						x;
	private double						y;
	private double						width;
	private double						height;
	
	public AbstractLocationScenario(String schemaFileName, String scenarioFileName) {
		super(schemaFileName, scenarioFileName);
		parseMapFeatures();
	}

	/**
	 * Return a set of agents inside an area describing by a function and a center. This function is parsed with the library expr.jar.
	 * @param function function describing the area. This function is a {@link String}. 
	 * For example, a circle is on the form : x^2+y^2 < positiveInt
	 * @param center {@link Location} of the center of the area
	 * @param step Specify the time at which we apply the function. It's a positive {@link Integer}.
	 *  Actually, area could be in function of the time, addition to x and y.
	 * @return a set of the {@link LocationAgent} which are inside the area described by the function
	 */
	protected Set<AgentID> getAgentInArea(String function, Location center, int step){
		Set<AgentID> agentsInArea = new TreeSet<AgentID>();
		Expr expr;

		Variable variable_x = Variable.make("x");
		Variable variable_y = Variable.make("y");
		Variable variable_t = Variable.make("t");
		
		try {
			expr = Parser.parse(function);

			for(AgentID agentId : agents.keySet()){
				A agent = agents.get(agentId) ;
				variable_x.setValue(agent.getLocation().getX() - center.getX());
				variable_y.setValue(agent.getLocation().getY() - center.getY());
				variable_t.setValue(step);
				
				// IN == 1.0, OUT == 0.0 (double)
				if(expr.value() != 0){
					agentsInArea.add(agentId); // IN
				}
			}
		} catch (SyntaxException e) {
			System.err.println(e.explain());
		}
		return agentsInArea ;
	}
	
	
	protected void parseMapFeatures(){
		x = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("features")
				.getFirstNode("coordinates").getFirstNode("x").getValue()).doubleValue();
		y = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("features")
				.getFirstNode("coordinates").getFirstNode("y").getValue()).doubleValue();
		width = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("features")
				.getFirstNode("size").getFirstNode("width").getValue()).doubleValue();
		height = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("features")
				.getFirstNode("size").getFirstNode("height").getValue()).doubleValue();
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


