package graphics;

import java.awt.Color;

import agent.AbstractAgent;

import logging.LogViewer;
import graphics.ViewerFactory.Type;
import graphics.ViewerFactory.WindowParameters;
import P2PAgent.EnvironmentP2P;
import P2PAgent.P2PAgent;
import P2PAgent.SimulationP2P;

public class ViewerFactoryP2P
{
	
	public static ControllableView<EnvironmentP2P>[] createViewers(EnvironmentP2P environment, WindowParameters params[])
	{
		ControllableView<EnvironmentP2P>[] viewers = new ControllableView[params.length];
		WindowParameters control = null;
		for(int i = 0; i < params.length && control == null; i++)
			if(params[i].getType() == Type.CONTROL)
				control = params[i];
		
		SimulationP2P p2p = (SimulationP2P)(control.getData());
		p2p.createMainWindow(control.getX(), control.getY(), control.getWidth(), control.getHeight());
		
		for(int i = 0; i < params.length; i++)
		{
			if(params[i].getType() != Type.CONTROL)
				viewers[i] = createViewer(environment, params[i]);
		}
		
		return viewers;
	}
	
	public static ControllableView<EnvironmentP2P> createViewer(EnvironmentP2P environment, WindowParameters params)
	{
		ControllableView<EnvironmentP2P> viewer = createViewerSub(environment, params.getType(), params.getData(), params.getSpecific());
		viewer.setLocation(params.getX(), params.getY());
		if(params.getWidth() != 0 && params.getHeight() != 0)
		{
//			System.out.println("setting dim: " + params.width + ", " + params.height);
			viewer.setSize(params.getWidth(), params.getHeight());
//			viewer.setSize(100, 100);
		}
		viewer.show();
		return viewer;
	}
	
	private static ControllableView<EnvironmentP2P> createViewerSub(EnvironmentP2P environment, Type type, Object data, Object specific)
	{
		switch(type)
		{
			case AGENT_SELECTION_GRID:
				return new AbstractGridViewerWhitoutLocation<EnvironmentP2P>(environment) {
					@Override
					public Color getColor(AbstractAgent cell)
					{
						return Color.GREEN;
					}
				}.setTitle("Selection Grid");
			case ITEM_GRAF:
				return new AbstractGraphViewer<EnvironmentP2P,P2PAgent>(environment, null, Color.red){
					@Override
					public double getCellValue(P2PAgent cell)
					{
						return P2PAgent.getNumberItem().doubleValue();
					}
					
					
					@Override
					public double stringScale()
					{
						return 1;
					}


					@Override
					protected double calculateValue()
					{
						// TODO Auto-generated method stub
						return P2PAgent.getNumberItem().doubleValue();
					}
				}.setTitle("Item owned");
			case ITEM_LOCATION_GRAF:
				return new AbstractGraphViewer<EnvironmentP2P,P2PAgent>(environment, null, Color.red){
					@Override
					public double getCellValue(P2PAgent cell)
					{
						return P2PAgent.getNumberItem().doubleValue();
					}
					
					
					@Override
					public double stringScale()
					{
						return 1;
					}


					@Override
					protected double calculateValue()
					{
						// TODO Auto-generated method stub
						return P2PAgent.getNumberItemLocation().doubleValue();
					}
				}.setTitle("Item location");
			case ITEM_WANTED_GRAF:
				return new AbstractGraphViewer<EnvironmentP2P,P2PAgent>(environment, null, Color.red){
					@Override
					public double getCellValue(P2PAgent cell)
					{
						return P2PAgent.getNumberItem().doubleValue();
					}
					
					
					@Override
					public double stringScale()
					{
						return 1;
					}


					@Override
					protected double calculateValue()
					{
						// TODO Auto-generated method stub
						return P2PAgent.getNumberItemWanted().doubleValue();
					}
				}.setTitle("Item wanted");
				
			case LOG_VIEWER:
				return new LogViewer<EnvironmentP2P>(environment).setTitle("Event Log");
			case CONTROL:
				return null;
			default:
				return null;
		}
		
	}

}
