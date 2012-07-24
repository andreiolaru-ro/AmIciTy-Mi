package KCAAgent;

import java.util.HashMap;
import java.util.Map;

import agent.AbstractMeasure;
import agent.Measure;
import agent.MeasureName;
import agent.AbstractMeasure.NumericMeasure;

/**
 * 
 * Class which define the location of an agent on the view
 *
 */
public class Location implements Measure<Map<String, NumericMeasure>>{
	private double x;
	private double y;
	private Map<String, NumericMeasure> coord; //map which contains all the informations about the location of an agent
	
	@SuppressWarnings("hiding")
	public Location(double x, double y) {
		this.x = x;
		this.y = y;
		coord= new HashMap<String,NumericMeasure>();
		coord.put("x", new NumericMeasure(x,MeasureName.LOCATION));
		coord.put("y", new NumericMeasure(y,MeasureName.LOCATION));
	}
	
	public double getDistance(Location loc) {
		double dx = loc.x - x;
		double dy = loc.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	@Override
	public String toString()
	{
		return x + "," + y;
	}

	@Override
	public Map<String,NumericMeasure> getValue()
	{
		// TODO Auto-generated method stub
		return coord;
	}

	@Override
	public void setValue(Map<String, NumericMeasure> measure) throws Exception
	{
		// TODO Auto-generated method stub
		this.coord=measure;
		this.x=coord.get("x").getValue().doubleValue();
		this.y=coord.get("y").getValue().doubleValue();
	}

	@Override
	public MeasureName getName()
	{
		// TODO Auto-generated method stub
		return MeasureName.LOCATION;
	}
}
