package oldScenario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import KCAAgent.DataContent;
import KCAAgent.Fact;
import KCAAgent.KCAAgent;
import KCAAgent.Logix;
import KCAAgent.Specialty;
import agent.AgentID;
import agent.Location;
import base.Command;


public class Scenario extends DefaultHandler {
	private static final String SEED_FILE = "test/seed";
	private static long seed;
	private static Random rand;
	
	private String fileName;
	
	private int nsteps;

	private double x;
	private double y;
	private double width;
	private double height;

	private Map<AgentID, KCAAgent> agents = new HashMap<AgentID, KCAAgent>();

	private Map<String, DataContent> datamap = new HashMap<String, DataContent>();
	private DataContent[] data;
	
	private SortedSet<Command> commandset = new TreeSet<Command>(new Comparator<Command>() {
		@Override
		public int compare(Command c1, Command c2) {
			if (c1.getTime() != c2.getTime()) {
				return c1.getTime() - c2.getTime();				
			}
			return c1.hashCode() - c2.hashCode();
		}
	});
	private Command[] commands;
	
	static Schema schema = new Schema();
	static {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(new File("test/schema.xml"), schema);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Doc doc = new Doc(schema);

	@SuppressWarnings("hiding")
	@Override
	public void startElement(String ns, String localName, String qName, Attributes att) throws SAXException {
		if (qName.equals("map")) {
			x = Double.parseDouble(att.getValue("x"));
			y = Double.parseDouble(att.getValue("y"));
			width = Double.parseDouble(att.getValue("width"));
			height = Double.parseDouble(att.getValue("height"));
		} else if (qName.equals("agent")) {
			double x = Double.parseDouble(att.getValue("location.x"));
			double y = Double.parseDouble(att.getValue("location.y"));
			AgentID id = new AgentID(x, y);
			agents.put(id, new KCAAgent(null, id, new Location(x, y), Logix.agentCapacity, nsteps));
		} else if (qName.equals("timeline")) {
			nsteps = Integer.parseInt(att.getValue("duration"));
		} else if (qName.equals("event")) {
			String dname = att.getValue("domain.a") + att.getValue("domain.b") + att.getValue("domain.c");
			if (!datamap.containsKey(dname)) {
				datamap.put(dname, new DataContent(datamap.keySet().size()));
			}
			try
			{
				commandset.add(new Command(
						Command.Action.INJECT, 
						new Location(
								Integer.parseInt(att.getValue("location.x")), 
								Integer.parseInt(att.getValue("location.y"))
						), 
						new Fact(null, datamap.get(dname), 0)
							.setPressure(Float.parseFloat(att.getValue("pressure")))
							.setPersistence(Float.parseFloat(att.getValue("persistence")))
							.setSpecialty(new Specialty(
									Double.parseDouble(att.getValue("domain.a")),
									Double.parseDouble(att.getValue("domain.b")),
									Double.parseDouble(att.getValue("domain.c"))
							)), 
						Integer.parseInt(att.getValue("time"))
				));
			} catch (NumberFormatException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void endDocument() {
		data = datamap.values().toArray(new DataContent[datamap.values().size()]);
		commands = commandset.toArray(new Command[commandset.size()]);
		
		//FIXME this is useless, generated data has consecutive ids
		Arrays.sort(data, new DataContent.DataContentComparator());
	}

	@SuppressWarnings({ "hiding"})
	public Scenario(String fileName) {
		this.fileName = fileName;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(new File(fileName), doc);
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(doc.toString()));
			parser.parse(is, this);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Iterator<?> it;
		Map.Entry<?,?> p;
		KCAAgent ag;
		it = agents.entrySet().iterator();
		while(it.hasNext())
		{
			p = (Map.Entry<?,?>)it.next();
			ag = (KCAAgent)p.getValue();
			ag.setHistory(nsteps);
		}
	}
	
	public int getnSteps() {
		return nsteps;
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
		assert data != null;
		return data;
	}
	
	public Command[] getCommands() {
		assert commands != null;
		return commands; 
	}
	
	public Map<AgentID, KCAAgent> getAgents() {
		assert agents != null;
		return agents;
	}
	
	@SuppressWarnings("static-method")
	public Command[] getAbsCommands() {
		return new Command[] {
//			 new Command(Command.Action.SNAPSHOT, null, 2, 0, 100),
		};
	}

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
	
	@SuppressWarnings("hiding")
	public static void initRandom(long seed) {
		Scenario.seed = seed;
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
	
	public static Random rand() {
		return rand;
	}

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
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			Document doc = builder.parse(new File(fileName));
//			
//			Transformer transformer = TransformerFactory.newInstance().newTransformer();
//			transformer.setOutputProperty(OutputKeys.METHOD, "text");
//			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//
//			//initialize StreamResult with File object to save to file
//			StreamResult result = new StreamResult(file);
//			DOMSource source = new DOMSource(doc);
//			transformer.transform(source, result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Long getSeed() {
		return new Long(seed);
	}
}
