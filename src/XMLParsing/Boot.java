package XMLParsing;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import XMLParsing.XMLParser;
import XMLParsing.XMLTree;
import XMLParsing.XMLTree.XMLNode;

import util.logging.Log;
import util.platformUtils.PlatformUtils;
import util.windowLayout.WindowLayout;
import core.claim.ClaimAgent;
import core.claim.parser.ClaimAgentDefinition;
import core.interfaces.JadeInterface;
import core.interfaces.JadeInterface.JadeConfig;
import core.interfaces.Logger;
import core.interfaces.ParametrizedAgent;
import core.interfaces.ParametrizedAgent.AgentParameterName;
import core.visualization.VisualizationAgent;

/**
 * 
 * THIS FILE IS HERE ONLY AS USAGE EXAMPLE FOR USING XMLParser AND XMLTree
 * 
 * @author Andrei Olaru
 * @author Nguyen Thi Thuy Nga
 */
public class Boot
{
	enum Loader {
		
		ADF2("adf2"),
		
		JAVA("java"),
		
		;
		
		private String	name	= null;
		
		private Loader(String loaderName)
		{
			name = loaderName;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
		
	}
	
	private static String	unitName	= "boot";
	protected static Logger	log			= Log.getLogger(unitName);
	
	private static String	schemaName	= "config/scenarioSchema2.xsd";
	
	public static void main(String args[])
	{
		log.trace("Booting World.");
		
		BootSettings settings = new BootSettings();
		
		// default arguments
		String scenarioFileName = settings.scenarioFileName;
		int windowLayoutWidth = settings.windowLayoutWidth;
		int windowLayoutHeight = settings.windowLayoutHeight;
		
		String jadeIP = null;
		String jadePort = null;
		String localIP = null;
		String localPort = null;
		
		// expected arguments: scenario xml, remote IP, remote port, local IP, local port, screen width, screen height
		switch(args.length)
		{
		default:
			log.warn("too many arguments");
			//$FALL-THROUGH$
		case 7:
			windowLayoutWidth = Integer.parseInt(args[5]);
			windowLayoutHeight = Integer.parseInt(args[6]);
			//$FALL-THROUGH$
		case 6:
			log.warn("incorrect number of arguments");
			//$FALL-THROUGH$
		case 5:
			try
			{
				if(Integer.parseInt(args[4]) >= 0)
					localPort = args[4];
			} catch(NumberFormatException e1)
			{
				// TODO
			}
			//$FALL-THROUGH$
		case 4:
			if(!"null".equals(args[3]))
				localIP = args[3];
			//$FALL-THROUGH$
		case 3:
			try
			{
				if(Integer.parseInt(args[2]) >= 0)
					jadePort = args[2];
			} catch(NumberFormatException e1)
			{
				// TODO
			}
			//$FALL-THROUGH$
		case 2:
			if(!"null".equals(args[1]))
				jadeIP = args[1];
			//$FALL-THROUGH$
		case 1:
			if(!args[0].equals("default") && new File(args[0]).exists())
				scenarioFileName = args[0];
			else
				log.error("file [" + args[0] + "] does not exist");
			//$FALL-THROUGH$
		case 0:
		}
		
		// create window layout
		WindowLayout.staticLayout = new WindowLayout(windowLayoutWidth, windowLayoutHeight, settings.layout, null);
		
		// parse scenario; get jade initialization data
		
		Map<String, AgentCreationData> allAgents = new HashMap<String, AgentCreationData>();
		
		log.info("loading scenario [" + scenarioFileName + "]");
		XMLTree scenarioTree = XMLParser.validateParse(schemaName, scenarioFileName);
		log.info("scenario:");
		log.info(scenarioTree.toString());
		
		XMLNode jadeConfigNode = (scenarioTree.getRoot().getNodeIterator("jadeConfig").hasNext() ? scenarioTree.getRoot().getNodeIterator("jadeConfig").next() : null);
		String mainContainerName = null;
		boolean createMainContainer = false;
		
		String jadePlatform = null;
		if(jadeConfigNode != null)
		{
			if(jadeConfigNode.getAttributeValue("IPaddress") != null)
				jadeIP = jadeConfigNode.getAttributeValue("IPaddress");
			if(jadeConfigNode.getAttributeValue("port") != null)
				jadePort = jadeConfigNode.getAttributeValue("port");
			if(jadeConfigNode.getAttributeValue("platformID") != null)
				jadePlatform = jadeConfigNode.getAttributeValue("port");
			if(jadeConfigNode.getAttributeValue("mainContainerName") != null)
			{
				mainContainerName = jadeConfigNode.getAttributeValue("mainContainerName");
				createMainContainer = true;
			}
			if((jadeConfigNode.getAttributeValue("isMain") != null) && jadeConfigNode.getAttributeValue("isMain").equals(new Boolean(true).toString()))
				createMainContainer = true;
		}
		
		// boot JADE platform
		log.info("booting JADE");
		
		// identify appropriate JadeInterface instance
		JadeInterface jade = null;
		Class<?> jadeinterfaceClass = PlatformUtils.jadeInterfaceClass();
		try
		{
			ClassLoader cl = new ClassLoader(Boot.class.getClassLoader()) {
				// nothing to extend
			};
			Constructor<?> cons = cl.loadClass(jadeinterfaceClass.getCanonicalName()).getConstructor();
			jade = (JadeInterface)cons.newInstance();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		if(jade == null)
		{
			log.error("unable to create jade interface");
			return;
		}
		
		JadeConfig jadeConfig = jade.fillConfig(new JadeConfig());
		jadeConfig.setLocalHost(localIP).setLocalPort(localPort).setMainHost(jadeIP).setMainPort(jadePort);
		if(createMainContainer)
			jadeConfig.setPlatformID(jadePlatform).setMainContainerName(mainContainerName);
		jade.setConfig(jadeConfig);
		
		if(createMainContainer)
		{
			log.info("booting platform / main container");
			jade.startPlatform();
		}
		
		// create containers
		XMLNode initial = scenarioTree.getRoot().getNodeIterator("initial").next();
		for(Iterator<XMLNode> itC = initial.getNodeIterator("container"); itC.hasNext();)
		{
			XMLNode containerConfig = itC.next();
			String containerName = containerConfig.getAttributeValue("name");
			boolean doCreateContainer = (containerConfig.getAttributeValue("create") == null) || containerConfig.getAttributeValue("create").equals(new Boolean(true));
			
			if(!containerName.equals(mainContainerName) && doCreateContainer)
			{
				log.info("creating container [" + containerName + "]...");
				jade.startContainer(containerName);
				log.info("container created; adding agents...");
			}
			else
				log.info("adding agents in main container");
			
			// get general parameters
			Set<String> adfPaths = new HashSet<String>(), agentPackages = new HashSet<String>();
			Iterator<XMLNode> adfPathsIt = scenarioTree.getRoot().getNodeIterator("adfPath");
			while(adfPathsIt.hasNext())
				adfPaths.add((String)adfPathsIt.next().getValue());
			Iterator<XMLNode> packagePathsIt = scenarioTree.getRoot().getNodeIterator(AgentParameterName.AGENT_PACKAGE.toString());
			while(packagePathsIt.hasNext())
				agentPackages.add((String)packagePathsIt.next().getValue());
			
			// set up creation for all agents in the container
			for(Iterator<XMLNode> itA = containerConfig.getNodeIterator("agent"); itA.hasNext();)
			{
				XMLNode agentConfig = itA.next();
				// get interesting parameters
				if(!agentConfig.getNodeIterator("parameter").hasNext())
				{
					log.error("agent has no parameters");
					continue;
				}
				
				String agentName = getParameterValue(agentConfig, AgentParameterName.AGENT_NAME.toString());
				if(agentName == null)
				{
					log.error("agent has no name; will not be created.");
					continue;
				}
				
				String agentLoader = getParameterValue(agentConfig, "loader");
				Loader loader = null;
				for(Loader ld : Loader.values())
					if(ld.toString().equals(agentLoader))
						loader = ld;
				if(loader == null)
				{
					log.error("unknown agent loader [" + agentLoader + "]; agent [" + agentName + "] will not be created");
					continue;
				}
				
				// get parameters and put them into a neat HashMap
				ParametrizedAgent.AgentParameters parameters = new ParametrizedAgent.AgentParameters();
				for(Iterator<XMLNode> paramIt = agentConfig.getNodeIterator("parameter"); paramIt.hasNext();)
				{
					XMLNode param = paramIt.next();
					AgentParameterName parName = AgentParameterName.getName(param.getAttributeValue("name"));
					if(parName != null)
						parameters.add(parName, param.getAttributeValue("value"));
					else
					{
						log.trace("adding unregistered parameter [" + param.getAttributeValue("name") + "].");
						parameters.add(param.getAttributeValue("name"), param.getAttributeValue("value"));
					}
				}
				for(String pack : agentPackages)
					parameters.add(AgentParameterName.AGENT_PACKAGE, pack);
				
				switch(loader)
				{
				case JAVA:
					// TODO: to do, if needed; see oldBoot for the code
					break;
				case ADF2:
				{
					ClaimAgentDefinition cad = ClaimAgent.fillCAD(parameters.get(AgentParameterName.AGENT_CLASS.toString()), parameters.getValues(AgentParameterName.JAVA_CODE.toString()), adfPaths, agentPackages, log);
					parameters.addObject(AgentParameterName.AGENT_DEFINITION, cad);
					
					// register agent
					allAgents.put(agentName, new AgentCreationData(agentName, ClaimAgent.class.getCanonicalName(), parameters, containerName, !doCreateContainer));
					log.info("configured [" + agentLoader + "] agent [" + agentName + "] in container [" + containerName + "]");
					break;
				}
				}
				
			}
		}
		
		if(createMainContainer)
		{
			// FIXME visualizer / simulator name should be set by scenario
			String vizName = "visualizer";
			jade.addAgentToContainer(mainContainerName, vizName, VisualizationAgent.class.getCanonicalName(), null);
			
			XMLNode timeline = null;
			if(scenarioTree.getRoot().getNodeIterator(AgentParameterName.TIMELINE.toString()).hasNext())
				timeline = scenarioTree.getRoot().getNodeIterator(AgentParameterName.TIMELINE.toString()).next();
			
			ParametrizedAgent.AgentParameters parameters = new ParametrizedAgent.AgentParameters();
			parameters.addObject(AgentParameterName.JADE_INTERFACE, jade);
			parameters.addObject(AgentParameterName.AGENTS, allAgents.values());
			if(timeline != null)
				parameters.addObject(AgentParameterName.TIMELINE, timeline);
			parameters.add(AgentParameterName.VISUALIZTION_AGENT, vizName);
			jade.addAgentToContainer(mainContainerName, "simulator", SimulationAgent.class.getCanonicalName(), new Object[] { parameters });
			
		}
		
		Log.exitLogger(unitName);
	}
	
	static String getParameterValue(XMLNode agentConfig, String parameterName)
	{
		Iterator<XMLNode> paramsIt = agentConfig.getNodeIterator("parameter");
		while(paramsIt.hasNext())
		{
			XMLNode param = paramsIt.next();
			if(param.getAttributeValue("name").equals(parameterName))
				return param.getAttributeValue("value");
		}
		return null;
	}
}
