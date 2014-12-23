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
package P2PAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import base.agent.AgentID;
import base.scenario.AbstractScenario;
import base.scenario.ScenarioNode;
import XMLParsing.XMLTree.XMLNode;

/**
 * Scenario of the {@link SimulationP2P}.
 * 
 * @author Alexandre Hocquard
 * 
 */
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

			// set of the contacts
			Set<AgentID> contactsCurrentAgent = new TreeSet<AgentID>();

			int numberContacts = AbstractScenario.rand().nextInt(numberContactsMax + 1);
			while (numberContacts < numberContactsMin) {
				numberContacts = AbstractScenario.rand().nextInt(numberContactsMax + 1);
			}

			for (int j = 0; j < numberContacts; j++) {
				AgentID idContact = saveID.get(new Integer(AbstractScenario.rand().nextInt(
						numberAgents)).intValue());
				// check if it's not the current agent and it's not already put
				// in the set
				while (currentAgentId.hashCode() == idContact.hashCode()
						|| contactsCurrentAgent.add(idContact) == false) {
					idContact = saveID.get(new Integer(AbstractScenario.rand()
							.nextInt(numberAgents)).intValue());
				}
				contactsCurrentAgent.add(idContact);
			}
			contacts.put(currentAgentId, contactsCurrentAgent);
		}

		// Map path for a node -> node
		// useful for the foreach implementation
		Map<String, ScenarioNode> parameters;

		// create items OWNED for an agent
		Iterator<XMLNode> ownedItems = scenario.getRoot().getFirstNode("timeline")
				.getFirstNode("items").getNodeIterator("owned");
		// FIXME: cheat
		TreeMap<Integer, Item> prev = new TreeMap<Integer, Item>();
		while (ownedItems.hasNext()) {
			XMLNode item = ownedItems.next();
			parameters = getParametersPath(item, "");
			List<Map<String, String>> values = generateValues(parameters);
			Integer intItem;
			for (Map<String, String> value : values) {
				intItem = new Integer(
						new Double(Double.parseDouble(value.get("idItem"))).intValue());
				if (!prev.containsKey(intItem))
					prev.put(intItem, new Item(intItem.intValue()));

				commandset.add(new CommandP2P(CommandP2P.Action.INJECT_ITEM,
						new AgentID(new Integer(
								new Double(Double.parseDouble(value.get("idAgent"))).intValue())),
						prev.get(intItem), new Double(Double.parseDouble(value.get("time")))
								.intValue()));
			}
		}

		// create items WANTED for an agent
		Iterator<XMLNode> wantedItems = scenario.getRoot().getFirstNode("timeline")
				.getFirstNode("items").getNodeIterator("wanted");

		while (wantedItems.hasNext()) {
			XMLNode item = wantedItems.next();
			parameters = getParametersPath(item, "");
			List<Map<String, String>> values = generateValues(parameters);
			Integer intItem;
			for (Map<String, String> value : values) {
				intItem = new Integer(
						new Double(Double.parseDouble(value.get("idItem"))).intValue());
				if (!prev.containsKey(intItem)) {
					prev.put(new Integer(intItem.intValue()), new Item(intItem.intValue()));
				}
				commandset.add(new CommandP2P(CommandP2P.Action.INJECT_ITEM_WANTED,
						new AgentID(new Integer(
								new Double(Double.parseDouble(value.get("idAgent"))).intValue())),
						prev.get(intItem), new Double(Double.parseDouble(value.get("time")))
								.intValue()));
			}
		}
		System.out.println(prev);

		commands = commandset.toArray(new CommandP2P[commandset.size()]);
	}

	public Map<AgentID, Set<AgentID>> getContacts() {
		return contacts;
	}

}
