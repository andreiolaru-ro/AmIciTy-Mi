package base;

import graphics.UpdateListener;

import javax.swing.JFrame;

import logging.Log;

public abstract class Simulation<ENVIRONMENT extends Environment<?, ?>, COMMAND extends Command>
		extends JFrame implements Runnable, UpdateListener {
	/**
	 * 
	 */
	private static final long			serialVersionUID	= 1L;
	// number of steps at which to print **STEP n** in console
	protected final static int			PRINTSTEP			= 1;
	// initial level of logging, and
	protected final static Log.Level	LEVEL				= Log.Level.INFO;
	// after this number of steps. negative value: never change
	protected final static int			LEVELSWITCH			= -1;
	// switch to this level
	protected final static Log.Level	LEVELTO				= Log.Level.INFO;
	protected ENVIRONMENT				cm;
	protected Log						log;
	protected int						nextcommand			= 0;
	protected COMMAND[]					commands;
	/**
	 * To save the action of the start and stop button
	 */
	protected boolean					active				= false;
	protected boolean					oneStep				= false;

	public abstract void createMainWindow(int x, int y, int w, int h);

	protected abstract void doCommand(COMMAND command);

	public void start() {
		if (!active) {
			active = true;
			new Thread(this).start();
		}
	}

	public void stop() {
		active = false;
	}

	public void step() {
		if (!active) {
			oneStep = true;
			active = true;
			new Thread(this).start();
		}
	}
}
