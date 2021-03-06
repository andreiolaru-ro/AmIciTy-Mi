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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import net.xqhs.util.XML.XMLParser;
import net.xqhs.util.XML.XMLTree;
import net.xqhs.util.XML.XMLTree.XMLNode;
import base.Command;
import base.agent.AbstractAgent;
import base.agent.AgentID;

/**
 * Commun functions and variables for each scenarios.
 * 
 * @author Alexandre Hocquard
 * 
 * @param <T>
 *            Type of the agent of the scenario
 * @param <C>
 *            Type of the commands of the scenario
 */
public class AbstractScenario<T extends AbstractAgent, C extends Command> {

	private static final String		SEED_FILE		= "test/seed";
	/**
	 * Split character use in the tag "foreach" of the xml scneario to define a
	 * path.
	 */
	private static final String		SPLIT_CHARACTER	= "/";

	protected static Random			rand;
	protected static long			seed;

	protected String				scenarioFileName;
	protected int					nsteps;
	protected C[]					commands;
	protected Map<AgentID, T>		agents			= new HashMap<AgentID, T>();

	protected XMLTree				scenario;

	protected SortedSet<Command>	commandset		= new TreeSet<Command>(
															new Comparator<Command>() {
																@Override
																public int compare(Command c1,
																		Command c2) {
																	if (c1.getTime() != c2
																			.getTime()) {
																		return c1.getTime()
																				- c2.getTime();
																	}
																	return c1.getId() - c2.getId();
																}
															});

	@SuppressWarnings("hiding")
	protected AbstractScenario(String schemaFileName, String scenarioFileName) {
		scenario = getScenarioTree(schemaFileName, scenarioFileName);
		parseSeed();
		parseDuration();
		this.scenarioFileName = scenarioFileName;
	}

	/**
	 * Association between each nodes and theirs paths, relatively to a father
	 * node.
	 * 
	 * @param event
	 *            father Node, paths are generated relatively to this node
	 *            {@link XMLNode}
	 * @param path
	 * @return
	 */
	protected Map<String, ScenarioNode> getParametersPath(XMLNode event, String path) {

		Map<String, ScenarioNode> specifications = new TreeMap<String, ScenarioNode>();
		// iterate on child
		for (XMLNode child : event.getNodes()) {

			// cast for the visibility of getParameters()
			ScenarioNode kcaChild = (ScenarioNode) child;

			// path name
			String newPath;
			// example : path = "domain" ==> "domain/a"
			// at start, path is empty or null
			if (path != null && !path.equals(""))
				newPath = path + SPLIT_CHARACTER + kcaChild.getName();
			else
				newPath = kcaChild.getName();

			// child contains a value or a set of parameters
			if (!kcaChild.getParameters().isAllValueNull()) {
				specifications.put(newPath, kcaChild);

			}
			// recursive : check if kcaChild contains or not a value or set of
			// parameters
			else if (kcaChild.getNodes().size() > 0)
				specifications.putAll(getParametersPath(kcaChild, newPath));
		}
		return specifications;
	}

	/**
	 * @param parameters
	 *            map associating a path with a node. This path is of type
	 *            Specification, which is an enum.
	 * @return a list of events. Each event is a map associating a Specification
	 *         with its value generated.
	 */
	protected List<Map<String, String>> generateValues(Map<String, ScenarioNode> parameters) {

		// initialization of the map
		List<Map<String, String>> events = new ArrayList<Map<String, String>>();
		events.add(new TreeMap<String, String>());

		for (String path : parameters.keySet()) {
			events = generateValues(path, parameters, events);
		}

		return events;
	}

	/**
	 * Generate values for one parameter. If this parameter depends of another
	 * parameter, this last will be generated recursively.
	 * 
	 * @param path
	 * @param parameters
	 * @param events
	 */
	protected List<Map<String, String>> generateValues(String path,
			Map<String, ScenarioNode> parameters, List<Map<String, String>> events) {

		List<Map<String, String>> eventsUpdate = events;
		// no need to generate : already done with recursion
		if (!events.get(0).containsKey(path)) {

			// number of values to generate, depends of the foreach
			int countNumberValues = 1;
			List<String> values = new ArrayList<String>();

			// get the number of values to generate
			for (String foreach : parameters.get(path).getParameters().getForeach()) {
				// if set of value if not generated, we should generate them
				if (!events.get(0).containsKey(foreach)) {
					// recursive
					eventsUpdate = generateValues(foreach, parameters, eventsUpdate);
				}
				Integer select = parameters.get(foreach).getParameters().getSelect();
				if (select != null)
					countNumberValues *= select.intValue();
			}
			// it generates values
			for (int i = 0; i < countNumberValues; i++) {
				values.addAll(parameters.get(path).getParameters().getValues());
			}

			// events with these new values generated
			List<Map<String, String>> newEvents = new ArrayList<Map<String, String>>();

			// number of times each tuple is duplicated
			int numberSameTuples = eventsUpdate.size() / countNumberValues;

			// number of different values generated for each tuple
			int numberValuesProduced = values.size() / countNumberValues;

			for (int i = 0; i < countNumberValues; i++) {
				for (int j = 0; j < numberSameTuples; j++) {
					for (int k = 0; k < numberValuesProduced; k++) {
						Map<String, String> eventWithNewValue = new TreeMap<String, String>(
								eventsUpdate.get(i * numberSameTuples + j));
						eventWithNewValue.put(path, values.get(i * numberValuesProduced + k));
						newEvents.add(eventWithNewValue);
					}
				}
			}
			return newEvents;
		}
		return events;
	}

	/**
	 * @param tree
	 *            tree for transformation
	 * @return a new tree with each {@link XMLNode} transformed in
	 *         {@link ScenarioNode}
	 */
	@SuppressWarnings("hiding")
	protected XMLTree getScenarioTree(String schemaFileName, String scenarioFileName) {

		// parse scenario
		XMLTree scenarioXMLNode = XMLParser.validateParse(schemaFileName, scenarioFileName);
		// it transforms each XMLNodes in scenarioNode
		// useful to call getParameters() function
		// getParameters() permit to generate random values
		XMLNode newRoot = getScenarioNodes(scenarioXMLNode.getRoot());
		return new XMLTree(newRoot);
	}

	/**
	 * Recursive function to transform an {@link XMLNode} and its children in
	 * {@link ScenarioNode}.
	 * 
	 * @param node
	 *            {@link XMLNode} to transform in {@link ScenarioNode}
	 * @return a {@link ScenarioNode}, with all child nodes transformed in
	 *         {@link ScenarioNode} too.
	 */
	protected ScenarioNode getScenarioNodes(XMLNode node) {
		ScenarioNode newNode = new ScenarioNode(node);

		for (int i = 0; i < newNode.getNodes().size(); i++)
			newNode.getNodes().set(i, getScenarioNodes(newNode.getNodes().get(i))); // recursive

		return newNode;
	}

	/**
	 * Read seed in the file "test/seed".
	 */
	public static void initRandom() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(SEED_FILE));
			initRandom(Long.parseLong(br.readLine()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// create random with the variable "seed"
	@SuppressWarnings("hiding")
	public static void initRandom(long seed) {
		AbstractScenario.seed = seed;
		rand = new Random(seed);
	}

	public static void resetRandom() {
		seed = new Random().nextLong();
		rand = new Random(seed);
		try {
			PrintWriter pw = new PrintWriter(SEED_FILE);
			pw.print(seed);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse the seed in the xml scenario. If it's not specified, it will read
	 * it in test/seed.
	 */
	protected void parseSeed() {
		// attributes are always String with XMLParser
		String stringSeed = scenario.getRoot().getAttributeValue("seed");

		// seed specified in the xml
		if (stringSeed != null) {
			seed = Long.parseLong(stringSeed);
			AbstractScenario.initRandom(seed);
		}
		// or not : read seed value in test/seed
		else {
			AbstractScenario.initRandom();
			updateSeedToRoot();
		}
	}

	/**
	 * Update teh seed in the scenario. Useful to save function.
	 */
	protected void updateSeedToRoot() {
		scenario.getRoot().setAttribute("seed", new Long(seed).toString());
	}

	/**
	 * Default parsing of the duration.
	 */
	protected void parseDuration() {
		// attributes are always String
		String steps = scenario.getRoot().getNodeIterator("timeline").next()
				.getAttributeValue("duration");
		nsteps = Integer.parseInt(steps);
	}

	// FIXME : it doesn't work for the moment
	public void save(File file) {
		updateSeedToRoot();
		XMLParser.save(file, scenario);
	}

	public static Long getSeed() {
		return new Long(seed);
	} // copy

	public static Random rand() {
		return rand;
	}

	public int getNsteps() {
		return nsteps;
	}

	public Map<AgentID, T> getAgents() {
		assert agents != null;
		return agents;
	}

	public C[] getCommands() {
		assert commands != null;
		return commands;
	}
}
