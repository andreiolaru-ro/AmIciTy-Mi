package graphics;

import java.awt.Color;

import KCAAgent.EnvironmentKCA;
import KCAAgent.KCAAgent;

public abstract class AbstractAvgGraphViewer extends AbstractGraphViewer
{
	@SuppressWarnings("hiding")
	protected AbstractAvgGraphViewer(EnvironmentKCA cm, Object data, Color color)
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
