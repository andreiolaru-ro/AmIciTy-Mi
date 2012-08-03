package scenario;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import KCAAgent.DataContent;
import KCAAgent.Fact;
import KCAAgent.KCAAgent;
import KCAAgent.Logix;
import KCAAgent.Specialty;
import XMLParsing.XMLParser;
import XMLParsing.XMLTree;
import XMLParsing.XMLTree.XMLNode;
import agent.AgentID;
import agent.Location;
import base.Command;

public class KCAScenario extends AbstractScenario {
	
	private final static String			SCHEMA_FILE_NAME	= "test/KCASchema.xsd";

	private double						x;
	private double						y;
	private double						width;
	private double						height;

	private Map<AgentID, KCAAgent>		agents				= new HashMap<AgentID, KCAAgent>();

	private Map<String, DataContent>	datamap				= new HashMap<String, DataContent>();
	private DataContent[]				data;

	private Command[]					commands;

	private SortedSet<Command>			commandset			= new TreeSet<Command>(
			new Comparator<Command>() {
				@Override
				public int compare(
						Command c1,
						Command c2) {
					if (c1.getTime() != c2
							.getTime()) {
						return c1.getTime()
								- c2.getTime();
					}
					return c1.hashCode()
							- c2.hashCode();
				}
			});


	/**
	 * Parse a scenario from an XML file. Check its validity with an XSD schema.
	 * 
	 * @param scenarioFileName
	 *            scenario file name load.
	 */
	public KCAScenario(String scenarioFileName) {

		// parse scenario
		XMLTree scenarioXMLNode = XMLParser.validateParse(SCHEMA_FILE_NAME, scenarioFileName);
		// it transforms each XMLNodes in KCANodes
		// permit to call getParameters() function
		// getParameters() permit to generate random values
		XMLTree scenario = getScenarioTree(scenarioXMLNode);

		/*************************************** seed random ***************************************/
		// attributes are always String with XMLParser
		String stringSeed = scenario.getRoot().getAttributeValue("seed");
		
		// speed specified in the xml
		if(stringSeed != null){
			seed = Long.parseLong(stringSeed);
			AbstractScenario.initRandom(seed);
		}
		// read seed in test/seed
		else{
			AbstractScenario.initRandom();
		}

		/********************************* map : coordinates + size ********************************/

		x = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("characteristic")
				.getFirstNode("coordinates").getFirstNode("x").getValue()).doubleValue();
		x = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("characteristic")
				.getFirstNode("coordinates").getFirstNode("y").getValue()).doubleValue();
		width = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("characteristic")
				.getFirstNode("size").getFirstNode("width").getValue()).doubleValue();
		height = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("characteristic")
				.getFirstNode("size").getFirstNode("height").getValue()).doubleValue();

		/****************************** duration : number of steps ********************************/
		// CHANGE DURATION RATHER IN ELEMENT THAN ATTRIBUTE
		// attributes are always String
		String steps = scenario.getRoot().getNodeIterator("timeline").next()
				.getAttributeValue("duration");
		try {
			nsteps = Integer.parseInt(steps);
		} catch (NumberFormatException ex) {
			// TODO: handle exception
			// should be never called (because xsd schema checks already if it's
			// an integer or not)
			assert false : steps;
		}

		/********************************** creation of agents ************************************/
		// coordinates are randomly generated, depending of the value of first,
		// last and count
		// actually, Strings are Double : need to parse them into double
		List<String> coordinatesX = ((ScenarioNode) scenario.getRoot().getFirstNode("map")
				.getFirstNode("agent").getFirstNode("location").getFirstNode("x")).getParameters()
				.getValues();

		List<String> coordinatesY = ((ScenarioNode) scenario.getRoot().getFirstNode("map")
				.getFirstNode("agent").getFirstNode("location").getFirstNode("y")).getParameters()
				.getValues();

		for (String stringX : coordinatesX) {
			double doubleX = Double.parseDouble(stringX);
			for (String stringY : coordinatesY) {
				double doubleY = Double.parseDouble(stringY);
				AgentID id = new AgentID(doubleX, doubleY);
				agents.put(id, new KCAAgent(null, id, new Location(doubleX, doubleY),
						Logix.agentCapacity, nsteps));
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

		/********************************** creation of events ***********************************/

		Map<String, ScenarioNode> specifications;
		Iterator<XMLNode> events = scenario.getRoot().getFirstNode("timeline")
				.getNodeIterator("event");

		while (events.hasNext()) {

			XMLNode event = events.next();
			// return path-> node for each node which have value or parameters
			// example path : domain/a or location/x
			// usefull for foreach
			specifications = getEventSpecificationsPath(event, "");

			// generate events with every values
			List<Map<String, String>> values = generateValues(specifications);

			// put all these events in commandset
			for (Map<String, String> value : values) {
				String dname = value.get("domain/a")
						+ value.get("domain/b") + value.get("domain/c");
				if (!datamap.containsKey(dname))
					datamap.put(dname, new DataContent(datamap.keySet().size()));
				
				try {
					commandset.add(new Command(Command.Action.INJECT, new Location(new Double(Math
							.round(Double.parseDouble(value.get("location/x"))))
					.doubleValue(), new Double(Math.round(Double.parseDouble(value
							.get("location/y")))).doubleValue()), new Fact(null, datamap
									.get(dname), 0)
					.setPressure(Float.parseFloat(value.get("pressure")))
					.setPersistence(Float.parseFloat(value.get("persistence")))
					.setSpecialty(
							new Specialty(
									Double.parseDouble(value.get("domain/a")),
									Double.parseDouble(value.get("domain/b")),
									Double.parseDouble(value.get("domain/c")))),
									new Double(Double.parseDouble(value.get("time"))).intValue()));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		data = datamap.values().toArray(new DataContent[datamap.values().size()]);
		commands = commandset.toArray(new Command[commandset.size()]);

		// FIXME this is useless, generated data has consecutive ids
		Arrays.sort(data, new DataContent.DataContentComparator());
		System.out.println(data.length);
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

	public Map<AgentID, KCAAgent> getAgents() {
		assert agents != null;
		return agents;
	}

	public Command[] getCommands() {
		assert commands != null;
		return commands;
	}

	public DataContent[] getData() {
		assert data != null : data;
		return data;
	}

}
