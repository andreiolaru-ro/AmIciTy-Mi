package agent;

public class AgentID implements Comparable<AgentID>
{
	public static int iteratorId = 0;

	public String name;
	public Integer id ;
	
	public AgentID( String name)
	{
		this.name = name;
		this.id = new Integer(AgentID.iteratorId) ;
		
		AgentID.iteratorId++;
	}

	public AgentID(Integer id)
	{
		this.name = id.toString() ;
		this.id = id ;
		
		AgentID.iteratorId++;
	}
	
	public AgentID(double x, double y) 
	{
		this.name = "(" + x + "," + y + ")";
		this.id = new Integer(AgentID.iteratorId) ;
		
		AgentID.iteratorId++;
	}
	
	
	@Override
	public int compareTo(AgentID agent)
	{
		return id.compareTo(agent.id);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof AgentID && ((AgentID) obj).name.equals(name) && ((AgentID) obj).id.equals(id);
	}
	
	@Override
	public int hashCode() 
	{
		return id.hashCode();
	}
	
	@Override
	public String toString()
	{
		return "#" + id + " #" + name  ;
	}
	

	
}
