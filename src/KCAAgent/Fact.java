/**
 * 
 */
package KCAAgent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import agent.AbstractMeasure.FloatMeasure;
import agent.AgentID;
import agent.MeasureName;


public class Fact
{
	// FIXME this function should be eventually removed
	public static Collection<Fact> filterCollectionOnAbstractContent(Collection<Fact> c, DataContent d)
	{
		Collection<Fact> ret = new HashSet<Fact>();
		for(Fact f : c)
			if(f.getAbstractContentRecursive().equals(d))
				ret.add(f);
		return ret;
	}
	
	private AgentID		agent;
	
	// only one of these is not null
	// if all are null, this is to represent the goal of clearing some capacity
	private Fact		fact			= null; // i.e. the agent knows this fact
	private Goal		goal			= null; // i.e. the agent wants to get this goal fulfilled
	private DataContent	abstractContent	= null; // i.e. abstract content
												
	// private float interest = 0.0f; // [0, 1] interest manifested by the agent towards this fact
	
	private Specialty		specialty			= null; //it's a measure too
	
	private FloatMeasure	pressure			= new FloatMeasure(0.0f, MeasureName.AGENT_PRESSURE);	// [-1, 1] positive / negative for push / pull
	// actually, negative pressure facts should be goals and positive should be data (informs)
	
	private FloatMeasure	persistence			= new FloatMeasure(0.0f, MeasureName.PERSISTENCE); // [0, 1]; 1 is for forever. 0 is that nobody else should care about it, i.e. do not continue to inform on it
	
	public int				firstStep			= 0;

	private double			agentFactBalance	= 0.0;
	
	// at the time, persistence refers only to space
	
	@SuppressWarnings("hiding")
	Fact(AgentID agent)
	{
		this.agent = agent;
		this.firstStep = 0;
	}
	
	//public Fact(AgentID agent, DataContent data, int fstep)
	@SuppressWarnings("hiding")
	public Fact(AgentID agent, DataContent data, int step)
	{
		this.agent = agent;
		this.abstractContent = data;
		//this.firstStep = fstep;
		this.firstStep = step;
	}
	
	//public Fact(AgentID agent, Goal goal, int fstep)
	@SuppressWarnings("hiding")
	public Fact(AgentID agent, Goal goal, int step)
	{
		this.agent = agent;
		this.goal = goal;
		//this.firstStep = fstep;
		this.firstStep = step;
	}
	
	//public Fact(AgentID agent, Fact fact, int fstep)
	@SuppressWarnings("hiding")
	public Fact(AgentID agent, Fact fact, int step)
	{
		this.agent = agent;
		this.fact = fact;
		//this.firstStep = fstep;
		this.firstStep = step;
	}
	
	public void resetFact(Fact f)
	{
		// TODO some checks would be necessary. like if replacing fact is the same with replaced
		if(this.fact != null)
			this.fact = f;
	}
	
	public DataContent getAbstractContent()
	{
		return abstractContent;
	}
	
	public Fact getFact()
	{
		return fact;
	}
	
	public Goal getGoal()
	{
		return goal;
	}
	
	Fact recurse()
	{
		if((abstractContent != null) || (goal != null))
			return this;
		if(fact != null)
			return fact.recurse();
		return null;
	}
	
	boolean isNullFact()
	{
		return ((abstractContent == null) && (goal == null) && (fact == null));
	}
	
	// returns the data contained in a fact
	// ATTENTION what this data means depends on the exact content
	// of the fact and on the depth level where the data is found
	public DataContent getAbstractContentRecursive()
	{
		if(abstractContent != null)
		{
			return abstractContent;
		}
		// else if(goal != null)
		// {
		// if(goal.getData() != null)
		// return goal.getData();
		// else
		// return goal.getFact().getDataRecursive();
		// }
		else if(fact != null)
		{
			return fact.getAbstractContentRecursive();
		}
		return null;
	}
	
	Goal getGoalRecursive()
	{
		if(goal != null)
		{
			return goal;
		}
		else if(fact != null)
		{
			return fact.getGoalRecursive();
		}
		return null;
	}
	
	int getDepth()
	{
		if(abstractContent != null)
		{
			return 0;
		}
		else if(goal != null)
		{
			return 0;
		}
		else if(fact != null)
		{
			return 1 + fact.getDepth();
		}
		return 0;
	}
	
	public AgentID getAgent()
	{
		return agent;
	}
	
	public float getPressure()
	{
		return pressure.getValue().floatValue();
	}
	
	// public float getInterest()
	// {
	// return interest;
	// }
	
	public Specialty getSpecialty()
	{
		return specialty;
	}
	
	@SuppressWarnings("hiding")
	public Fact setPressure(float pressure) throws Exception
	{
		this.pressure.setValue(new Float(pressure));
		return this;
	}
	
	@SuppressWarnings("hiding")
	public Fact setPressure(float pressure, float fade) throws Exception
	{
		return setPressure(pressure * fade);
	}
	
	public void fadePressure(float fadeRate) throws Exception
	{
		if (pressure.getValue().floatValue() < 1)
			pressure.setValue(new Float(pressure.getValue().floatValue() * (1.0f - fadeRate)));
		else
			pressure.setValue(pressure.getValue());
	}
	
	public void fadePersistence(float fadeRate) throws Exception
	{
		if (persistence.getValue().floatValue() < 1)
			persistence.setValue(new Float(persistence.getValue().floatValue() * (1.0f - fadeRate)));
		else
			persistence.setValue(persistence.getValue());
	}
	
	@SuppressWarnings("hiding")
	public Fact setSpecialty(Specialty specialty)
	{
		this.specialty = specialty;
		return this;
	}
	
	public float getPersistence()
	{
		return persistence.getValue().floatValue();
	}
	
	@SuppressWarnings("hiding")
	public Fact setPersistence(float persistence) throws Exception
	{
		this.persistence.setValue(new Float(persistence));
		return this;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		if(obj.getClass() != this.getClass())
			return false;
		Fact f = (Fact)obj;
		if(!agent.equals(f.agent))
			return false;
		if(abstractContent != null)
			return abstractContent.equals(f.abstractContent);
		if(goal != null)
			return goal.equals(f.goal);
		if(fact != null)
			return fact.equals(f.fact);
		// return false;
		return f.isNullFact();
	}
	
	@Override
	public int hashCode()
	{
		if(abstractContent != null)
			return abstractContent.hashCode();
		if(goal != null)
			return goal.hashCode();
		if(fact != null)
			return fact.hashCode();
		return 0;
	}
	
	@Override
	public String toString()
	{
		return this.toString(0, this.getDepth(), null);
	}
	
	public String toString(Specialty spec)
	{
		return this.toString(0, this.getDepth(), spec);
	}
	
	public String toString(int depthIn, int depthTo, Specialty spec)
	{
		String ret, end;
		if(depthIn < 2 || depthTo < 2)
		{
			ret = agent + ".!" + (int)(pressure.getValue().floatValue() * 100) + ".*" + specialty.toString() + (spec != null ? "(" + (int)(Logix.similarity(specialty, spec) * 100) + ")" : "") + ".~" + (int)(persistence.getValue().floatValue() * 100) + "(";
			end = ")";
		}
		else
		{
			ret = ".";
			end = "";
		}
		String content = "";
		if(abstractContent != null)
			content = abstractContent.toString();
		if(goal != null)
			content = goal.toString();
		if(fact != null)
			content = fact.toString(depthIn + 1, depthTo - 1, spec);
		return ret + content + end;
	}
	
	public Collection<Fact> toCollection()
	{
		Collection<Fact> ret = new ArrayList<Fact>();
		ret.add(this);
		return ret;
	}
	
	public void setAgentFactBalance(double balance)
	{
		agentFactBalance = balance;
	}

	public double getAgentFactBalance()
	{
		return agentFactBalance;
	}
}
