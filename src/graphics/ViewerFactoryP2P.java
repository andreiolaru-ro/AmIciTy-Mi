package graphics;

import logging.LogViewer;
import graphics.ViewerFactory.Type;
import graphics.ViewerFactory.WindowParameters;
import KCAAgent.EnvironmentKCA;
import P2PAgent.EnvironmentP2P;
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
			case LOG_VIEWER:
				return new LogViewer<EnvironmentP2P>(environment).setTitle("Event Log");
			default:
				return null;
		}
		
	}

}
