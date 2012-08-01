package agent;

/**
 * 
 * enum of the different measures that an agent can have
 *
 */
public enum MeasureName{ 
	SPECIALTY("specialty"), 
	
	LOCATION("location"),
	
	CAPACITY("capacity"),
	
	AGENT_PRESSURE("agent pressure"), 
	
	LOWPRESSURE("low pressure"), 
	
	HIGHPRESSURE("high pressure"), 
	
	PERSISTENCE("persistence"), 
	
	NEIGHBOUR_DATA_FACT_PERSISTENCE("neighbour data fact persistence"), 
	
	NEW_FACT_GOAL_IMPORTANCE("new fact goal importance"), 
	
	SPEC_UPDATE_RATIO("spec update ratio"),
	
	SECONDARY_PRESSURE_FADE("secondary pressure fade"),
	
	SECONDARY_PERSISTENCE_FADE("secondary persistence fade"),
	
	AGENT_BALANCE("agent balance"),
	
	PROBABILITY("probability"),
	
	AGENT_USELESS_FACT("agent useless fact");
	
	private String nameEnum;
	
	private MeasureName (String name)
	{
		this.nameEnum=name;
	}
	
	@Override
	public String toString()
	{
		return nameEnum;
	}
}