package P2PAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import base.Environment;
import base.Message;
import base.Message.Type;
import agent.AbstractAgent;
import agent.AbstractMeasure.NumericMeasure;
import agent.AgentID;
import agent.Measure;
import agent.MeasureName;
import agent.Measures;

/**
 * class which will simulate the basic behavior of an agent on a p2p network
 * @author Guillaume Masson
 *
 */
public class P2PAgent extends AbstractAgent
{
	private List<Item>					items; //List of items that our agent has
	private List<Item>					itemsWanted;//List which contains the items that our agent wants			
	private Map<AgentID, List<Item>>	pendingQueries;	//Map which contains the pending queries from the others agents		
	private Map<Item, List<AgentID>>	itemsLocation; //Map which contains the items location that our agent knows
	private List<AgentID>				contacts;//list of the other agent that our agent knows
	private Measures					measures;
	private NumericMeasure				probability;
	final static int					maxNumberOfContacts	= 10;

	public P2PAgent(Environment parent, AgentID id,double probability, int nsteps)
	{
		super();
		this.id = id;
		this.items = new ArrayList<Item>();
		this.itemsWanted = new ArrayList<Item>();
		this.pendingQueries = new HashMap<AgentID, List<Item>>();
		this.itemsLocation = new HashMap<Item, List<AgentID>>();
		this.contacts = new ArrayList<AgentID>();
		this.measures=new Measures(this.id);
		this.probability=(NumericMeasure) this.measures.createMeasure(new NumericMeasure(probability,MeasureName.PROBABILITY));
	}

	@Override
	protected void agentPrint()
	{
		// TODO Auto-generated method stub

	}

	/*protected void addPartner(AgentID agent, int step)
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
	}*/

	@Override
	/**
	 * function to send a message to the others agents
	 * @param to
	 * @param msg
	 */
	protected void sendMessage(AgentID to, Message<?> msg)
	{
		getAgentById(to).receiveMessage(msg);
		log.lf("sending to ~ :", to, msg);
	}

	@Override
	@SuppressWarnings("unchecked")
	/**function to receive a message from the others agents
	 * 
	 * @param msg
	 */
	public void receiveMessage(Message<?> msg)
	{
		log.lf("received ~", msg);
		switch (msg.getType())
		{
			//An other agent asks our agent if he has this items or if he knows where to find them
			case REQUEST:
				List<Item> queriesItems=null;
				if(pendingQueries.containsKey(msg.getFrom()))
				{
					queriesItems = this.pendingQueries.get(msg.getFrom());					
				}
				else
				{
					queriesItems=new ArrayList<Item>();
				}
				queriesItems.addAll((List<Item>) msg.getContents());
				this.pendingQueries.put(msg.getFrom(), queriesItems);
			break;
			
			//an other agent informs our agent where to find some items that he wants
			case INFORM:
				Map<AgentID, List<Item>> responseToOurRequests=(Map<AgentID, List<Item>>) msg.getContents();
				for(Entry<AgentID, List<Item>> information: responseToOurRequests.entrySet())
				{
					//our agent reacts by sending a request to the agent which has the requested items
					this.sendMessage(information.getKey(),new Message<List<Item>>(this.id,Type.REQUEST,information.getValue()));
				}
			break;
			
			//response to a request
			case DATA:
				for(Item itemResponse:(List<Item>) msg.getContents())
				{
					//the item is for him, our agent "downloads" it
					if(this.itemsWanted.contains(itemResponse))
					{
						items.add(itemResponse);
					}
					//the item is not for him, he put it in the itemLocation
					else
					{
						List<AgentID> possessor=null;
						if(itemsLocation.containsKey(itemResponse))
						{
							possessor=itemsLocation.get(itemResponse);
						}
						else
						{
							possessor=new ArrayList<AgentID>();
						}
						this.itemsLocation.put(itemResponse, possessor);
					}
				}
				this.itemsWanted.removeAll((List<Item>) msg.getContents());
			break;
		}
	}
	@Override
	public void step()
	{
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Treatment of the pending queries
	 */
	private void pendingQueriesTreatement()
	{
		Iterator<Item> iteratorQueries=null;
		List<Item> itemsToSend=new ArrayList<Item>();
		Map<AgentID, List<Item>> locationOfItemsRequested=new HashMap<AgentID, List<Item>>();
		List<Item> itemsToRequest=new ArrayList<Item>();
		//our agent checks all the pendingQueries and try to respond
		for(Entry<AgentID, List<Item>> queries: this.pendingQueries.entrySet())
		{
			iteratorQueries= queries.getValue().iterator();
			
			for(Iterator<Item> it = iteratorQueries; it.hasNext();)
			{
				Item requestedItem = it.next();
				
				//if our agent possessed the requested item, he will send it
				if(items.contains(requestedItem))
				{
					itemsToSend.add(requestedItem);
				}
				//if our agent doesn't possessed the item requested but he knows where to find it, he will inform the agent who queries this item
				else if(this.itemsLocation.containsKey(items))
				{
					AgentID possessor=this.itemsLocation.get(items).get(this.itemsLocation.get(items).size()-1); //we take the last agent of the list, which possessed the item
					List<Item> itemPossessed=null;
					if(locationOfItemsRequested.containsKey(possessor))
					{
						itemPossessed=locationOfItemsRequested.get(possessor);
					}
					else
					{
						itemPossessed=new ArrayList<Item>();
					}
					itemPossessed.add(requestedItem);
					locationOfItemsRequested.put(possessor, itemPossessed);
				}
				//else our agent interrogate this contacts with a certain probability
				else
				{
					
					double calculateProba=Math.random();
					//we calculate the probability, to know if we have to send the request to our contacts
					if(calculateProba<=this.probability.getValue().doubleValue())
					{
						//we will send the request for the item
						itemsToRequest.add(requestedItem);
					}
				}
			}
			
			//if we have some items locations, we send them here
			if(!locationOfItemsRequested.isEmpty())
			{
				this.sendMessage(queries.getKey(), new Message<Map<AgentID,List<Item>>>(this.id,Type.INFORM,locationOfItemsRequested));
			}
			//if we have some items to send, we send them here
			if(!itemsToSend.isEmpty())
			{
				this.sendMessage(queries.getKey(), new Message<List<Item>>(this.id,Type.DATA,itemsToSend));
			}
			//if we have some request to send, we send them here
			if(!itemsToRequest.isEmpty())
			{
				for(AgentID contact: contacts)
				{
					this.sendMessage(contact, new Message<List<Item>>(this.id, Type.REQUEST,itemsToRequest));
				}
			}			
		}
	}

	@Override
	public Measure<?> getMeasure(MeasureName measure)
	{
		// TODO Auto-generated method stub
		return this.measures.getMeasures().get(measure);
	}

	@Override
	public Map<?, ?> getAllMeasures()
	{
		// TODO Auto-generated method stub
		return this.measures.getMeasures();
	}
	
	private static P2PAgent getAgentById(AgentID id){
	 return Annuaire.annuaire.get(id);
	}

}
