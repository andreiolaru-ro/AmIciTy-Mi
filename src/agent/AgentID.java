package agent;

public class AgentID implements Comparable<AgentID>
{
	private String name;
	
	public AgentID(@SuppressWarnings("hiding") String name)
	{
		this.name = name;
	}
	
	public AgentID(double x, double y) 
	{
		this.name = "(" + x + "," + y + ")";
	}
	
	
	@Override
	public int compareTo(AgentID id)
	{
		return name.compareTo(id.name);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof AgentID && ((AgentID) obj).name.equals(name);
	}
	
	@Override
	public int hashCode() 
	{
		return name.hashCode();
	}
	
	@Override
	public String toString()
	{
		return "#" + name;
	}
}
