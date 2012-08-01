package P2PAgent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;

import base.Environment;
import base.Message;
import base.Message.Type;
import agent.AbstractAgent;
import agent.AbstractMeasure.NumericMeasure;
import agent.AgentID;
import agent.Location;
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
	private Set<Item>					items; //Set of items that our agent has
	private Set<Item>					itemsWanted;//Set which contains the items that our agent wants			
	private Map<AgentID, Set<Item>>		pendingQueries;	//Map which contains the pending queries from the others agents		
	private Map<Item, Set<AgentID>>		itemsLocation; //Map which contains the items location that our agent knows
	private Set<AgentID>				contacts;//Set of the other agent that our agent knows
	private Measures					measures;//all the measures of our agent
	private NumericMeasure				probability;//probability that our agent will send a request about a file that it doesn't want
	final static int					maxNumberOfContacts	= 10;
	private Location					location;//location of our agent on the view
	private List<Message<?>>			waitingMessage;//inbox for all the messages(request, data...) that our agent received

	@SuppressWarnings("hiding")
	public P2PAgent(Environment parent, AgentID id,Location loc, double probability)
	{
		super();
		this.id = id;
		this.items = new HashSet<Item>();
		this.itemsWanted = new HashSet<Item>();
		this.pendingQueries = new HashMap<AgentID, Set<Item>>();
		this.itemsLocation = new HashMap<Item, Set<AgentID>>();
		this.contacts = new HashSet<AgentID>();
		this.measures=new Measures(this.id);
		this.probability=(NumericMeasure) this.measures.createMeasure(new NumericMeasure(probability,MeasureName.PROBABILITY));
		this.location=(Location) this.measures.createMeasure(loc);
	}

	@Override
	protected void agentPrint()
	{
		// TODO Auto-generated method 
		StringBuffer statPrint = new StringBuffer();
		statPrint.append("\" agent ").append(this.id);
		statPrint.append(" has ").append(this.items.size()).append(" items");
		statPrint.append(" and it knows ").append(this.itemsLocation.size()).append("items");
		statPrint.append(" and it wants ").append(this.itemsWanted.size()).append("items");
		
		log.li("~", statPrint);
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
	/**function to receive a message from the others agents
	 * 
	 * @param msg
	 */
	public void receiveMessage(Message<?> msg)
	{
		log.lf("received ~", msg);
		this.waitingMessage.add(msg);
	}
	
	/**
	 * step for one agent
	 */
	@Override
	public void step()
	{
		// TODO Auto-generated method 
		
		//print some information about the agent in the logs
		agentPrint();
		
		//Treatment of the pending queries
		this.pendingQueriesTreatement();
		
		// we send our requests to our contacts about the items that our agent wants
		for(AgentID contact: this.contacts)
		{
			this.sendMessage(contact, new Message<Set<Item>>(this.id,Type.REQUEST, this.itemsWanted));
		}
		
		//we treat all the waitingMessage that we had previously received
		this.waitingMessageTreatment();
	}
	
	/**
	 * Treatment of the pending queries
	 */
	private void pendingQueriesTreatement()
	{
		Iterator<Item> iteratorQueries=null;
		Set<Item> itemsToSend=new HashSet<Item>();
		Map<AgentID, Set<Item>> locationOfItemsRequested=new HashMap<AgentID, Set<Item>>();
		Set<Item> itemsToRequest=new HashSet<Item>();
		//our agent checks all the pendingQueries and try to respond
		for(Entry<AgentID, Set<Item>> queries: this.pendingQueries.entrySet())
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
					AgentID[] possessors=(AgentID[]) this.itemsLocation.get(items).toArray();
					AgentID possessor= possessors[possessors.length-1];//we take the last agent of the list
					Set<Item> itemPossessed=null;
					if(locationOfItemsRequested.containsKey(possessor))
					{
						itemPossessed=locationOfItemsRequested.get(possessor);
					}
					else
					{
						itemPossessed=new HashSet<Item>();
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
				this.sendMessage(queries.getKey(), new Message<Map<AgentID,Set<Item>>>(this.id,Type.INFORM,locationOfItemsRequested));
			}
			//if we have some items to send, we send them here
			if(!itemsToSend.isEmpty())
			{
				this.sendMessage(queries.getKey(), new Message<Set<Item>>(this.id,Type.DATA,itemsToSend));
			}
			//if we have some request to send, we send them here
			if(!itemsToRequest.isEmpty())
			{
				for(AgentID contact: contacts)
				{
					this.sendMessage(contact, new Message<Set<Item>>(this.id, Type.REQUEST,itemsToRequest));
				}
			}			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void waitingMessageTreatment()
	{
		for(Message<?> msg: waitingMessage)
		{
			switch (msg.getType())
			{
				//An other agent asks our agent if he has this items or if he knows where to find them
				case REQUEST:
					Set<Item> queriesItems=null;
					if(pendingQueries.containsKey(msg.getFrom()))
					{
						queriesItems = this.pendingQueries.get(msg.getFrom());					
					}
					else
					{
						queriesItems=new HashSet<Item>();
					}
					queriesItems.addAll((Set<Item>) msg.getContents());
					this.pendingQueries.put(msg.getFrom(), queriesItems);
				break;
				
				//an other agent informs our agent where to find some items that he wants
				case INFORM:
					Map<AgentID, Set<Item>> responseToOurRequests=(Map<AgentID, Set<Item>>) msg.getContents();
					for(Entry<AgentID, Set<Item>> information: responseToOurRequests.entrySet())
					{
						//our agent reacts by sending a request to the agent which has the requested items
						this.sendMessage(information.getKey(),new Message<Set<Item>>(this.id,Type.REQUEST,information.getValue()));
					}
				break;
				
				//response to a request
				case DATA:
					for(Item itemResponse:(Set<Item>) msg.getContents())
					{
						//the item is for him, our agent "downloads" it
						if(this.itemsWanted.contains(itemResponse))
						{
							items.add(itemResponse);
						}
						//the item is not for him, he put it in the itemLocation
						else
						{
							Set<AgentID> possessor=null;
							if(itemsLocation.containsKey(itemResponse))
							{
								possessor=itemsLocation.get(itemResponse);
							}
							else
							{
								possessor=new HashSet<AgentID>();
							}
							this.itemsLocation.put(itemResponse, possessor);
						}
					}
					this.itemsWanted.removeAll((Set<Item>) msg.getContents());
				break;
			}
		}
		//we erase the inbox at the end
		waitingMessage.clear();
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
