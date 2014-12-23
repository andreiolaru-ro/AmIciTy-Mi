/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru. See the AUTHORS file for more information.
 * 
 * This file is part of AmIciTy-Mi.
 * 
 * AmIciTy-Mi is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * AmIciTy-Mi is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with AmIciTy-Mi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package base.scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import XMLParsing.XMLTree.XMLNode;
import base.Command;
import base.agent.AgentID;
import base.agent.Location;
import base.agent.LocationAgent;
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

	/**
	 * For each step, new location of the agent which are moving.
	 */
	protected Map<Integer, Map<AgentID, Location>>	movingAgents;

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
	 * Parse the size and coordinates of the map.
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
	 * @return for each step, you have a map of agents id and if they will move
	 *         or not
	 */
	// FIXME : delete consecutive agents pause two steps in a row
	protected Map<Integer, Map<AgentID, Boolean>> parsePauseEvents() {

		Iterator<XMLNode> selections = scenario.getRoot().getFirstNode("timeline")
				.getNodeIterator("selection");

		// see return javadoc
		Map<Integer, Map<AgentID, Boolean>> pauseUnpause = new HashMap<Integer, Map<AgentID, Boolean>>();

		while (selections.hasNext()) {

			XMLNode selection = selections.next();

			// functions parsing
			List<ScenarioFunction> functions = new ArrayList<ScenarioFunction>();
			Iterator<XMLNode> iteratorFunct = selection.getNodeIterator("area");

			while (iteratorFunct.hasNext()) {
				XMLNode nodeFunction = iteratorFunct.next();
				String function = nodeFunction.getFirstNode("function").getValue().toString();
				double coordX = Double.parseDouble(nodeFunction.getFirstNode("centerCoordinates")
						.getFirstNode("x").getValue().toString());
				double coordY = Double.parseDouble(nodeFunction.getFirstNode("centerCoordinates")
						.getFirstNode("y").getValue().toString());
				boolean inside = Boolean.parseBoolean(nodeFunction.getFirstNode("inside")
						.getValue().toString());
				functions.add(new ScenarioFunction(new Location(coordX, coordY), function, inside));
			}

			// duration
			int stepStart = ((Double) selection.getFirstNode("timeStart").getValue()).intValue();
			int stepEnd = nsteps;
			if (selection.getFirstNode("timeEnd") != null)
				stepEnd = ((Double) selection.getFirstNode("timeEnd").getValue()).intValue();

			// agents in the former area
			Set<AgentID> agentsFormerArea = new TreeSet<AgentID>();

			// loop on every step : area could change in function of the
			// variable step
			for (int step = stepStart; step <= stepEnd; step++) {
				Set<AgentID> agentsArea = new TreeSet<AgentID>();

				// get area and update the final area depeding of the inside
				// boolean
				// look selectionSchema.xsd comments
				for (ScenarioFunction function : functions) {
					Set<AgentID> newArea = getAgentsInArea(function.getFunction(),
							function.getCoordinates(), step);
					agentsArea = AbstractLocationScenario.mergeAreas(agentsArea, newArea,
							function.isInside());
				}

				// update new agents in the area
				for (AgentID aId : agentsArea) {
					Map<AgentID, Boolean> agentStep = new TreeMap<AgentID, Boolean>();

					agentStep.put(aId, new Boolean(true));

					if (pauseUnpause.containsKey(new Integer(step))) {
						// remove it first, anyway if it's true or false and if
						// it exists or not
						pauseUnpause.get(new Integer(step)).remove(aId);
						// then put true because PAUSE has the priority
						pauseUnpause.get(new Integer(step)).putAll(agentStep);
					} else {
						pauseUnpause.put(new Integer(step), agentStep);
					}
				}

				// update former agents if they are not anymore in the area
				for (AgentID id : agentsFormerArea) {
					if (!agentsArea.contains(id)) {
						Map<AgentID, Boolean> agentStep = new TreeMap<AgentID, Boolean>();
						agentStep.put(id, new Boolean(false));
						if (pauseUnpause.containsKey(new Integer(step))) {
							// if it exist already, do not override value
							// because pause(true) value has the priority
							if (!pauseUnpause.get(new Integer(step)).containsKey(id))
								pauseUnpause.get(new Integer(step)).putAll(agentStep);
						} else {
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
				if (pauseUnpause.containsKey(new Integer(stepEnd + 1))) {
					if (!pauseUnpause.get(new Integer(stepEnd + 1)).containsKey(id))
						pauseUnpause.get(new Integer(stepEnd + 1)).putAll(agentStep);
				} else {
					pauseUnpause.put(new Integer(stepEnd + 1), agentStep);
				}
			}
		}

		return pauseUnpause;
	}

	/**
	 * Parse moving events.
	 * 
	 * @return for each steps, a map with agents which are moving and their new
	 *         {@link Location}.
	 */
	protected Map<Integer, Map<AgentID, Location>> parseMoveEvents() {
		Iterator<XMLNode> movements = scenario.getRoot().getFirstNode("timeline")
				.getNodeIterator("movement");

		Map<Integer, Map<AgentID, Location>> moveEvents = new TreeMap<Integer, Map<AgentID, Location>>();

		while (movements.hasNext()) {
			XMLNode movement = movements.next();

			// functions parsing
			List<ScenarioFunction> functions = new ArrayList<ScenarioFunction>();
			Iterator<XMLNode> iteratorFunct = movement.getNodeIterator("area");

			while (iteratorFunct.hasNext()) {
				XMLNode nodeFunction = iteratorFunct.next();
				String function = nodeFunction.getFirstNode("function").getValue().toString();
				double coordX = Double.parseDouble(nodeFunction.getFirstNode("centerCoordinates")
						.getFirstNode("x").getValue().toString());
				double coordY = Double.parseDouble(nodeFunction.getFirstNode("centerCoordinates")
						.getFirstNode("y").getValue().toString());
				boolean inside = Boolean.parseBoolean(nodeFunction.getFirstNode("inside")
						.getValue().toString());
				functions.add(new ScenarioFunction(new Location(coordX, coordY), function, inside));
			}

			// duration
			int stepStart = ((Double) movement.getFirstNode("timeStart").getValue()).intValue();
			int stepEnd = nsteps;

			// number of leap, by default, it's the duration
			int numberLeap = stepEnd - stepStart + 1;
			int deltaLeap = 1;

			if (movement.getFirstNode("timeEnd") != null)
				stepEnd = ((Double) movement.getFirstNode("timeEnd").getValue()).intValue();
			if (movement.getFirstNode("numberLeap") != null) {
				int checkLeap = ((Double) movement.getFirstNode("numberLeap").getValue())
						.intValue();
				if (checkLeap <= numberLeap)
					numberLeap = checkLeap;

				deltaLeap = Integer.parseInt(new Long(Math.round((new Double(stepEnd - stepStart)
						.doubleValue() / new Double(numberLeap - 1).doubleValue()) - 0.5f))
						.toString());
			}

			// Never xDependance and yDependance could have a value in the same
			// time
			// look moveSchema.xsd comments for more details
			String xDependance = null;
			String yDependance = null;
			String xFunction = movement.getFirstNode("moveFunctions").getFirstNode("x")
					.getFirstNode("function").getValue().toString();
			String yFunction = movement.getFirstNode("moveFunctions").getFirstNode("y")
					.getFirstNode("function").getValue().toString();

			if (movement.getFirstNode("moveFunctions").getFirstNode("x").getFirstNode("dependsOn") != null)
				xDependance = movement.getFirstNode("moveFunctions").getFirstNode("x")
						.getFirstNode("dependsOn").getValue().toString();

			if (movement.getFirstNode("moveFunctions").getFirstNode("y").getFirstNode("dependsOn") != null)
				yDependance = movement.getFirstNode("moveFunctions").getFirstNode("y")
						.getFirstNode("dependsOn").getValue().toString();

			// area is just check at the start, and then always the same agents
			// are moving
			Set<AgentID> agentsArea = new TreeSet<AgentID>();
			for (ScenarioFunction function : functions) {
				Set<AgentID> newArea = getAgentsInArea(function.getFunction(),
						function.getCoordinates(), 0);
				agentsArea = AbstractLocationScenario.mergeAreas(agentsArea, newArea,
						function.isInside());
			}

			Location lastCenterCoordinates = new Location(functions.get(0).getCoordinates().getX(),
					functions.get(0).getCoordinates().getY());

			for (int step = stepStart; step <= stepEnd; step += deltaLeap) {

				// we move each agents relatively like the center of the area
				// moving
				Location centerCoordinates = new Location(lastCenterCoordinates);

				// x and y could depends of t, it doesn't matter
				// t would be set in the getNewValue function
				Double agentX = null;
				Double agentY = null;

				// x depends on new value of y
				if (xDependance != null) {
					agentY = getNewValue(yFunction, lastCenterCoordinates, step, height);
					lastCenterCoordinates.setY(agentY.doubleValue());
					agentX = getNewValue(xFunction, lastCenterCoordinates, step, width);
					lastCenterCoordinates.setX(agentX.doubleValue());
				}
				// y depends on new value of x
				else if (yDependance != null) {
					agentX = getNewValue(xFunction, lastCenterCoordinates, step, width);
					lastCenterCoordinates.setX(agentX.doubleValue());
					agentY = getNewValue(yFunction, lastCenterCoordinates, step, height);
					lastCenterCoordinates.setY(agentY.doubleValue());
				}
				// new value of x and y depends either of former values of x and
				// y (and t), or depends of nothing
				else {
					agentX = getNewValue(xFunction, lastCenterCoordinates, step, width);
					agentY = getNewValue(yFunction, lastCenterCoordinates, step, height);
					lastCenterCoordinates.setX(agentX.doubleValue());
					lastCenterCoordinates.setY(agentY.doubleValue());
				}

				Double deltaX = new Double(lastCenterCoordinates.getX() - centerCoordinates.getX());
				Double deltaY = new Double(lastCenterCoordinates.getY() - centerCoordinates.getY());

				// agents move exactly like the center of the area moves
				for (AgentID agent : agentsArea) {

					Location agentLocation = new Location(getLastLocation(moveEvents, agent));
					adjustLocation(agentLocation, deltaX, deltaY);

					Map<AgentID, Location> newLocation = new TreeMap<AgentID, Location>();
					newLocation
							.put(agent, new Location(agentLocation.getX(), agentLocation.getY()));

					if (moveEvents.containsKey(new Integer(step))) {
						moveEvents.get(new Integer(step)).putAll(newLocation);
					} else {
						moveEvents.put(new Integer(step), newLocation);
					}
				}
			}

		}

		return moveEvents;
	}

	/**
	 * Update the new {@link Location} of an agent. An agent can't be outside of
	 * the map. This rule have to be thought more seriously.
	 * 
	 * @param agentLocation
	 *            location to update
	 * @param deltaX
	 *            moving on axis x
	 * @param deltaY
	 *            moving on axis y
	 */
	// FIXME : an agent could maybe get outside of the map
	// for the moment it won't
	private void adjustLocation(Location agentLocation, Double deltaX, Double deltaY) {

		// set X
		if ((agentLocation.getX() + deltaX.doubleValue()) < 0)
			agentLocation.setX(0);
		else if ((agentLocation.getX() + deltaX.doubleValue()) > width)
			agentLocation.setX(width - 0.05);
		else
			agentLocation.setX(agentLocation.getX() + deltaX.doubleValue());

		// set Y
		if ((agentLocation.getY() + deltaY.doubleValue()) < 0)
			agentLocation.setY(0);
		else if ((agentLocation.getY() + deltaY.doubleValue()) > height)
			agentLocation.setY(height - 0.05);
		else
			agentLocation.setY(agentLocation.getY() + deltaY.doubleValue());
	}

	/**
	 * Get the value depending on a function. This value is bounded between a
	 * maximum value and zero. Function could depend of the the value of the
	 * former location.
	 * 
	 * @param function
	 *            String describing the function.
	 * @param formerLocation
	 *            Former location you need to update
	 * @param step
	 *            step is useful to move depending of the time
	 * @param maxValue
	 *            maximum value it could return
	 * @return the new calculate value
	 */
	private static Double getNewValue(String function, Location formerLocation, int step,
			double maxValue) {
		Double newValue = null;
		Expr expr;

		Variable variable_x = Variable.make("x");
		Variable variable_y = Variable.make("y");
		Variable variable_t = Variable.make("t");

		try {
			expr = Parser.parse(function);

			variable_x.setValue(formerLocation.getX());
			variable_y.setValue(formerLocation.getY());
			variable_t.setValue(step);
			newValue = new Double(expr.value());
			if (newValue.doubleValue() < 0)
				newValue = new Double(0);
			if (newValue.doubleValue() > maxValue) {
				// FIXME : bug without minus
				newValue = new Double(maxValue - 0.05);
			}
		} catch (SyntaxException e) {
			System.err.println(e.explain());
		}

		return newValue;
	}

	/**
	 * Get teh last location of an agent in the list. Useful to update the
	 * moving events of an agent.
	 * 
	 * @param moveEvents
	 *            List containing the moves events for every agents in an area.
	 * @param id
	 *            Id of the agents you need to get the last location
	 * @return the last location of this agent in the list
	 */

	private Location getLastLocation(Map<Integer, Map<AgentID, Location>> moveEvents, AgentID id) {
		// default
		Location newLocation = agents.get(id).getLocation();

		for (Integer step : moveEvents.keySet()) {
			Map<AgentID, Location> locations = moveEvents.get(step);
			if (locations.containsKey(id)) {
				newLocation = locations.get(id);
			}
		}

		return newLocation;
	}

	/**
	 * Merge two areas in one. If inside is at true, it will be the intersection
	 * of the two areas. If it's at false, agents in areaToMerge will be removed
	 * of teh actual area.
	 * 
	 * @param actualArea
	 *            main area
	 * @param areaToMerge
	 *            area you will merge with actualArea
	 * @param inside
	 *            specify it additions the two areas or it removes agents of
	 *            areaToMerge from actualArea
	 * @return the merging between the two areas
	 */
	private static Set<AgentID> mergeAreas(Set<AgentID> actualArea, Set<AgentID> areaToMerge,
			boolean inside) {
		Set<AgentID> newArea = new TreeSet<AgentID>(actualArea);

		for (AgentID agent : areaToMerge) {
			if (!newArea.contains(agent) && inside)
				newArea.add(agent);
			else if (newArea.contains(agent) && !inside)
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
