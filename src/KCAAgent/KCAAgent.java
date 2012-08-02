package KCAAgent;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import logging.Log;
import KCAAgent.Goal.GoalList;
import KCAAgent.Logix.Domain;
import XMLParsing.KCAScenario;
import agent.AbstractAgent;
import agent.AgentID;
import agent.Location;
import agent.Measure;
import base.Environment;
import base.Message;
import base.Message.Type;

public class KCAAgent extends AbstractAgent
{
	private static double			balanceMinimum			= 0.4;


	// payload
	private int						usedCapacity			= 0;
	// private Collection<Data> data = new Vector<Data>(); // data held by this
	// agent

	// internal workings
	private KnowledgeBase			kb;
	private GoalList				goals					= new GoalList();
	// these
	// are
	// the
	// agent's
	// desires.
	// they
	// must
	// be
	// transformed
	// in
	// intentions
	// in
	// order
	// to
	// take
	// action
	// for
	// them
	private Goal					freeGoal				= null;
	private Intention.IntentionList	intentions				= new Intention.IntentionList();

	// behaviour
	private Specialty				agent_specialty			= null;								// sorry
																									// for
																									// the
																									// naming
																									// convention
	public Specialty[]				specialtyHistory;
	public int						currentSpecialtyIndex	= -1;
	private float					agent_pressure			= 0.0f;								// sorry
																									// for
																									// the
																									// naming
																									// convention
	private float					lowPressure				= 0.0f;
	private float					highPressure			= 0.0f;								// only
																									// positive
																									// measures

	// communication
	// up
	private Environment				parent;
	@SuppressWarnings("unused")
	private Vector<Fact>			externalRequests		= new Vector<Fact>();					// data
																									// id's
																									// requested
																									// from
																									// outside
	// peer
	private Queue<Message>			inbox					= new PriorityBlockingQueue<Message>();
	private Queue<Message>			atemporalInbox			= new PriorityBlockingQueue<Message>();
	private Map<AgentID, KCAAgent>		neighbours				= new HashMap<AgentID, KCAAgent>();

	private double					agentBalance			= 0.0;
	private double					agentUselessFacts		= 0.0;

	// logging
	private boolean					selected				= false;
	private Log						log						= new Log(this);

	@SuppressWarnings("hiding")
	public KCAAgent(Environment parent, AgentID id, Location loc, int capacity,
			int nsteps)
	{
		this(parent, id, loc, capacity, null, nsteps);
	}

	public void setParent(Environment env)
	{
		this.parent = env;
	}

	@SuppressWarnings({ "hiding", "unused" })
	KCAAgent(Environment parent, AgentID id, Location loc, int capacity,
			Specialty spec, int nsteps)
	{
		super();
		this.id = id;
		this.location = loc;
		this.parent = parent;
		this.capacity = capacity;
		this.agent_specialty = (spec == null) ? new Specialty() : spec;
		// specialtyHistory = new Specialty[nsteps+1];
		/*
		 * specialtyHistory = new Specialty[10000]; specialtyHistory[0] = new
		 * Specialty(); specialtyHistory[0].set(agent_specialty);
		 * currentSpecialty = 0;
		 */

		this.kb = new KnowledgeBase(this, agent_specialty);

		this.freeGoal = new Goal();
	}

	public void updateNeighbors(boolean recursive)
	{
		for (KCAAgent agent : parent.getAgents())
		{
			if (!equals(agent)
					&& location.getDistance(agent.getLocation()) <= 1.5)
			{
				if (!neighbours.containsKey(agent.id))
				{
					neighbours.put(agent.id, agent);
					if (recursive)
					{
						agent.neighbours.put(id, this);
					}
				}
			} else
			{
				if (neighbours.containsKey(agent.id))
				{
					neighbours.remove(agent.id);
					if (recursive)
					{
						agent.neighbours.remove(id);
					}
				}
			}
		}
	}


	public void setHistory(int nsteps)
	{
		specialtyHistory = new Specialty[nsteps + 1];
		specialtyHistory[0] = new Specialty();
		specialtyHistory[0].set(agent_specialty);
		currentSpecialtyIndex = 0;
	}

	@Override
	protected void sendMessage(AgentID to, Message msg)
	{
		if (to == null)
			parent.produce(msg);
		else if (neighbours.containsKey(to))
		{
			log.lf("sending to ~ :", to, msg);
			neighbours.get(to).receiveMessage(msg);
		} else
		{
			log.le("agent ~ not a neighbor (m: ~)", to, msg);
		}
	}

	@Override
	public void receiveMessage(Message msg)
	{
		if (msg.getFrom() == null)
			log.li("received ~", msg);
		else
			log.lf("received ~", msg);
		atemporalInbox.offer(msg);
	}

	@Override
	public void step()
	{
		// We will really make it BDI this time
		// really? we could just make it cognitive... why necessarily BDI?

		agentPrint();

		// fill the inbox for this time step
		for (Iterator<Message> it = atemporalInbox.iterator(); it.hasNext();)
		{
			Message msg = it.next();
			if (!msg.isFuture())
			{
				inbox.offer(msg);
				it.remove();
			}
		}
		/*
		 * get data received
		 * 
		 * this is simple and might be very necessary to do first. requires no
		 * plans.
		 */
		// Collection<Fact> receivedData = receiveData();

		/*
		 * fulfill requests
		 * 
		 * it is questionable if this should be done with no planning, but it
		 * will be done like this for now
		 */
		// Collection<Fact> sentData = sendData();

		/*
		 * revise beliefs
		 * 
		 * this will mean learning new facts from the exterior.
		 * 
		 * the number of facts assimilated depends on the pressure on the agent.
		 * 
		 * pressure of messages will imply that beliefs are adopted faster when
		 * their pressure is higher.
		 * 
		 * also here:
		 * 
		 * check if plans have succeeded and remove them if they did.
		 * 
		 * check if plans are impossible and remove them if they are.
		 */
		reviseBeliefs(Math.max(Logix.minimalBeliefProcessing(), (int) (inbox
				.size() * Logix.availableBeliefProcessing(agent_pressure,
				lowPressure, highPressure))));

		/*
		 * select goal
		 * 
		 * select an achievable goal.
		 * 
		 * make some plan (series of intentions).
		 * 
		 * define commitment.
		 * 
		 * add plan to ongoing plans
		 */
		plan();

		/*
		 * execute
		 * 
		 * execute the most important plan (as in one [or more] action(s) from
		 * the plan).
		 */
		execute();

		calculateAgentBalance();
	}

	@Override
	protected void agentPrint()
	{
		StringBuffer statPrint = new StringBuffer();
		statPrint.append("* ").append(agent_specialty).append("\t");
		if (agent_pressure < lowPressure)
			statPrint.append("!").append((int) (100 * agent_pressure))
					.append("<").append((int) (100 * lowPressure)).append("<")
					.append((int) (100 * highPressure));
		if (agent_pressure >= lowPressure && agent_pressure <= highPressure)
			statPrint.append((int) (100 * lowPressure)).append("<!")
					.append((int) (100 * agent_pressure)).append("<")
					.append((int) (100 * highPressure));
		if (agent_pressure > highPressure)
			statPrint.append((int) (100 * lowPressure)).append("<")
					.append((int) (100 * highPressure)).append("<!")
					.append((int) (100 * agent_pressure));
		statPrint
		/* .append("\t Kfading: ").append(Logix.memoryFade()) */.append("\t")
				.append(usedCapacity).append("/").append(capacity);
		// statString = statString + "\t Intentions [" + intentions.size() +
		// "]: \n" + intentions;

		log.li("~", statPrint);
		// log.li(neighbours.keySet().toString());
	}

	/**
	 * gets from the inbox the received DataContent
	 * 
	 * @return the collection of the (valid) Fact containing DataContent
	 */
	/*
	 * private Collection<Fact> receiveData() { Collection<Fact> ret = new
	 * Vector<Fact>(); for(Iterator<Message> it = inbox.iterator();
	 * it.hasNext();) { Message msg = it.next(); if(msg.getType() == Type.DATA)
	 * { for(Fact f : msg.getFacts()) { Data d = f.getData();
	 * if(!d.hasContent())
	 * log.le("data in data message not containing content. discarding"); else {
	 * // add data if(!data.contains(d)) { data.add(d); // note that we now have
	 * the data. if(msg.getFrom() == null) // keep the original fact, as it
	 * contains context information ret.add(f); else ret.add(new Fact(id, d));
	 * usedCapacity += d.getSize(); log.li("new data added: ~", d); } else
	 * log.lw("data already contained: ~", d); // note that the sending agent
	 * has that data if(msg.getFrom() != null) ret.add(f); } } it.remove(); } }
	 * 
	 * return ret; }
	 */
	/*
	 * Collection<Fact> sendData() { Vector<Fact> ret = new Vector<Fact>();
	 * for(Iterator<Message> it = inbox.iterator(); it.hasNext();) { Message msg
	 * = it.next(); if(msg.getType() == Type.REQUEST) { for(Fact f :
	 * msg.getFacts()) { boolean found = false; Data d = f.getData(); for(Data
	 * ds : data) if(ds.equals(d)) { Fact fr = new Fact(id, ds); Message reply =
	 * new Message(id, Type.DATA, fr.toCollection()); sendMessage(msg.getFrom(),
	 * reply); // we suppose that the agent will be having this data ret.add(new
	 * Fact(msg.getFrom(), d)); found = true; break; } if(!found) {
	 * if(msg.getFrom() == null) { ret.add(f); externalRequests.add(f); } else {
	 * log.le("requested data not found: ~", d); // this should never happen
	 * though // request is ignored } } } it.remove(); } } return ret; }
	 */
	/**
	 * 
	 * @param rcvD
	 *            specifies previously partially processed facts that contained
	 *            DataContent
	 * @param sentD
	 *            specifies data sent to neighbours, and that is now supposedly
	 *            contained by the neighbours
	 * @param amount
	 *            specifies how many external perceptions should be processed in
	 *            this call
	 */
	protected void reviseBeliefs(Integer amount)
	{
		// analyze received data
		// if already had. then why was it received again? - this to solve later
		// note that sending agent had the data - although this is probably
		// known
		// for(Fact f : rcvD)
		// {
		// if(f.getAgent() == null)
		// { // fact is about our new data that was injected from the exterior
		// Fact fa = new Fact(id, f.getData()); // create new fact
		// kb.insert(Logix.setNewInjectedDataFact(this.agent_specialty, fa, f));
		// // set. add
		// goals.add(Logix.makeNewDataInformGoal(fa)); // spread
		// }
		// else if(f.getAgent() == id)
		// { // fact is about our new data
		// kb.insert(Logix.setNewDataFact(this.agent_specialty, f)); // set. add
		// goals.add(Logix.makeNewDataInformGoal(f)); // spread
		// }
		// else
		// { // fact inferred about other agent
		// Fact fa = new Fact(id, f); // integrate
		// kb.insert(Logix.setDistantDataFact(this.agent_specialty, fa)); //
		// set. add
		// goals.add(Logix.makeInformGoal(fa)); // spread
		// }
		// }

		// for(Fact f : sentD)
		// {
		// if(f.getAgent() == null)
		// // unsatisfied external request
		// goals.add(Logix.makeUnExRqGoal(f));
		// else
		// {
		// Fact fa = new Fact(id, f); // integrate
		// kb.insert(Logix.setDistantDataFact(this.agent_specialty, fa, null));
		// // set. add.
		// goals.add(Logix.makeInformGoal(fa)); // spread
		// }
		// }

		// check received facts, limited by the allowed amount of processing
		// (according to agent pressure)
		int nHandled = 0;
		for (Iterator<Message> it = inbox.iterator(); it.hasNext()
				&& nHandled < amount; nHandled++)
		{
			Message m = it.next();
			switch (m.getType())
			{
			case INFORM:
				for (Fact f : m.getFacts())
				{ // getting informed on new facts

					if (f.getAgent() != null)
					{
						Fact fa = new Fact(id, f.getAbstractContentRecursive(),
								parent.getStep()); // integrate basic knowledge
						Fact fb = new Fact(id, f, parent.getStep()); // integrate
																		// knowledge
																		// about
																		// other
																		// agents
						kb.insert(Logix.setNewFact(fa, f.recurse())); // integrate
																		// the
																		// original
																		// fact
						kb.insert(Logix.setDistantFactFact(fb, f)); // integrate
																	// the
																	// knowledge
																	// about the
																	// agent
						// spread
						// goals.add(Logix.makeInformGoal(fa));
						// goals.add(Logix.makeInformGoal(fb));
					} else
					{
						Fact fa = new Fact(id, f.getAbstractContent(),
								parent.getStep());
						kb.insert(Logix.setNewFact(fa, f));
						// goals.add(Logix.makeInformGoal(fa));
					}

					// if(fa.getGoalRecursive() != null)
					// { // it's about a goal
					// if(fa.getDepth() == 1)
					// // it's a neighbour's goal
					// // collaborate
					// // TODO
					// ;
					// else
					// // TODO this souldn't really happen at this point
					// ;
					// }
					// else if(fa.getDataRecursive() != null)
					// {
					// // it's about some data
					// // integrate
					// kb.insert(Logix.setDistantDataFact(agent_specialty, fa,
					// f));
					// // spread
					// goals.add(Logix.makeInformGoal(fa));
					// // desire, if necessary
					// if(!kb.doesAgentHaveData(id, fa.getDataRecursive()))
					// goals.add(Logix.makeNormalGetGoal(fa));
					// }
					// else
					// // N/A
					// ;
				}
				break;
			case REQUEST:
			case DATA:
			default:
				// already handled
				log.le("message type not known or already handled: ~", m);
				nHandled--;
			}
			it.remove();
		}

		for (Iterator<Intention> it = intentions.iterator(); it.hasNext();)
		{
			Intention i = it.next();
			if (goals.containsReturn(i.goal) == null)
			{
				// it's out. the goal is no more active
				it.remove();
				continue;
			}
			if (i.isWaiting())
			{
				i.waitStep();
				switch (i.goal.type)
				{
				case INFORM:
				{
					// int nsuccess = 0;
					// for(AgentID agent : neighbours.keySet())
					// if(kb.doesAgentKnowFact(agent, i.goal.relatedFact))
					// nsuccess++;
					// if(nsuccess >= (neighbours.size() *
					// Logix.getInformFraction()))
					// {
					i.goal.relatedFact.fadePressure(0.01f); // if it was
															// pressure 1, take
															// it down from
															// there

					log.li("plan done: ~", i);
					// goal is satisfied
					goals.remove(i.goal);
					// plan done
					it.remove();
					// }
					break;
				}
				// case GET:
				// if(kb.doesAgentHaveData(id, i.goal.relatedData))
				// {
				// log.li("plan done: ~", i);
				// // goal is satisfied
				// goals.remove(i.goal);
				// // plan done
				// it.remove();
				// }
				// break;
				case FREE:
					if (usedCapacity <= Logix.memoryThresh() * capacity)
					{
						log.li("plan done: ~", i);
						// goal is satisfied
						goals.remove(i.goal);
						// plan done
						it.remove();
					}
					break;
				default:
					break;
				}
			}
		}

		// TODO check plans for failure / check plans for achievability

		// fade KB
		kb.fade(Logix.pressureFade(), Logix.persistenceFade());

		// revise agent pressure, interest, knowledge
		agent_pressure = kb.totalPressure();
		revisePressureLimits();
		agent_specialty = kb.reviseSpecialty();
		currentSpecialtyIndex++;
		specialtyHistory[currentSpecialtyIndex] = new Specialty();
		specialtyHistory[currentSpecialtyIndex].set(agent_specialty);

		// fade goals
		// goals.fadeAll(Logix.goalFadeRate());
		// goals.forgetTail((int)(Logix.goalMemoryFade() * goals.size()));
		// clear goals referring to facts that have been forgotten
		// goals.clear(kb);

		log.li("~", kb.printFacts());
	}

	protected void plan()
	{
		Goal primaryGoal = null, chosenGoal = null;

		for (Iterator<Goal> it = goals.iterator(); it.hasNext();)
			if (intentions.containsGoal(it.next()) == null)
				it.remove();

		// keep capacity free
		usedCapacity = kb.size();
		if (usedCapacity > Logix.memoryThresh() * capacity)
		{
			Logix.setFreeGoal(usedCapacity, capacity, freeGoal);
			if (goals.containsReturn(freeGoal) != null)
				goals.update(freeGoal);
			else
				goals.add(freeGoal);
		} else
			goals.remove(freeGoal);

		for (Fact f : kb.owned)
		{
			Goal g = Logix.makeGoal(f, agent_specialty);
			if (g != null)
				if (goals.containsReturn(g) == null)
					goals.add(g);
		}

		log.li("~", goals.printGoals());

		Iterator<Goal> it = goals.iterator();

		while (chosenGoal == null)
		{
			if (!it.hasNext())
			{
				log.li("no more goals");
				return;
			}
			primaryGoal = it.next();
			log.li("checking available goal ~", primaryGoal);

			Intention i = intentions.containsGoal(primaryGoal);
			if (i != null)
			{ // goal is already planned for
				if (i.isWaiting())
					if (i.nStepsWaited > Logix.nWaits2replan)
					{ // should try to re-plan
						intentions.remove(i);
						chosenGoal = i.goal;
					} else
						// exists, but is waiting
						// so, choose some other goal
						;
				else
				{
					// it's there already, so it should be promoted
					// if it's already first, it's ok, the function will be
					// effectless
					Logix.promoteGoal(primaryGoal, intentions);
					// enough planning
					log.li("goal promoted ~", primaryGoal);
					return;
				}
			} else
				// the goal is not already planned for
				chosenGoal = primaryGoal;
		}

		// we have a goal that is new, and must be planned
		log.li("primary concern is ~", primaryGoal);

		switch (primaryGoal.type)
		{
		case FREE:
		{
			// choose data
			Intention intention = new Intention(primaryGoal,
					new Action().toCollection());
			intentions.add(intention);
			intentions.addFirst(intention);
			log.li("new intention: ~", intention);
			return;
		}
		// case GET:
		// {
		// Intention intention = new Intention(primaryGoal);
		// AgentID connection = null;
		// for(AgentID neighbour : neighbours.keySet())
		// if(kb.doesAgentHaveData(neighbour, primaryGoal.getData()))
		// {
		// connection = neighbour;
		// break;
		// }
		// if(connection != null)
		// { // some neighbour has the data. get it.
		// intention.plan.add(new Action(primaryGoal.getData(), connection));
		// }
		// else
		// { // make new goal to inform others about this goal. (yes, it does
		// make sense)
		// // goals.add(Logix.makeGoalInformGoal(primaryGoal, id)); // spread
		// // done
		// }
		// intentions.add(intention);
		// log.li("new intention: ~", intention);
		// return;
		// }
		case INFORM:
		{
			Fact f = primaryGoal.relatedFact;

			if (f.getPersistence() < Logix.zeroPersistence)
				// TODO say log something?
				return;
			// if((f == null) || (f.getDataRecursive() == null))
			// // cases unsupported for now
			// return;
			final Specialty targetInterest = f.getSpecialty();

			SortedSet<AgentID> sortedN = new TreeSet<AgentID>(
					new Comparator<AgentID>()
					{
						@Override
						public int compare(AgentID o1, AgentID o2)
						{
							int dif = (int) (Logix.similarity(targetInterest,
									kb.calcAgentSpecialty(o2)) - Logix
									.similarity(targetInterest,
											kb.calcAgentSpecialty(o1)));
							if ((dif == 0) && (o1 != o2))
								return -1;
							return dif;
						}
					});
			List<AgentID> shuffled = new LinkedList<AgentID>(
					neighbours.keySet());
			Collections.shuffle(shuffled, KCAScenario.rand());
			sortedN.addAll(shuffled);
			Intention intention = new Intention(primaryGoal);
			for (AgentID a : neighbours.keySet())
			{
				if ((KCAScenario.rand().nextFloat() < f.getPressure())
						&& !kb.doesAgentKnowFact(a, f.recurse()))
					intention.plan.add(new Action(f, a));
			}

			intentions.add(intention);
			log.li("new intention: ~", intention);
			return;
		}
		}
	}

	protected void execute()
	{
		log.li("intentions [~]: ~", intentions.size(), intentions);
		// get the primary intention
		if (intentions.isEmpty() || intentions.getFirst().isWaiting())
			return;

		Intention intention = intentions.getFirst();
		if (intention.plan.isEmpty())
		{ // shouldn't happen though, but it does
			intention.waitStep();
			return;
		}

		Action action = intention.plan.poll();

		log.li("executing action for ~: ~", intention.goal, action);

		switch (action.type)
		{
		case FREE:
			if (usedCapacity > Logix.memoryThresh() * capacity)
				kb.reduce((usedCapacity) / (float) capacity
						- Logix.memoryThresh());
			// data.remove(action.relatedFact.getData());
			// usedCapacity -= action.relatedFact.getData().getSize();
			break;
		// case REQUEST:
		// sendMessage(action.targetAgent, new Message(id, Type.REQUEST, new
		// Fact(id, action.relatedData).toCollection()));
		// break;
		case INFORM:
			sendMessage(action.targetAgent, new Message(id, Type.INFORM,
					action.relatedFact.toCollection()));
			break;

		}

		if (intention.plan.isEmpty())
			intention.waitStep();
	}

	private void revisePressureLimits()
	{
		highPressure = Logix.highPressureRevise(agent_pressure, highPressure,
				lowPressure);
		lowPressure = Logix.lowPressureRevise(agent_pressure, highPressure,
				lowPressure);
	}

	public double gradeFactHistory(Specialty factSpec, int firstStep)
	{ // AO: returns at what point this specialty was most similar to the
		// agent's specialty, as a fraction of the agent's entire evolution
		double maxI = 0.0f;
		double maxSim = 0.0f;
		double sim;
		for (int i = firstStep; i <= currentSpecialtyIndex; i++)
		{
			sim = Logix.similarity(specialtyHistory[i], factSpec);
			if (sim > maxSim)
			{
				maxI = i;
				maxSim = sim;
			}
		}
		return ((double) (maxI - ((double) firstStep) + 1.0f))
				/ ((double) ((double) currentSpecialtyIndex
						- (double) firstStep + 1.0f));
	}

	public double calculateAgentBalance()
	{
		// FIXME not flexible Domain implementation
		Collection<Fact> facts = getFacts(false);
		double n = (double) facts.size();
		if (n == 0)
		{
			agentBalance = 0.0;
			agentUselessFacts = 0.0;
			return 0;
		}
		Specialty factSpec;
		double a1 = 0.0f, b1 = 0.0f, c1 = 0.0f, a2, b2, c2;
		double grade = 0.0f, grade2;
		double uselessFacts = 0;
		for (Fact fact : facts)
		{
			factSpec = fact.getSpecialty();

			grade2 = gradeFactHistory(fact.getSpecialty(), fact.firstStep);
			fact.setAgentFactBalance(Logix
					.similarity(agent_specialty, factSpec) * grade2);
			// if(Logix.similarity(agent_specialty, factSpec) * grade2 <
			// balanceMinimum)
			if (fact.getAgentFactBalance() < balanceMinimum)
				uselessFacts++;

			// grade += gradeFactHistory(fact.getSpecialty(), fact.firstStep);
			grade += grade2;
			a2 = factSpec.getValue(Domain.A);
			b2 = factSpec.getValue(Domain.B);
			c2 = factSpec.getValue(Domain.C);
			a1 += a2 / n;
			b1 += b2 / n;
			c1 += c2 / n;
		}
		grade /= n;
		// grade *= Logix.similarity(getSpecialty(), new Specialty(a1, b1, c1));
		grade *= Logix.similarity(agent_specialty, new Specialty(a1, b1, c1));
		agentBalance = grade; // AO: mean degree of usefulness
		agentUselessFacts = uselessFacts / n; // AO: facts with balance <
												// balanceMinimum
		return grade;
	}

	public double getAgentBalance()
	{
		return agentBalance;
	}

	public double getAgentUselessFacts()
	{
		return agentUselessFacts;
	}

	// public Collection<Data> getData() // for Drawing, snapshotting. etc
	// {
	// return data;
	// }

	public Collection<Fact> getFacts(boolean full)
	{
		return kb.getContent(full);
	}

	public Collection<Goal> getGoals()
	{
		return goals;
	}

	// return plans, only the scope
	public Collection<Intention> getPlans()
	{
		return intentions;
	}

	public Queue<Message> getInbox() // for Drawer
	{
		return inbox;
	}

	public Specialty getSpecialty()
	{
		return agent_specialty;
	}

	public float getPressure()
	{
		return agent_pressure;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void toggleSelected()
	{
		selected = !selected;
		if (selected)
			parent.addSelected(this);
		else
			parent.removeSelected(this);
		parent.doUpdate();
	}

	public Log getLog()
	{
		return log;
	}

	@Override
	public String toString()
	{
		return id.toString();
	}

	@Override
	public Measure getMeasure()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Measure> getAllMeasures()
	{
		// TODO Auto-generated method stub
		return null;
	}


}
