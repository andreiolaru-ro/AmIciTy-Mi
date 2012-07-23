package graphics;

import java.awt.Color;

import KCAAgent.KCAAgent;
import base.Environment;

public abstract class AbstractAggregGraphViewer extends AbstractGraphViewer
{
	double	sum;
	double	avg;
	double	max;
	double	min;
	
	@SuppressWarnings("hiding")
	protected AbstractAggregGraphViewer(Environment cm, Object data)
	{
		this(cm, data, new GraphParam(null, null, null));
	}
	
	@SuppressWarnings("hiding")
	protected AbstractAggregGraphViewer(Environment cm, Object data, Color color)
	{
		this(cm, data, new GraphParam(color, null, null));
	}
	
	@SuppressWarnings("hiding")
	protected AbstractAggregGraphViewer(Environment cm, Object data, GraphParam param)
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
	@SuppressWarnings("hiding")
	protected String makeString(double val, double max)
	{
		return sum + " : " + super.makeString(val, max);
	}
}
