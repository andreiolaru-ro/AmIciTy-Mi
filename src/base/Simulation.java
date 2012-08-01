package base;

import graphics.UpdateListener;

import javax.swing.JFrame;

import logging.Log;

public abstract class Simulation extends JFrame implements Runnable, UpdateListener
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	protected final static int		PRINTSTEP	= 1;// number of steps at which to print **STEP n** in console
	protected final static Log.Level	LEVEL		= Log.Level.INFO;	// initial level of logging, and
	protected final static int		LEVELSWITCH	= -1;// after this number of steps. negative value: never change
	protected final static Log.Level	LEVELTO		= Log.Level.INFO;								// switch to this level
	protected EnvironmentP2P env;
	protected Log log;
	protected boolean					active		= false; 
	protected boolean					oneStep		= false;
	
	
	public abstract void createMainWindow(int x, int y, int w, int h);

	public void start()
	{
		if(!active)
		{
			active = true;
			new Thread(this).start();
		}
	}
	
	public void stop()
	{
		active = false;
	}
	
	public void step()
	{
		if(!active)
		{
			oneStep = true;
			active = true;
			new Thread(this).start();
		}
	}
}
