/**
 * 
 */
package KCAAgent;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;



public class Intention
{
	static class IntentionList extends LinkedList<Intention>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3927412629464953411L;

		public Intention containsGoal(Goal g)
		{
			for(Iterator<Intention> it = iterator(); it.hasNext();)
			{
				Intention i = it.next();
				if(i.goal.equals(g))
					return i;
			}
			return null;
		}
		
		@Override
		public String toString()
		{
			return super.toString();
		}
	}
	
	public Goal				goal			= null;
	public Queue<Action>	plan			= new LinkedList<Action>();
	protected int			nStepsWaited	= 0;
	protected int			nTimesFailed	= 0;
	protected boolean		waiting			= false;
	
	public Intention(Goal g)
	{
		goal = g;
	}
	
	@SuppressWarnings("unused")
	public Intention(Goal g, Collection<Action> actions)
	{
		plan.addAll(actions);
	}
	
	public boolean isWaiting()
	{
		return waiting;
	}
	
	public void waitStep()
	{
		waiting = true;
		nStepsWaited++;
	}
	
	public void fail()
	{
		nTimesFailed++;
	}
	
	@Override
	public String toString()
	{
		return ">>" + goal + "[w" + nStepsWaited + " f" + nTimesFailed + "]" + ": \t\t" + plan;
	}
}