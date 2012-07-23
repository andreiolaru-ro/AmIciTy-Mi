package graphics;

import java.awt.Color;

import KCAAgent.KCAAgent;
import base.Environment;

public abstract class AbstractMaxGraphViewer extends AbstractGraphViewer
{
	@SuppressWarnings("hiding")
	protected AbstractMaxGraphViewer(Environment cm, Object data, Color color)
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
