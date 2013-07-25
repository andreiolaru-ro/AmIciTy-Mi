/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru. See the AUTHORS file for more information.
 * 
 * This file is part of AmIciTy-Mi.
 * 
 * AmIciTy-Mi is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * AmIciTy-Mi is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with AmIciTy-Mi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package KCAAgent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import logging.Log;
import agent.AgentID;
import base.QuickSet;
import base.QuickSet.QuickForgetConfirmer;

public class KnowledgeBase implements QuickForgetConfirmer<Fact> {
	private Log					log;

	private AgentID				holder;
	private Specialty			specialty	= new Specialty();
	@SuppressWarnings("unused")
	private float				pressure	= 0.0f;

	private Map<Fact, Integer>	base		= new HashMap<Fact, Integer>();				// this
																							// vector
																							// contains
																							// *all*
																							// facts.
																							// even
																							// facts
																							// that
																							// are
																							// included
																							// in
																							// other
																							// facts
	// the value is for the number of links to this fact (reference count)
	/* private */QuickSet<Fact>	owned		= new QuickSet<Fact>(
													new Logix.FactComparator(specialty));	// this
																							// contains
																							// all
																							// facts,
																							// as
																							// rooted
																							// in
																							// this
																							// agent

	public KnowledgeBase(KCAAgent agent, @SuppressWarnings("hiding") Specialty specialty) {
		this.holder = agent.getId();
		this.specialty.set(specialty);

		log = agent.getLog();
	}

	public Collection<Fact> getContent(boolean full) {
		if (full)
			return base.keySet();
		return owned;
	}

	private void insertSub(Fact f) {
		if (base.containsKey(f)) {
			log.lw("fact already contained");
			return;
		}
		log.lf("inserting base fact ~", f);
		base.put(f, new Integer(1));
		// if there is a sub fact, relink it or add it
		if (f.getFact() != null && checkRelink(f))
			insertSub(f.getFact());
	}

	public void insert(Fact f) throws Exception {
		// check if Fact doesn't refer circularly to this agent // more than
		// once
		boolean circular = false;
		Float circularPressure = null;
		Fact fs = f.getFact();
		while (fs != null) {
			if (fs.getAgent() == holder) { // circular
											// for now, it will be ignored
											// log.lw("circular fact. not inserted");
											// return;

				if (circular) {
					log.lw("2+ circular fact. not inserted");
					return;
				}
				circular = true;
				if (base.containsKey(fs))
					circularPressure = new Float(contains(fs).getPressure());
				else
					log.le("circular fact not found" + fs);
			}
			fs = fs.getFact();
		}

		// check if fact is actually new
		if (base.containsKey(f)) {
			log.lw("fact already contained");
			return;
		}

		if (circular && (circularPressure != null)) {
			f.setPressure(Math.min(f.getPressure(), circularPressure.floatValue()));
			log.li("pressure reduced (due to circularity) to ~");
		}

		log.li("inserting new fact ~", f);

		if (owned.add(f))
			insertSub(f);
		else
			log.le("fact insertion failed: ~", f);
	}

	public Fact contains(Fact f) {
		for (Fact fs : base.keySet())
			if (fs.equals(f))
				return fs;
		return null;
	}

	// returns true if the sub fact is new and should be added
	protected boolean checkRelink(Fact parent) {
		Fact oldFact = contains(parent.getFact());
		if (oldFact != null) {
			parent.resetFact(oldFact);
			base.put(oldFact, new Integer(base.get(oldFact).intValue() + 1));
			log.lf("sub fact reset");
			return false;
		}
		return true;
	}

	public boolean doesAgentKnowFact(AgentID a, Fact f) {
		for (Fact fs : base.keySet())
			if ((fs.getAgent() == a) && ((fs == f) || (fs.recurse()) == f))
				return true;
		return false;
	}

	// public boolean doesAgentHaveData(AgentID a, Data d)
	// {
	// for(Fact fs : base.keySet())
	// if((fs.getAgent() == a) && (fs.getData() == d))
	// return true;
	// return false;
	// }

	// public Fact leastinterestingDataHeld()
	// {
	// Fact ret = null;
	// for(Fact f : owned)
	// if(f.getData() != null)
	// ret = f;
	// return ret;
	// }

	public float totalPressure() {
		// quadratic mean instead of arithmetic mean between max value
		// and arithmetic mean of all values
		float sum = 0.0f;
		int n = base.size();
		for (Fact f : base.keySet()) {
			sum += f.getPressure() * f.getPressure();
		}
		if (n > 0) {
			return (pressure = (float) Math.sqrt(sum / n));
		}

		return (pressure = 0.0f);

	}

	public Specialty reviseSpecialty() {
		specialty.set(Logix.updateSpecialty(specialty, owned));

		owned.updateAll();

		return specialty;
	}

	public Specialty calcAgentSpecialty(AgentID a) {
		boolean first = true;
		Specialty spec = new Specialty();
		float d = 0.0f;

		for (Fact f : base.keySet())
			if ((f.getAgent() == a)) {
				Specialty spec2 = f.getSpecialty();
				float d2 = 0;
				if (first) {
					spec = Logix.merge(spec, spec2, 1.0f);
					d = d2;
					first = false;
				} else {
					if (d + d2 > 0) {
						spec = Logix.merge(spec, spec2, d / (d + d2));
						d = d * d2 / (d + d2);
					}
				}
			}
		return spec;
	}

	public void fade(float pressureFade, float persistenceFade) throws Exception {
		int nremovals = 0;
		for (Fact f : base.keySet()) {
			f.fadePressure(pressureFade);
			f.fadePersistence(persistenceFade);
		}
		for (Iterator<Fact> it = owned.iterator(); it.hasNext();)
			if (Logix.goodToForget(it.next())) {
				it.remove();
				nremovals++;
			}
		if (nremovals > 0)
			reBase();
	}

	public void reduce(float existenceFade) {
		log.li("reducing ~ of the facts", new Float(existenceFade));
		owned.forgetTail(Math.max((int) (existenceFade * owned.size()), 1), this);
		// no need to refresh - order will not change
		reBase();
	}

	@Override
	public boolean confirmForget(Fact fact) {
		// return (fact.getAgent() != holder) || (fact.getData() == null);
		// return (Math.random() > fact.getInterest()) && (Math.random() >
		// fact.getPersistence() * Logix.persistenceFade(pressure));
		// return (Math.random() > fact.getInterest()) && (Math.random() >
		// fact.getPersistence());
		if (Logix.goodToForgetPressed(fact)) {
			log.lf("fact confirmed for forgetting ~", fact);
			return true;
		}
		return false;
	}

	public void remove(Fact f) // only for owned facts
	{
		owned.remove(f);
		reBase();
	}

	protected void reBase() {
		// re-build base. no more need for re-link checks, owned is coherent.
		Map<Fact, Integer> newBase = new HashMap<Fact, Integer>();
		for (Fact f : owned) {
			Fact fr = f; // this will recurse
			while (fr != null) {
				Integer oldCount = newBase.get(fr);
				if (oldCount == null)
					oldCount = new Integer(0);
				newBase.put(fr, new Integer(oldCount.intValue() + 1));
				fr = fr.getFact();
			}
		}
		base = newBase;
	}

	public int size() {
		return base.size();
	}

	public StringBuffer printFacts() {
		StringBuffer ret = new StringBuffer();
		ret.append(owned.size());
		ret.append(" root / ");
		ret.append(base.size());
		ret.append(" total facts:");
		for (Fact f : owned)
			if (f.getAgent() == holder)
				ret.append("\n\t").append(f.toString(specialty));

		ret.append("\n agents known:");
		Map<AgentID, Specialty> map = new HashMap<AgentID, Specialty>();
		for (Fact f : base.keySet()) {
			AgentID a = f.getAgent();
			if (!map.containsKey(a)) {
				Specialty spec = calcAgentSpecialty(a);
				map.put(a, spec);
				ret.append("\n\t").append(a).append(": ").append(spec);
			}
		}

		// ret.append("\n\n");
		// for(Fact f : base.keySet())
		// ret.append("\n").append(f);

		return ret;
	}

}
