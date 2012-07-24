package KCAAgent;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import base.QuickSet;
import base.QuickSet.QuickFadable;

public class Goal implements QuickFadable
{
	public enum GoalType {
		INFORM,

		// GET,
		
		FREE,
	}
	
	public static Collection<Goal> filterCollectionOfType(Collection<Goal> c, GoalType type)
	{
		Collection<Goal> ret = new HashSet<Goal>();
		for(Goal g : c)
			if(g.type == type)
				ret.add(g);
		return ret;
	}
	
	static class GoalComparator implements Comparator<Goal>
	{
		@Override
		public int compare(Goal g1, Goal g2)
		{
			int ret;
			if(g1.importance == g2.importance)
			{
				if(g1.type != g2.type)
					ret = (g1.type == GoalType.FREE) ? -1 : 1;
				else
					ret = 0;
			}
			else
				ret = -(int)Math.signum(g1.importance - g2.importance);
//			System.out.println(g1.type + ":" + g1.importance + " : " + g2.type + ":" + g2.importance + " : " + ret);
			return ret;
		}
	}
	
	static class GoalList extends QuickSet<Goal>
	{
		public GoalList()
		{
			super(new GoalComparator());
		}
		
		public void clear(KnowledgeBase kb)
		{
			for(Iterator<Goal> it = this.iterator(); it.hasNext();)
			{
				Goal goal = it.next();
				if((goal.getType() == GoalType.INFORM) && (kb.contains(goal.getFact()) == null))
					it.remove();
			}
		}
		
		@Override
		public boolean add(Goal element)
		{
			Goal g = this.containsReturn(element);
			if(g != null)
			{
				if(element.compareTo(g) > 0)
				{
					// the added goal is more important
					// update the old goal (same in fact) to the new importance
					g.importance = element.importance;
					this.update(g);
					return true;
				}
				return false;
			}
			return super.add(element);
		}
		
		public StringBuffer printGoals()
		{
			StringBuffer ret = new StringBuffer();
			
			ret.append("goals [");
			ret.append(this.size());
			ret.append("]:");
			
			for(Goal goal : this)
				ret.append("\n\t").append(goal);
			
			return ret;
		}
	}
	
	GoalType	type;
	// Data relatedData = null; // used in GET
	Fact		relatedFact	= null; // used in INFORM
									
	float		importance	= 0.0f; // the importance is equivalent to the pressure. in cases it will be directly translated
									
	public Goal()
	{
		type = GoalType.FREE;
	}
	
//	public Goal(Data data)
//	{
//		type = GoalType.GET;
//		this.relatedData = data;
//	}
	
	public Goal(Fact fact)
	{
		type = GoalType.INFORM;
		this.relatedFact = fact;
	}
	
	public GoalType getType()
	{
		return type;
	}
	
//	public Data getData()
//	{
//		return relatedData;
//	}
	
	public Fact getFact()
	{
		return relatedFact;
	}
	
	public float getImportance()
	{
		return importance;
	}
	
	public Goal setImportance(float newDataGoalImportance)
	{
		this.importance = newDataGoalImportance;
		return this;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		if(obj.getClass() != Goal.class)
			return false;
		Goal g = (Goal)obj;
		if(type != g.type)
			return false;
		switch(type)
		{
		case FREE:
			return true;
//		case GET:
//			return relatedData.equals(g.relatedData);
		case INFORM:
			return relatedFact.equals(g.relatedFact);
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		String ret = type.toString() + ".!" + (int)(importance * 100) + ".";
		switch(type)
		{
		case FREE:
			break;
//		case GET:
//			ret = ret + "(" + relatedData.toString() + ")";
//			break;
		case INFORM:
			ret = ret + "(" + relatedFact.toString() + ")";
			break;
		}
		return ret;
	}
	
	public int compareTo(Goal g2)
	{
		if(this.importance == g2.importance)
		{
			if(this.type != g2.type)
				return (this.type == GoalType.FREE) ? -1 : 1;
			return 0;
		}
		return -(int)Math.signum(this.importance - g2.importance);
	}
	
	@Override
	public void quickFade(float rate)
	{
		if(importance < 1)
			importance *= (1 - rate);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
}