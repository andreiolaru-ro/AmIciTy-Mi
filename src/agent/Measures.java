package agent;

import java.util.HashMap;
import java.util.Map;
/**
 * class which contains contains all the measure for a specific agent
 */
public class Measures
{
	private Map<MeasureName, Measure<?>> measures;
	
	/**
	 * intern class which contains all agents measures
	 * using the pattern singleton
	 */
	private final static class AllAgentsMeasures
	{
		private static AllAgentsMeasures allAgentsMeasures =null;
		
		//contains all the measures for all the agents
		private static Map<AgentID, Map<MeasureName, Measure<?>>> measuresByAgent;
		
		private AllAgentsMeasures()
		{
			measuresByAgent= new HashMap<AgentID, Map<MeasureName, Measure<?>>>();
		}
		
		/**
		 * 
		 * @param id the id of the specific agent
		 * @return the measures for this agent
		 */
		protected static Map<MeasureName, Measure<?>> getMeasuresOfTheAgent(AgentID id)
		{
			if(AllAgentsMeasures.allAgentsMeasures==null)
				allAgentsMeasures = new AllAgentsMeasures();
			if(!AllAgentsMeasures.measuresByAgent.containsKey(id))
				AllAgentsMeasures.measuresByAgent.put(id, new HashMap<MeasureName, Measure<?>>());

			return AllAgentsMeasures.measuresByAgent.get(id);
			
			
		}
	}
	
	public Measures(AgentID id)
	{
		this.measures= AllAgentsMeasures.getMeasuresOfTheAgent(id);
	}
	
	
	/**
	 * fonction to create automatically the different class measure and add them to the map at the same time
	 * @param value that we went to register for our agent
	 * @param name the name of the measure
	 * @return the measure
	 */
	public Measure<?> createMeasure(Measure<?> value)
	{
		MeasureName name = value.getName();
		this.measures.put(name, value);
		return value;
	}
	
	

	public Map<MeasureName, Measure<?>> getMeasures()
	{
		return measures;
	}
	

}
