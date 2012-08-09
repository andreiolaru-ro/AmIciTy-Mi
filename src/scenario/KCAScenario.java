package scenario;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import KCAAgent.CommandKCA;
import KCAAgent.DataContent;
import KCAAgent.Fact;
import KCAAgent.KCAAgent;
import KCAAgent.Logix;
import KCAAgent.Specialty;
import XMLParsing.XMLTree.XMLNode;
import agent.AgentID;
import agent.Location;
import base.Command;
import expr.Expr;
import expr.Parser;
import expr.SyntaxException;
import expr.Variable;

public class KCAScenario extends AbstractScenario<KCAAgent, CommandKCA> {

	private final static String			SCHEMA_FILE_NAME	= "schemas/agent/kcaSchema.xsd";

	private double						x;
	private double						y;
	private double						width;
	private double						height;


	private Map<String, DataContent>	datamap				= new HashMap<String, DataContent>();
	private DataContent[]				data;

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

		super(SCHEMA_FILE_NAME, scenarioFileName) ;
		
		Expr expr;
		Variable variable_x = Variable.make("x");
		Variable variable_t = Variable.make("t");

		try {
			expr = Parser.parse("x+t=4");
		} catch (SyntaxException e) {
			System.err.println(e.explain());
			return;
		}

//		int low = 0;
//		int high = 10;
//		int step = 1;
//
//		for (double xval = low; xval <= high; xval += step) {
//			variable_x.setValue(xval);
//			for (double xval2 = low; xval2 <= high; xval2 += step) {
				variable_t.setValue(2);
				variable_x.setValue(2);

				System.out.println(expr.value());
//			}
//		}


		/********************************* map : coordinates + size ********************************/

		x = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("characteristic")
				.getFirstNode("coordinates").getFirstNode("x").getValue()).doubleValue();
		x = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("characteristic")
				.getFirstNode("coordinates").getFirstNode("y").getValue()).doubleValue();
		width = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("characteristic")
				.getFirstNode("size").getFirstNode("width").getValue()).doubleValue();
		height = ((Double) scenario.getRoot().getFirstNode("map").getFirstNode("characteristic")
				.getFirstNode("size").getFirstNode("height").getValue()).doubleValue();



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

		/********************************** creation of events ***********************************/

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
					commandset.add(new CommandKCA(CommandKCA.Action.INJECT, new Location(new Double(Math
							.round(Double.parseDouble(value.get("location/x")))).doubleValue(),
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


	public DataContent[] getData() {
		assert data != null : data;
		return data;
	}

}
