package graphics;

import java.awt.Color;

import KCAAgent.EnvironmentKCA;
import KCAAgent.KCAAgent;

public abstract class AbstractMaxGraphViewer extends AbstractGraphViewer<EnvironmentKCA>
{
	protected AbstractMaxGraphViewer(EnvironmentKCA cm, Object data, Color color)
	{
		super(cm, data, color);
	}

	@Override
	protected double calculateValue()
	{
		double value = Double.NEGATIVE_INFINITY;
		for (KCAAgent cell : cm.getAgents()) {
			if(value < getCellValue(cell)) {
				value = getCellValue(cell);			
			}
		}
		return value;
	}
}
