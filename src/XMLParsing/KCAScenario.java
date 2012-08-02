package XMLParsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.transform.TransformerFactoryConfigurationError;

import KCAAgent.Fact;
import KCAAgent.KCAAgent;
import KCAAgent.Logix;
import KCAAgent.Specialty;
import XMLParsing.XMLTree.XMLNode;
import agent.AgentID;
import agent.Location;
import base.Command;
import base.DataContent;

public class KCAScenario {
	private static final String			SEED_FILE			= "test/seed";
	private static final String			SPLIT_CHARACTER		= "/";
	private final static String			SCHEMA_FILE_NAME	= "test/KCASchema.xsd";

	private static long					seed;
	private static Random				rand;

	private double						x;
	private double						y;
	private double						width;
	private double						height;
	private int							nsteps;

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

	private String						fileName;

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
			KCAScenario.initRandom(seed);
		}
		// read seed in test/seed
		else{
			KCAScenario.initRandom();
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
		List<String> coordinatesX = ((KCANode) scenario.getRoot().getFirstNode("map")
				.getFirstNode("agent").getFirstNode("location").getFirstNode("x")).getParameters()
				.getValues();

		List<String> coordinatesY = ((KCANode) scenario.getRoot().getFirstNode("map")
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

		Map<Specification, KCANode> specifications;
		Iterator<XMLNode> events = scenario.getRoot().getFirstNode("timeline")
				.getNodeIterator("event");

		while (events.hasNext()) {

			XMLNode event = events.next();
			// return path-> node for each node which have value or parameters
			// example path : domain/a or location/x
			// usefull for foreach
			specifications = getEventSpecificationsPath(event, "");

			// generate events with every values
			List<Map<Specification, String>> values = generateValues(specifications);

			// put all these events in commandset
			for (Map<Specification, String> value : values) {
				String dname = value.get(Specification.DOMAIN_A)
						+ value.get(Specification.DOMAIN_B) + value.get(Specification.DOMAIN_C);
				if (!datamap.containsKey(dname))
					datamap.put(dname, new DataContent(datamap.keySet().size()));
				commandset.add(new Command(Command.Action.INJECT, new Location(new Double(Math
						.round(Double.parseDouble(value.get(Specification.LOCATION_X))))
				.doubleValue(), new Double(Math.round(Double.parseDouble(value
						.get(Specification.LOCATION_X)))).doubleValue()), new Fact(null, datamap
								.get(dname), 0)
				.setPressure(Float.parseFloat(value.get(Specification.PRESSURE)))
				.setPersistence(Float.parseFloat(value.get(Specification.PERSISTENCE)))
				.setSpecialty(
						new Specialty(
								Double.parseDouble(value.get(Specification.DOMAIN_A)),
								Double.parseDouble(value.get(Specification.DOMAIN_B)),
								Double.parseDouble(value.get(Specification.DOMAIN_C)))),
								new Double(Double.parseDouble(value.get(Specification.TIME))).intValue()));
			}

		}

		data = datamap.values().toArray(new DataContent[datamap.values().size()]);
		commands = commandset.toArray(new Command[commandset.size()]);

		// FIXME this is useless, generated data has consecutive ids
		Arrays.sort(data, new DataContent.DataContentComparator());
		System.out.println(data.length);
	}

	// read random in test/seed
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
		KCAScenario.seed = seed;
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

	/* need to FIX */
	public void save(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();
			PrintWriter pw = new PrintWriter(file);
			pw.println("<scenario seed=\"" + seed + "\">");
			while ((line = br.readLine()) != null) {
				pw.println(line);
			}
			pw.close();
			br.close();
			// DocumentBuilderFactory factory =
			// DocumentBuilderFactory.newInstance();
			// DocumentBuilder builder = factory.newDocumentBuilder();
			// Document doc = builder.parse(new File(fileName));
			//
			// Transformer transformer =
			// TransformerFactory.newInstance().newTransformer();
			// transformer.setOutputProperty(OutputKeys.METHOD, "text");
			// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			//
			// //initialize StreamResult with File object to save to file
			// StreamResult result = new StreamResult(file);
			// DOMSource source = new DOMSource(doc);
			// transformer.transform(source, result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Association between every nodes and theirs paths, relatively to a father
	 * node.
	 * 
	 * @param event
	 *            father Node, paths are generated relatively to this node{@link XMLNode}
	 * @param path 
	 * @return
	 */
	private Map<Specification, KCANode> getEventSpecificationsPath(XMLNode event, String path) {

		Map<Specification, KCANode> specifications = new TreeMap<Specification, KCANode>();
		// iterate on child
		System.out.println(event);
		for (XMLNode child : event.getNodes()) {

			// cast for the visibility of getParameters()
			KCANode kcaChild = (KCANode) child;

			// path name
			String newPath;
			// example : path = "domain" ==> "domain/a"
			// at start, path is empty or null
			if (path != null && !path.equals(""))
				newPath = path + SPLIT_CHARACTER + kcaChild.name;
			else
				newPath = kcaChild.name;

			// child contains a value or a set of parameters
			if (!kcaChild.getParameters().isAllValueNull()) {
				specifications.put(Specification.parseType(newPath), kcaChild);

			}
			// recursive : check if kcaChild contains or not a value or set of
			// parameters
			else if (kcaChild.getNodes().size() > 0)
				specifications.putAll(getEventSpecificationsPath(kcaChild, newPath));
		}
		return specifications;
	}

	/**
	 * @param specifications
	 *            map associating a path with a node. This path is of type
	 *            Specification, which is an enum.
	 * @return a list of events. Each event is a map associating a Specification
	 *         with its value generated.
	 */
	private List<Map<Specification, String>> generateValues(
			Map<Specification, KCANode> specifications) {
		// TODO Auto-generated method stub

		// initialization of the map
		List<Map<Specification, String>> events = new ArrayList<Map<Specification, String>>();
		events.add(new TreeMap<Specification, String>());

		for (Specification path : specifications.keySet()) {
			events = generateValues(path, specifications, events);
		}

		for(Map<Specification, String> event : events)
			System.out.println(event);
			
		return events;
	}

	/**
	 * Generate values for one specification. If this Specification depends of
	 * another Specification, this last will be generated recursively.
	 * 
	 * @param path
	 * @param specifications
	 * @param events
	 */
	private List<Map<Specification, String>> generateValues(Specification path,
			Map<Specification, KCANode> specifications, List<Map<Specification, String>> events) {
		// TODO Auto-generated method stub

		List<Map<Specification, String>> eventsUpdate = events;
		// no need to generate : already done with recursion
		if (!events.get(0).containsKey(path)) {

			// number of values to generate, depends of the foreach
			int countNumberValues = 1;
			List<String> values = new ArrayList<String>();

			// get the number of values to generate
			for (String foreach : specifications.get(path).getParameters().getForeach()) {
				Specification foreachSpe = Specification.parseType(foreach);
				// if set of value if not generated, we should generate them
				if (!events.get(0).containsKey(foreachSpe)) {
					// recursive
					eventsUpdate = generateValues(foreachSpe, specifications, eventsUpdate);
				}
				Integer select = specifications.get(foreachSpe).getParameters().getSelect();
				if (select != null)
					countNumberValues *= select.intValue();
			}
			// it generates values
			for (int i = 0; i < countNumberValues; i++) {
				values.addAll(specifications.get(path).getParameters().getValues());
			}

			// events with these new values generated
			List<Map<Specification, String>> newEvents = new ArrayList<Map<Specification, String>>();

			// number of times each tuple is duplicated
			int numberSameTuples = eventsUpdate.size() / countNumberValues;

			// number of different values for each tuple
			int numberValuesProduced = values.size() / countNumberValues;

			for (int i = 0; i < countNumberValues; i++) {
				for (int j = 0; j < numberSameTuples; j++) {
					for (int k = 0; k < numberValuesProduced; k++) {
						Map<Specification, String> eventWithNewValue = new TreeMap<Specification, String>(
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
	 *         {@link KCANode}
	 */
	private XMLTree getScenarioTree(XMLTree tree) {
		XMLNode newRoot = getKCANodes(tree.getRoot());
		return new XMLTree(newRoot);
	}

	/**
	 * Recursive function to transform an {@link XMLNode} and its children in
	 * {@link KCANode}.
	 * 
	 * @param node
	 *            {@link XMLNode} to transform in {@link KCANode}
	 * @return a {@link KCANode}, with all child nodes transformed in
	 *         {@link KCANode} too.
	 */
	private KCANode getKCANodes(XMLNode node) {
		KCANode newNode = new KCANode(node);

		for (int i = 0; i < newNode.getNodes().size(); i++)
			newNode.nodes.set(i, getKCANodes(newNode.nodes.get(i))); // recursive

		return newNode;
	}

	/**
	 * {@link KCANode} is a {@link XMLNode} with a set of parameters. These
	 * parameters could generate random values.
	 * 
	 * @author Alexandre Hocquard
	 * 
	 */
	public static class KCANode extends XMLNode {

		KCAParameters	parameters;

		public KCANode(XMLNode node) {
			// TODO Auto-generated constructor stub
			super(node);
			parameters = new KCAParameters(this);
		}

		public KCAParameters getParameters() {
			return parameters;
		}

		private class KCAParameters {
			private boolean		allValueNull	= true;
			private String		value			= null;
			private String		list			= null;
			private Double		mean			= null;
			private Double		stdev			= null;
			private Double		min				= null;
			private Double		max				= null;
			private Double		first			= null;
			private Double		last			= null;
			private Integer		count			= null;
			private Integer		select			= null;
			private Double		step			= null;
			ArrayList<String>	foreach			= new ArrayList<String>();

			public KCAParameters(XMLNode node) {
				// either value or parameters to generate random values
				if (node.getNodeIterator("value").hasNext()) {
					value = node.getNodeIterator("value").next().getValue().toString();
					allValueNull = false;
				} else {
					if (node.getNodeIterator("list").hasNext()) {
						list = (String) node.getNodeIterator("list").next().getValue();
						allValueNull = false;
					}
					if (node.getNodeIterator("mean").hasNext()) {
						mean = (Double) node.getNodeIterator("mean").next().getValue();
						allValueNull = false;
					}
					if (node.getNodeIterator("stdev").hasNext()) {
						stdev = (Double) node.getNodeIterator("stdev").next().getValue();
						allValueNull = false;
					}
					if (node.getNodeIterator("min").hasNext()) {
						min = (Double) node.getNodeIterator("min").next().getValue();
						allValueNull = false;
					}
					if (node.getNodeIterator("max").hasNext()) {
						max = (Double) node.getNodeIterator("max").next().getValue();
						allValueNull = false;
					}
					if (node.getNodeIterator("first").hasNext()) {
						first = (Double) node.getNodeIterator("first").next().getValue();
						allValueNull = false;
					}
					if (node.getNodeIterator("last").hasNext()) {
						last = (Double) node.getNodeIterator("last").next().getValue();
						allValueNull = false;
					}
					if (node.getNodeIterator("count").hasNext()) {
						count = new Integer(((Double) node.getNodeIterator("count").next()
								.getValue()).intValue());
					}
					if (node.getNodeIterator("select").hasNext()) {
						select = new Integer(((Double) node.getNodeIterator("select").next()
								.getValue()).intValue());
					}
					if (node.getNodeIterator("step").hasNext()) {
						step = (Double) node.getNodeIterator("step").next().getValue();
						allValueNull = false;
					}

					Iterator<XMLNode> foreachIterator = node.getNodeIterator("foreach");
					while (foreachIterator.hasNext()) {
						foreach.add(foreachIterator.next().getValue().toString());
						allValueNull = false;
					}
				}
			}

			List<String> getValues() {
				List<String> res = new ArrayList<String>();
				if (value != null) {
					res.add(value);
				} else if (list != null) {
					for (String item : list.split(",")) {
						res.add(item);
					}
				} else if (count == null && step == null) {
					assert min != null && max != null;
					select = new Integer(select == null ? 1 : select.intValue());
					for (int i = 0; i < select.intValue(); i++) {
						if (mean != null && stdev != null) {
							while (true) {
								double val = mean.doubleValue() + KCAScenario.rand().nextGaussian()
										* stdev.doubleValue();
								if (max.doubleValue() <= val && val <= min.doubleValue()) {
									res.add(new Double(val).toString());
									break;
								}
							}
						} else {
							assert mean == null && stdev == null;
							res.add((new Double(min.doubleValue()
									+ (max.doubleValue() - min.doubleValue())
									* KCAScenario.rand().nextDouble())).toString());
						}
					}
				} else {
					if (step != null && count != null) {
						assert first == null || last == null;
						if (first == null) {
							assert last != null;
							first = new Double(last.doubleValue() - (count.intValue() - 1)
									* step.doubleValue());
						}
						if (last == null) {
							assert first != null;
							last = new Double(first.doubleValue() + (count.intValue() - 1)
									* step.doubleValue());
						}
					}
					if (min == null) {
						assert first != null;
						min = first;
					}
					if (max == null) {
						assert last != null;
						max = last;
					}
					double start = first != null ? first.doubleValue() : min.doubleValue();
					double stop = last != null ? last.doubleValue() : max.doubleValue();
					double val = 0.0;
					if (step == null) {
						assert count.intValue() >= (first != null ? 1 : 0) + (last != null ? 1 : 0);
						step = new Double(
								(stop - start)
								/ (count.intValue() - (first != null ? 0.5 : 0.0) - (last != null ? 0.5
										: 0.0)));
						val = first != null ? start : start + step.intValue() / 2;
					}
					if (count == null) {
						assert first == null || last == null;
						count = new Integer(1 + (int) Math.floor((stop - start)
								/ step.doubleValue()));
						val = first != null ? start : start
								+ step.doubleValue()
								* (last != null ? stop - (count.intValue() - 1)
										* step.doubleValue()
										: (stop + start - (count.intValue() - 1)
												* step.doubleValue()) / 2);
					}
					for (int i = 0; i < count.intValue(); i++) {
						if (stdev != null) {
							while (true) {
								double v = val + KCAScenario.rand().nextDouble()
										* stdev.doubleValue();
								boolean ok1 = i == 0 && v >= min.doubleValue()
										|| v >= val - stdev.doubleValue();
										boolean ok2 = i == count.intValue() - 1 && v <= max.doubleValue()
												|| v <= val + stdev.doubleValue();
										if (ok1 && ok2) {
											res.add(new Double(v).toString());
											break;
										}
							}
						} else {
							res.add(new Double(val).toString());
						}
						val += step.doubleValue();
					}
					if (select != null) {
						for (int i = 0; i < count.intValue() - select.intValue(); i++) {
							res.remove(KCAScenario.rand().nextInt(res.size()));
						}
					}
				}
				assert res.size() > 0;
				return res;
			}

			public boolean isAllValueNull() {
				return allValueNull;
			}

			public ArrayList<String> getForeach() {
				return foreach;
			}

			public Integer getSelect() {
				return select;
			}

			@Override
			public String toString() {
				return "Parameters [list=" + list + ", mean=" + mean + ", stdev=" + stdev
						+ ", min=" + min + ", max=" + max + ", first=" + first + ", last=" + last
						+ ", count=" + count + ", select=" + select + ", step=" + step
						+ ", foreach=" + foreach + "]";
			}

		}

	}

	private enum Specification {
		DOMAIN_A, DOMAIN_B, DOMAIN_C, TIME, PRESSURE, PERSISTENCE, LOCATION_X, LOCATION_Y;

		public static Specification parseType(String str) {
			if (str.equals("domain/a")) {
				return Specification.DOMAIN_A;
			} else if (str.equals("domain/b")) {
				return Specification.DOMAIN_B;
			} else if (str.equals("domain/c")) {
				return Specification.DOMAIN_C;
			} else if (str.equals("time")) {
				return Specification.TIME;
			} else if (str.equals("pressure")) {
				return Specification.PRESSURE;
			} else if (str.equals("persistence")) {
				return Specification.PERSISTENCE;
			} else if (str.equals("location/x")) {
				return Specification.LOCATION_X;
			} else if (str.equals("location/y")) {
				return Specification.LOCATION_Y;
			} else {
				assert false : str; // there are no other recognized types
			return null;
			}
		}
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

	public int getNsteps() {
		return nsteps;
	}

	public static Long getSeed() {
		return new Long(seed);
	} // copy

	public static Random rand() {
		return rand;
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
