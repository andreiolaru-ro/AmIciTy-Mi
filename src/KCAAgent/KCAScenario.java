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
package KCAAgent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.xqhs.util.XML.XMLTree.XMLNode;
import base.Command;
import base.agent.AgentID;
import base.agent.Location;
import base.scenario.AbstractLocationScenario;
import base.scenario.ScenarioNode;

/**
 * Scenario of the {@link SimulationKCA}.
 * 
 * @author Alexandre Hocquard
 * 
 */
public class KCAScenario extends AbstractLocationScenario<KCAAgent, CommandKCA> {

	private final static String			SCHEMA_FILE_NAME	= "schemas/agent/kcaSchema.xsd";

	private Map<String, DataContent>	datamap				= new HashMap<String, DataContent>();
	private DataContent[]				data;

	/**
	 * Parse a scenario from an XML file. Check its validity with an XSD schema.
	 * 
	 * @param scenarioFileName
	 *            scenario file name load.
	 */
	public KCAScenario(String scenarioFileName) {

		super(SCHEMA_FILE_NAME, scenarioFileName);
		System.out.println(scenario);
		// XMLParser.save("scenarios/ooo.xml", scenario);
		/********************************** creation of agents ************************************/
		// coordinates are randomly generated,
		// actually, Strings are Double : need to parse them into double

		Iterator<XMLNode> iAgents = scenario.getRoot().getFirstNode("map").getNodeIterator("agent");

		while (iAgents.hasNext()) {

			XMLNode currentAgentLocation = iAgents.next();
			List<String> coordinatesX = ((ScenarioNode) currentAgentLocation.getFirstNode(
					"location").getFirstNode("x")).getParameters().getValues();
			List<String> coordinatesY = ((ScenarioNode) currentAgentLocation.getFirstNode(
					"location").getFirstNode("y")).getParameters().getValues();

			for (String stringX : coordinatesX) {
				double doubleX = Double.parseDouble(stringX);
				for (String stringY : coordinatesY) {
					double doubleY = Double.parseDouble(stringY);
					AgentID id = new AgentID(doubleX, doubleY);
					agents.put(id, new KCAAgent(null, id, new Location(doubleX, doubleY),
							Logix.agentCapacity, nsteps));
				}
			}
		}

		// for each agent, history initialization of speciality
		Iterator<?> it;
		Map.Entry<?, ?> p;
		KCAAgent ag;
		it = agents.entrySet().iterator();
		while (it.hasNext()) {
			p = (Map.Entry<?, ?>) it.next();
			ag = (KCAAgent) p.getValue();
			ag.setHistory(nsteps);
		}

		/******************************** creation of pause events ********************************/
		// need to have agents created before
		pauseUnpauseEvents = parsePauseEvents();

		for (Integer step : pauseUnpauseEvents.keySet()) {
			Map<AgentID, Boolean> inPause = pauseUnpauseEvents.get(step);
			for (AgentID id : inPause.keySet()) {
				if (inPause.get(id).booleanValue()) {
					commandset.add(new CommandKCA(Command.Action.PAUSE, agents.get(id), step
							.intValue()));
				} else {
					commandset.add(new CommandKCA(Command.Action.UNPAUSE, agents.get(id), step
							.intValue()));
				}
			}
		}

		/******************************** movements implementation *********************************/
		// need to have agents created before too
		movingAgents = parseMoveEvents();

		for (Integer step : movingAgents.keySet()) {
			Map<AgentID, Location> locations = movingAgents.get(step);
			for (AgentID id : locations.keySet()) {
				Location location = locations.get(id);
				commandset.add(new CommandKCA(Command.Action.MOVE, agents.get(id), location, step
						.intValue()));
			}
		}

		/******************************** creation of inject events *******************************/

		Map<String, ScenarioNode> parameters;
		Iterator<XMLNode> events = scenario.getRoot().getFirstNode("timeline")
				.getNodeIterator("event");

		while (events.hasNext()) {

			XMLNode event = events.next();
			// return path-> node for each node which have value or parameters
			// example path : domain/a or location/x
			// usefull for foreach
			parameters = getParametersPath(event, "");

			// generate events with every values
			List<Map<String, String>> values = generateValues(parameters);

			// put all these events in commandset
			for (Map<String, String> value : values) {
				String dname = value.get("domain/a") + value.get("domain/b")
						+ value.get("domain/c");
				if (!datamap.containsKey(dname))
					datamap.put(dname, new DataContent(datamap.keySet().size()));

				try {
					commandset.add(new CommandKCA(Command.Action.INJECT, new Location(new Double(
							Math.round(Double.parseDouble(value.get("location/x")))).doubleValue(),
							new Double(Math.round(Double.parseDouble(value.get("location/y"))))
									.doubleValue()), new Fact(null, datamap.get(dname), 0)
							.setPressure(Float.parseFloat(value.get("pressure")))
							.setPersistence(Float.parseFloat(value.get("persistence")))
							.setSpecialty(
									new Specialty(Double.parseDouble(value.get("domain/a")), Double
											.parseDouble(value.get("domain/b")), Double
											.parseDouble(value.get("domain/c")))), new Double(
							Double.parseDouble(value.get("time"))).intValue()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		data = datamap.values().toArray(new DataContent[datamap.values().size()]);
		commands = commandset.toArray(new CommandKCA[commandset.size()]);

		// FIXME this is useless, generated data has consecutive ids
		Arrays.sort(data, new DataContent.DataContentComparator());

	}

	public DataContent[] getData() {
		assert data != null : data;
		return data;
	}

}
