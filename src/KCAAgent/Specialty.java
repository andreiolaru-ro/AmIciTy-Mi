package KCAAgent;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import KCAAgent.Logix.Domain;



public class Specialty
{
	private Map<Domain, Double>	specMap	= new HashMap<Domain, Double>();
	
	public Specialty()
	{
		this(0, 0, 0);
	}
	
	public Specialty(double a, double b, double c)
	{
		specMap.put(Domain.A, new Double(a));
		specMap.put(Domain.B, new Double(b));
		specMap.put(Domain.C, new Double(c));
		Logix.normSpecialty(specMap);
	}
	
	public Specialty(Map<Domain, Double> map)
	{
		specMap = Logix.normSpecialty(map);
	}
	
	@Override
	public String toString()
	{
		String ret = null;
		for(Domain dom : Domain.values())
			if(specMap.containsKey(dom) && (specMap.get(dom).doubleValue() > 0))
				ret = ((ret == null) ? "" : (ret + " ")) + dom + ":" + (int)(100 * specMap.get(dom).doubleValue());
		return ret;
	}
	
	public double getValue(Domain dom)
	{
		if(specMap.containsKey(dom))
		{
			return specMap.get(dom).doubleValue();
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
			this.specMap.put(dom, new Double(spec.getValue(dom)));
		return this;
	}
}
