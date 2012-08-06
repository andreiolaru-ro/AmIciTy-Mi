package P2PAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import agent.AbstractAgent;
import agent.AbstractMeasure.NumericMeasure;
import agent.AgentID;
import agent.Location;
import agent.Measure;
import agent.MeasureName;
import agent.Measures;
import base.Environment;
import base.Message;
import base.Message.Type;

/**
 * class which will simulate the basic behaviors of an agent on a p2p network
 * @author Guillaume Masson
 *
 */
public class P2PAgent extends AbstractAgent
{
	/** Set of items that our agent has*/
	private Set<Item>					items; 
	/** Set which contains the items that our agent wants*/
	private Set<Item>					itemsWanted;	
	/**Map which contains the pending queries from the others agents*/
	private Map<AgentID, Set<Item>>		pendingQueries;
	/**Map which contains the items location that our agent knows*/
	private Map<Item, Set<AgentID>>		itemsLocation; 
	/**Set of the other agent that our agent knows*/
	private Set<AgentID>				contacts;
	/**all the measures of our agent*/
	private Measures					measures;
	/**probability that our agent will send a request about a file that it doesn't want*/
	private NumericMeasure				probability;
	final static int					maxNumberOfContacts	= 10;
	/**inbox for all the messages(request, data...) that our agent received*/
	private List<Message<?>>			waitingMessage;
	/** Map to do the link between an id and an agent*/
	private static HashMap<AgentID,P2PAgent>	directory;

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
		this.waitingMessage=new ArrayList<Message<?>>();
		//this.log=new Log(this);
		if(directory==null)
		{
			directory=new HashMap<AgentID, P2PAgent>();
		}
		P2PAgent.directory.put(this.id, this);
		
	}

	/**function to print some basic things about an agent*/
	@Override
	protected void agentPrint()
	{
		// TODO Auto-generated method 
		StringBuffer statPrint = new StringBuffer();
		statPrint.append("\" agent ").append(this.id);
		statPrint.append(" has ").append(this.items.size()).append(" items");
		statPrint.append(" and it knows ").append(this.itemsLocation.size()).append("items");
		statPrint.append(" and it wants ").append(this.itemsWanted.size()).append("items");
		
		//log.li("~", statPrint);
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
		if(P2PAgent.directory.containsKey(to))
		{
			P2PAgent.getAgentById(to).receiveMessage(msg);
			//log.lf("sending to ~ :", to, msg);
		}
		
	}

	@Override
	/**function to receive a message from the others agents
	 * 
	 * @param msg
	 */
	public void receiveMessage(Message<?> msg)
	{
		//log.lf("received ~", msg);
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
		
		//we treat all the waitingMessage that we had previously received
		this.waitingMessageTreatment();
		
		// we send our requests to our contacts about the items that our agent wants
		for(AgentID contact: this.contacts)
		{
				this.sendMessage(contact, new Message<Set<Item>>(this.id,Type.REQUEST, this.itemsWanted));
		}

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
		Set<Item> itemToRemove=new HashSet<Item>();
		AgentID p2pAgent=null;
		
		//our agent checks all the pendingQueries and try to respond
		for(Entry<AgentID, Set<Item>> queries: this.pendingQueries.entrySet())
		{
			p2pAgent=queries.getKey();
			iteratorQueries= queries.getValue().iterator();
			for(Iterator<Item> it = iteratorQueries; it.hasNext();)
			{
				Item requestedItem = it.next();
				//if our agent possessed the requested item, he will send it
				if(items.contains(requestedItem))
				{
					itemsToSend.add(requestedItem);
					itemToRemove.add(requestedItem);

				}
				//if our agent doesn't possessed the item requested but he knows where to find it, he will inform the agent who queries this item
				else if(this.itemsLocation.containsKey(requestedItem))
				{
					Iterator<AgentID> possessors=this.itemsLocation.get(requestedItem).iterator();
					AgentID possessor= possessors.next();//we take the last agent of the list	
					
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
					itemToRemove.add(requestedItem);
				}
				//else our agent interrogate his contacts with a certain probability
				else
				{
					
					double calculateProba=Math.random();
					//we calculate the probability, to know if we have to send the request to our contacts
					if(calculateProba<=this.probability.getValue().doubleValue())
					{
						System.out.println(this.id+"ajoute requ�te");
						//we will send the request for the item
						itemsToRequest.add(requestedItem);
						itemToRemove.add(requestedItem);
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
			this.pendingQueries.get(p2pAgent).removeAll(itemToRemove);
			itemToRemove.clear();
		}

	}
	
	/**
	 * Treatment of the messages that the agent has previously received
	 */
	@SuppressWarnings("unchecked")
	private void waitingMessageTreatment()
	{
		if(!waitingMessage.isEmpty())
		{
			for(Message<?> msg: waitingMessage)
			{
				switch (msg.getType())
				{
					//An other agent asks our agent if he has these items or if he knows where to find them
					case REQUEST:
						System.out.println("requ�te de "+msg.getFrom());
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
								System.out.println("l'agent "+this.id+" download"+itemResponse);
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
								possessor.add(msg.getFrom());
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
	 return P2PAgent.directory.get(id);
	}
	
	static class test{
		public static void main(String[] args)
		{
			P2PAgent agent1= new P2PAgent(null, new AgentID(""+1), new Location(0,0), 0.5);
			P2PAgent agent2= new P2PAgent(null, new AgentID(""+2), new Location(0,0), 0.5);
			P2PAgent agent3= new P2PAgent(null, new AgentID(""+3), new Location(0,0), 0.5);
			P2PAgent agent4= new P2PAgent(null, new AgentID(""+4), new Location(0,0), 0.5);
			
			agent1.contacts.add(agent3.id);
			agent3.contacts.add(agent4.id);
			
			Item item1=new Item(1);
			Item item2=new Item(2);
			Item item3=new Item(3);
			Item item4=new Item(4);
			
			agent1.items.add(item1);
			agent1.items.add(item4);
			agent3.items.add(item2);
			agent4.items.add(item3);
			
			agent1.itemsWanted.add(item2);
			agent1.itemsWanted.add(item3);
			
			System.out.println(item2);
			System.out.println(item3);
			
			List<P2PAgent> agents= new ArrayList<P2PAgent>();
			agents.add(agent1);
			agents.add(agent2);
			agents.add(agent3);
			agents.add(agent4);
			
			int cpt2=0;
			while(cpt2!=10)
			{
				for(P2PAgent agent : agents)
				{
					agent.step();
				}
				cpt2++;
				System.out.println(cpt2);
			}
		}
	
		
	}
	
}
