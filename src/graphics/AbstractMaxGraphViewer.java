package graphics;

import java.awt.Color;

import agent.AbstractAgent;
import base.Environment;


public abstract class AbstractMaxGraphViewer<ENVIRONMENT extends Environment<?,? extends AGENT>, AGENT extends AbstractAgent> extends AbstractGraphViewer<ENVIRONMENT, AGENT>
{
	protected AbstractMaxGraphViewer(ENVIRONMENT cm, Object data, Color color)
	{
		super(cm, data, color);
	}

	@Override
	protected double calculateValue()
	{
		double value = Double.NEGATIVE_INFINITY;
		for (AGENT cell : cm.getAgents()) {
			if(value < getCellValue(cell)) {
				value = getCellValue(cell);			
			}
		}
		return value;
	}
}
