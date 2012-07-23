package agent;

import java.util.List;


public abstract class Measure<T>
{
	T value;
	String name ;
	
	public T getMeasure()
	{
		return this.value;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}





	public static class StringMeasure extends Measure<String>{}
	public static class NumericMeasure extends Measure<Double>{}
	
	public static class VectorMeasure extends Measure<List<Measure<?>>>{
		public Measure<?> getMeasure(String name)
		{
			for(Measure measure : this.value){
				if(measure.getName().equals(name))
					return measure ;
			}
			return null ;
		}
	}
	
}
