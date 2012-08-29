package KCAAgent;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scenario.AbstractScenario;
import agent.AbstractMeasure.NumericMeasure;
import agent.AgentID;
import agent.MeasureName;




public class Logix
{
	public static class FactComparator implements Comparator<Fact>
	{
		Specialty	agentSpec	= new Specialty();
		
		@SuppressWarnings("hiding")
		public FactComparator(Specialty agentSpec)
		{
			this.agentSpec = agentSpec;
		}
		
		/**
		 * for (F1, F2), returns -1 if F1 is more important
		 */
		@Override
		public int compare(Fact f1, Fact f2)
		{
			// int ret = 0;
			if((f1.getPersistence() < zeroPersistence) && (f2.getPersistence() >= zeroPersistence))
				return 1;
			else if((f2.getPersistence() < zeroPersistence) && (f1.getPersistence() >= zeroPersistence))
				return -1;
			
			float d1 = 1 - Math.min(f1.getDepth() / 3f, 1);
			float d2 = 1 - Math.min(f2.getDepth() / 3f, 1);
			
			float i1 = Logix.similarity(f1.getSpecialty(), agentSpec);
			float i2 = Logix.similarity(f2.getSpecialty(), agentSpec);
			
			float p1 = f1.getPressure();
			float p2 = f2.getPressure();
			
			float mc1 = i1 + p1 + d1;
			float mc2 = i2 + p2 + d2;
			
			if(mc1 != mc2)
				return (mc1 > mc2) ? -1 : 1;
			
			return 0;
			// System.out.println(ret + " : " + f1 + " : " + f2);
			// return ret;
		}
	}
	
	public enum Domain {
		A, B, C;
	}
	
	public static int	agentCapacity					= 12;
	
	// behaviour
	static int			minimalBeliefProcessing			= 2;
	static float			zeroPersistence					= 0.01f;
	
	// measures
	static float		neighbourDataFactPersistence	= 0.8f;
	static float		newFactGoalImportance			= 1.0f;
	static float		specUpdateRatio					= 0.01f;

	static float		primaryPressureFade				= 0.01f;
	static float		secondaryPressureFade			= 0.1f;

	static float		secondaryPersistenceFade		= 0.01f;
	
	// fading
	static float			goalFadeRate					= 0.2f;
	static float			goalMemoryFade					= 0.1f;
	static float			pressureFadingRate				= 0.01f;
	static float			persistenceFadingRate			= 0.01f;
	static float			memoryThresh					= 0.75f;
	static float			pressure2persistenceFade		= 0.5f;
	static float			highLowPressureRevise			= 0.2f;
	
	// planning
	static Integer			nWaits2replan					= new Integer(5);
	static float			informFraction					= 0.1f;
	
	static int minimalBeliefProcessing()
	{
		return minimalBeliefProcessing;
	}
	
	/**
	 * The output is calculated as a function of the measure of pressure.
	 * 
	 * @return the fraction of [recommended] perceptions to process
	 */
	static double availableBeliefProcessing(double agentPressure, double lowPressure, double highPressure)
	{
		// revise 0% (or, anyway, some default small value) when above high limit
		if(agentPressure > highPressure)
			return 0.0;
		
		// revise 100% when under low limit
		if(agentPressure < lowPressure)
			return 100.0;
		
		// use sigmoid function between limits -> outputs sub-unit number
		double t = (agentPressure - lowPressure) / (highPressure - lowPressure);
		double result = 1 / (1 + Math.exp(10 * (t - 0.5)));
		
		return result;
	}
	
	// /////////// specialty & interest
	
	static Map<Domain, NumericMeasure> normSpecialty(Map<Domain, NumericMeasure> specMap)
	{
		double norm = 0.0;
		for(Entry<Domain, NumericMeasure> component : specMap.entrySet())
		{
			double val = component.getValue().getValue().doubleValue();
			if(val < 0)
				component.setValue(new NumericMeasure(0.0,MeasureName.SPECIALTY));
			if(val > 1)
				component.setValue(new NumericMeasure(1.0,MeasureName.SPECIALTY));
			norm += val * val;
		}
		if(norm > 1)
			for(Entry<Domain, NumericMeasure> component : specMap.entrySet())
				component.setValue(new NumericMeasure(component.getValue().getValue().doubleValue() / norm,MeasureName.SPECIALTY));
		
		return specMap;
	}
	
	public static float similarity(Specialty s1, Specialty s2)
	{
		double sum = 0, norm1 = 0, norm2 = 0, cos, dotprod = 0, sin;
		for(Domain dom : Domain.values())
		{
			double d = s1.getValue(dom)- s2.getValue(dom);
			sum += d * d;
			norm1 += s1.getValue(dom) * s1.getValue(dom);
			norm2 += s2.getValue(dom) * s2.getValue(dom);
			dotprod += s1.getValue(dom) * s2.getValue(dom);
		}
		if(norm1 <= 0 || norm2 <= 0)
			cos = 0;
		else
			cos = dotprod / (Math.sqrt(norm1) * Math.sqrt(norm2));
		sin = Math.sqrt(1 - cos * cos);
		return (float)(1 - Math.sqrt(sum / Domain.values().length) * sin);
	}
	
	static Specialty merge(Specialty s1, Specialty s2, float data2factsMergeRatio2)
	{
		Map<Domain, NumericMeasure> result = new HashMap<Domain, NumericMeasure>();
		for(Domain dom : Domain.values())
		{
			double v1 = s1.getValue(dom);
			double v2 = s2.getValue(dom);
			result.put(dom, new NumericMeasure(Math.sqrt((1 - data2factsMergeRatio2) * v1 * v1 + data2factsMergeRatio2 * v2 * v2),MeasureName.SPECIALTY));
		}
		return new Specialty(result);
	}
	
	static Specialty updateSpecialty(Specialty toUpdate, Collection<Fact> ownedFacts)
	{
		// calculate the mean specialty of facts that we have
		Specialty factSpec;
		Map<Domain, NumericMeasure> factSpecMap = new HashMap<Domain, NumericMeasure>();
		for(Domain dom : Domain.values())
		{
			double sum = 0.0;
			int n = 0;
			for(Fact f : ownedFacts)
			{
				sum += Math.pow(f.getSpecialty().getValue(dom), 2);
				n++;
			}
			if(n > 0)
				factSpecMap.put(dom, new NumericMeasure(sum / n,MeasureName.SPECIALTY));
		}
		factSpec = new Specialty(factSpecMap);
		
		return Logix.merge(toUpdate, factSpec, specUpdateRatio);
		// return Logix.merge(toUpdate, Logix.merge(dataSpec, factSpec, data2factsMergeRatio), specUpdateRatio);
		// return new Specialty();
	}
	
	// ////////////// fact setting
	
	// static float distanceToFade(Integer distance)
	// {
	// return (float)(Math.min(1.0, Math.pow(interestFadePerDistanceUnit, distance)));
	// }
	
	// static float setFactInterest(Fact f, Specialty agentSpec)
	// {
	// float i = similarity(f.getSpecialty(), agentSpec);
	// f.setInterest(i);
	// return i;
	// }
	//	
	// static Fact setNewDataFact(Specialty agentSpec, Fact f)
	// {
	// f.setPressure(0.0f); // this fact is not pressing (for me or for others);what was pressing was the fact telling somebody else had the data
	// setFactInterest(f, agentSpec);
	// f.setPersistence(1.0f); // as long as we have the data, the fact must live. when data will be discarded the fact willbe discarded as well
	// return f;
	// }
	//	
	// static Fact setNewInjectedDataFact(Specialty agentSpec, Fact f, Fact contextInfo)
	// {
	// f.setPressure(contextInfo.getPressure());
	// setFactInterest(f, agentSpec);
	// f.setPersistence(1.0f); // it is our data and, as far as it is not deleted, the fact must stay
	// return f;
	// }
	//	
	// protected static Fact setDistantDataFact(Specialty agentSpec, Fact f)
	// {
	// // should only be called for data facts !
	// // should only be called for inferred facts, not for received facts (as in INFORMs).
	// // no pressure involved. actually, the fact has just been built. (this should only result from a sendData / receiveData)
	// // i.e. after a receiveData, infer the sending agent has the data; after a sendData, infer the receiving agent [now] has the data
	// f.setPressure(0.0f); // this fact is not pressing (for me or for others)
	// setFactInterest(f, agentSpec);
	// f.setPersistence(neighbourDataFactPersistence); // the fact is inferred about a neighbour
	// return f;
	// }
	//	
	// static Fact setDistantDataFact(Specialty agentSpec, Fact f, Fact contextInfo)
	// {
	// if(contextInfo == null)
	// return setDistantDataFact(agentSpec, f);
	// // the original fact (last parameter) is built elsewhere; it has context information attached.
	// f.setPressure(contextInfo.getPressure()); // keep pressure unchanged, it will fade afterwards, maybe
	// setFactInterest(f, agentSpec); // f already contains the specialty information
	// f.setPersistence(contextInfo.getPersistence() * persistanceFade); // it may have to be persistent but that also depends of our interest.
	// return f;
	// }
	//	
	// static Fact setDistantGoalFact(Specialty agentSpec, Fact f, int distance)
	// {
	// return f;
	// }
	
	// //////////// new fact setting functions
	
	static Fact setNewFact(Fact newFact, Fact relatedFact) throws Exception
	{
		newFact.setPressure((relatedFact.getPressure() >= 1) ? relatedFact.getPressure() : (relatedFact.getPressure() * (1.0f - primaryPressureFade)));
		newFact.setSpecialty(relatedFact.getSpecialty());
		newFact.setPersistence(relatedFact.getPersistence());
		return newFact;
	}
	
	static Fact setDistantFactFact(Fact newFact, Fact relatedFact) throws Exception
	{
		newFact.setPressure(relatedFact.getPressure() * (1.0f - secondaryPressureFade));
		newFact.setSpecialty(relatedFact.getSpecialty());
		newFact.setPersistence(relatedFact.getPersistence() * (1.0f - secondaryPressureFade));
		return newFact;
	}
	
	// /////// goal setting
	
	static Goal makeGoal(Fact f, Specialty s)
	{
		Goal ret = null;
		
		if(f.getPersistence() < zeroPersistence)
			return null;

		float d = 1 - Math.min(f.getDepth() / 3f, 1);
		float i = Logix.similarity(f.getSpecialty(), s);
		float p = f.getPressure();
		
		float mc = i + p + d;
		
//		if(f.getDepth() > 0)
//			return null;
		
		ret = new Goal(f);
		ret.setImportance(mc / 3);
//		ret.setImportance(Math.max(p, i));
		
		return ret;
	}
	
	static Goal makeNewDataInformGoal(Fact f)
	{
		return new Goal(f).setImportance(f.getPressure());
	}
	
	static Goal makeInformGoal(Fact f)
	{ // the fact should also contain some context information
		return new Goal(f).setImportance(f.getPressure());
	}
	
	static Goal makeGoalInformGoal(Goal g, AgentID id, int step) throws Exception
	{
		Fact holder = new Fact(id, g, step);
		holder.setPressure(g.getImportance());
		// holder.setInterest(g.getImportance());
		holder.setPersistence(g.getImportance());
		return new Goal(holder).setImportance(g.getImportance());
	}
	
	@SuppressWarnings("unused")
	static Goal makeNormalGetGoal(Fact f)
	{ // the fact should also contain some context information
	// return new Goal(f.getDataRecursive()).setImportance(Math.max(f.getInterest(), f.getPressure()));
		return null;
	}
	
	@SuppressWarnings("unused")
	static Goal makeUnExRqGoal(Fact f)
	{
		// return new Goal(f.getData()).setImportance(f.getPressure());
		return null;
	}
	
	static Goal setFreeGoal(int used, int available, Goal g)
	{ // goal keep capacity available
		// for capacity between MT and 100% of total, should range between 0.0 and 1.0
		float importance = Math.min((used / (float)available - memoryThresh) / (1.0f - memoryThresh), 1.0f);
		g.setImportance(importance);
		return g;
	}
	
	static void promoteGoal(Goal goal, Intention.IntentionList intentions)
	{
		Intention i = intentions.containsGoal(goal);
		if(i == null)
			// ERROR
			return;
		intentions.remove(i);
		intentions.addFirst(i);
	}
	
	// other
	
	static boolean goodToForget(Fact f)
	{
		if(f.getPersistence() < Logix.zeroPersistence && AbstractScenario.rand().nextDouble() < 0.1)
			return true;
		return false;
	}
	
	static boolean goodToForgetPressed(Fact f)
	{
		return (f.getPersistence() < 1.0);
	}
	
	@SuppressWarnings("unused")
	static float highPressureRevise(float p, float high, float low)
	{
		return high + highLowPressureRevise * (p - high);
	}
	
	@SuppressWarnings("unused")
	static float lowPressureRevise(float p, float high, float low)
	{
		return low + highLowPressureRevise * (p - low);
	}
	
	static float getInformFraction()
	{
		return informFraction;
	}
	
	static float goalFadeRate()
	{
		return goalFadeRate;
	}
	
	static float goalMemoryFade()
	{
		return goalMemoryFade;
	}
	
	static float memoryThresh()
	{
		return memoryThresh;
	}
	
	static float pressureFade()
	{
		return pressureFadingRate;
	}
	
	static float persistenceFade()
	{
		return persistenceFadingRate;
	}
	
	// static float persistenceFade(float pressure)
	// {
	// return pressure * pressure2persistenceFade;
	// }
}
