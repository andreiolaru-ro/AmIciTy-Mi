package graphics;

import java.awt.Color;

import KCAAgent.EnvironmentKCA;
import KCAAgent.KCAAgent;

public abstract class AbstractAggregGraphViewer extends AbstractGraphViewer<EnvironmentKCA, KCAAgent>
{
	double	sum;
	double	avg;
	double	max;
	double	min;


	protected AbstractAggregGraphViewer(EnvironmentKCA cm, Object data)
	{
		this(cm, data, new GraphParam(null, null, null));
	}
	
	
	protected AbstractAggregGraphViewer(EnvironmentKCA cm, Object data, Color color)
	{
		this(cm, data, new GraphParam(color, null, null));
	}
	
	
	protected AbstractAggregGraphViewer(EnvironmentKCA cm, Object data, GraphParam param)
	{
		super(cm, data, param);
	}
	
	@Override
	protected double calculateValue()
	{
		double localValue = 0;
		boolean first = true;
		sum = 0;
		for(KCAAgent cell : cm.getAgents())
		{
			localValue = getCellValue(cell);
			if(first)
			{
				first = false;
				min = localValue;
				max = localValue;
			}
			else
			{
				if(localValue > max)
					max = localValue;
				if(localValue < min)
					min = localValue;
			}
			sum += localValue;
		}
		avg = sum / (cm.getAgents().size());
		return avg;
	}
	
	@Override
	protected double getValue(int trace)
	{
		if(trace == 1)
			return max;
		if(trace == 2)
			return min;
		return avg;
	}
	
	@Override
	
	protected String makeString(double val, double max)
	{
		return sum + " : " + super.makeString(val, max);
	}
}
