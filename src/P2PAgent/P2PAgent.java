package P2PAgent;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import base.Environment;
import base.Message;
import KCAAgent.Location;
import agent.AbstractAgent;
import agent.AgentID;
import agent.AbstractMeasure;
import agent.Measure;
import agent.MeasureName;

public class P2PAgent extends AbstractAgent
{
	private List<Items>					items; //List of items that our agent has
	private List<Items>					itemsWanted;//List which contains the items that our agent wants			
	private Map<AgentID, List<Items>>	pendingQueries;	//Map which contains the pending queries from the others agents		
	/*
	 * Map which contains the id of one of the partner of the agent and the step
	 * number where him and the agent did their last exchange (the exchange can
	 * be an answer or a request)
	 */
	private Map<AgentID, Integer>		contacts;//list of the other agent that our agent knows

	final static int					maxNumberOfContacts	= 10;

	public P2PAgent(Environment parent, AgentID id, int nsteps)
	{
		super();
		this.id = id;
	}

	protected void agentPrint()
	{
		// TODO Auto-generated method stub

	}

	protected void addPartner(AgentID agent, int step)
	{
		if (contacts.size() >= maxNumberOfContacts)
		{
			this.deleteUselessContact();
		}
		this.contacts.put(agent, new Integer(step));
	}

	protected void deleteUselessContact()
	{
		int smallestStep=-1;
		AgentID uselessAgent;
		for(Entry<AgentID, Integer> partner : this.contacts.entrySet())
		{
			if((smallestStep>=partner.getValue().intValue()) || (smallestStep==-1))
			{
				smallestStep=partner.getValue().intValue();
				uselessAgent=partner.getKey();
			}
		}
	}



	@Override
	public void step()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Measure getMeasure(MeasureName measure)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map getAllMeasures()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
