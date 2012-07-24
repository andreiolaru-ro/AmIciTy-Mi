package agent;


/**
 * 
 * abstract class which implements the interface Measure and which permits to create the different type of measure like FloatMeasure 
 * for example
 *
 * @param <T>
 */
public abstract class AbstractMeasure<T> implements Measure<T>
{
	protected T	value;
	protected MeasureName name;
	
	
	public AbstractMeasure(T value, MeasureName name)
	{
		this.value=value;
		this.name=name;
	}
	
	@Override
	public T getValue()
	{
		// TODO Auto-generated method stub
		return  this.value;
	}

	@SuppressWarnings({ "hiding", "unchecked" })
	@Override
	public void setValue(Object value) throws Exception
	{
	
			try{
				if(value.getClass()==this.value.getClass())
					this.value=(T) value;
				else
					throw new Exception("bad class");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	
	}

	@Override
	public MeasureName getName()
	{
		// TODO Auto-generated method stub
		return this.name;
	}

	public static class NumericMeasure extends AbstractMeasure<Double>
	{

		public NumericMeasure(Double value, MeasureName name)
		{
			super(value, name);
			// TODO Auto-generated constructor stub
		}
		
		public NumericMeasure(double value, MeasureName name)
		{
			super(new Double(value), name);
		}
	}

	public static class FloatMeasure extends AbstractMeasure<Float>
	{

		public FloatMeasure(Float value, MeasureName name)
		{
			super(value, name);
			// TODO Auto-generated constructor stub
		}
		
		public FloatMeasure(float value, MeasureName name)
		{
			super(new Float(value), name);
		}
		
	}

	/*public static class VectorMeasure extends AbstractMeasure<List<Measure>>
	{
		public VectorMeasure(List<Measure> value,NameMeasure name)
		{
			super(value, name);
			// TODO Auto-generated constructor stub
		}

		public Measure getValue(String name)
		{
			for (Measure measure : this.value)
			{
				if (measure.getName().equals(name))
					return measure;
			}
			return null;
		}
		
		public void addMeasure(Measure measure)
		{
			this.value.add(measure);
		}
		
	}*/

}
