package agent;


/**
 * 
 * this interface represent a measure for an agent
 *
 * @param <T>
 */
public interface Measure<T> 
{
	public T getValue() ;
	public void setValue(T measure) throws Exception;
	public MeasureName getName() ;
}
