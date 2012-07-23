package graphics;

import java.awt.Color;

import KCAAgent.KCAAgent;
import base.Environment;

public abstract class AbstractAvgGraphViewer extends AbstractGraphViewer
{
	@SuppressWarnings("hiding")
	protected AbstractAvgGraphViewer(Environment cm, Object data, Color color)
	{
		super(cm, data, color);
	}

	@Override
	protected double calculateValue()
	{
		double value = 0;
		for (KCAAgent cell : cm.getAgents()) {
			value += getCellValue(cell);			
		}
		return value / cm.getAgents().size();
	}
}
