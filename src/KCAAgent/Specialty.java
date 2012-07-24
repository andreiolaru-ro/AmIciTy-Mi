package KCAAgent;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import agent.AbstractMeasure.NumericMeasure;
import agent.Measure;
import agent.MeasureName;

import KCAAgent.Logix.Domain;



public class Specialty implements Measure<Map<Domain,NumericMeasure>>//NumericMeasure now
{
	private Map<Domain, NumericMeasure>	specMap	= new HashMap<Domain, NumericMeasure>();
	private MeasureName name;
	
	public Specialty()
	{
		this(0, 0, 0);
		name=MeasureName.SPECIALTY;
	}
	
	public Specialty(double a, double b, double c)
	{
		specMap.put(Domain.A, new NumericMeasure(a,MeasureName.SPECIALTY));
		specMap.put(Domain.B, new NumericMeasure(b,MeasureName.SPECIALTY));
		specMap.put(Domain.C, new NumericMeasure(c,MeasureName.SPECIALTY));
		Logix.normSpecialty(specMap);
	}
	
	public Specialty(Map<Domain, NumericMeasure> map)
	{
		specMap = Logix.normSpecialty(map);
	}
	
	@Override
	public String toString()
	{
		String ret = null;
		for(Domain dom : Domain.values())
			if(specMap.containsKey(dom) && (specMap.get(dom).getValue().doubleValue() > 0))
				ret = ((ret == null) ? "" : (ret + " ")) + dom + ":" + (int)(100 * specMap.get(dom).getValue().doubleValue());
		return ret;
	}
	
	public double getValue(Domain dom)
	{
		if(specMap.containsKey(dom))
		{
			return specMap.get(dom).getValue().doubleValue();
		}
		return 0.0;
	}
	
	// the color of the data, assumes there are exactly 3 domains
	public Color getColor()
	{
		double r = getValue(Domain.A);
		double g = getValue(Domain.B);
		double b = getValue(Domain.C);
		return new Color((float)r, (float)g, (float)b);
	}
	
	public Specialty set(Specialty spec)
	{
		for(Domain dom: Domain.values())
			this.specMap.put(dom, new NumericMeasure(spec.getValue(dom),MeasureName.SPECIALTY));
		return this;
	}

	@Override
	public Map<Domain,NumericMeasure> getValue()
	{
		// TODO Auto-generated method stub
		return this.specMap;
	}

	@Override
	public MeasureName getName()
	{
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public void setValue(Map<Domain, NumericMeasure> value)
	{
		// TODO Auto-generated method stub
			this.specMap=value;
	}
}
