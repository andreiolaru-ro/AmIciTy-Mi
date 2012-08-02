package agent;

import java.util.Map;

/**
 * 
 * interface to implement some specific methods to have a generic agent
 *
 * @param <T>
 */

public interface Measurable<T>
{
	/**
	 * get one specific measure of an agent
	 * @param measure the name of the measure that we want to get
	 * @return the specific measure
	 */
	Measure<T> getMeasure(MeasureName measure);
	
	/**
	 * get all measures of an agent
	 * @return a map which contains all the measures of the agent
	 */
	Map<MeasureName, Measure<T>> getAllMeasures();
}
