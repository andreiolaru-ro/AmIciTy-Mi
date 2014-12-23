/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru. See the AUTHORS file for more information.
 * 
 * This file is part of AmIciTy-Mi.
 * 
 * AmIciTy-Mi is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * AmIciTy-Mi is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with AmIciTy-Mi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package base;

import java.awt.Color;

import graphics.UpdateListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import logging.Log;

/**
 * Abstract class for simulation classes, covering general simulation features,
 * like starting, stopping, pausing, logging.
 * 
 * @param <ENVIRONMENT>
 *            - the {@link Environment} class that will be used by this
 *            simulation class.
 * @param <COMMAND>
 *            - the {@link Command} class that is used by this simulation class.
 */
public abstract class Simulation<ENVIRONMENT extends Environment<?, ?>, COMMAND extends Command>
		extends JFrame implements Runnable, UpdateListener {

	/**
	 * Visual control for displaying the number of the current step.
	 */
	public static class StepNumber extends JLabel implements UpdateListener {
		/**
		 * the serial version UID
		 */
		private static final long	serialVersionUID	= 1L;

		/**
		 * Default constructor.
		 */
		public StepNumber() {
			super("   ---   ");
			setForeground(Color.black);
		}

		@Override
		public void update() {
			setText("   step " + Environment.getStep() + "   ");
		}
	}

	/**
	 * Visual control for setting the duration of a step.
	 */
	public static class StepDuration extends JSlider {
		/**
		 * the serial version UID.
		 */
		private static final long	serialVersionUID	= 1L;

		/**
		 * Default constructor. 
		 */
		public StepDuration() {
			super(SwingConstants.HORIZONTAL, 0, 200, 0);
		}
	}

	/**
	 * the serial version id.
	 */
	private static final long			serialVersionUID	= 1L;
	/**
	 * number of steps at which to print **STEP n** in console
	 */
	protected final static int			PRINTSTEP			= 1;
	/**
	 * initial level of logging, and
	 */
	protected final static Log.Level	LEVEL				= Log.Level.INFO;
	/**
	 * after this number of steps. negative value: never change
	 */
	protected final static int			LEVELSWITCH			= -1;
	/**
	 * switch to this level
	 */
	protected final static Log.Level	LEVELTO				= Log.Level.INFO;
	/**
	 * The environment.
	 */
	protected ENVIRONMENT				environment;
	/**
	 * The log that is used.
	 */
	protected Log						log;
	/**
	 * The next command to issue.
	 */
	protected int						nextcommand			= 0;
	/**
	 * The series of commands to issue.
	 */
	protected COMMAND[]					commands;
	/**
	 * To save the action of the start and stop button - indicates whether the
	 * simulation is active.
	 */
	protected boolean					active				= false;
	/**
	 * Indicates whether the simulation should pause after one step.
	 */
	protected boolean					oneStep				= false;

	/**
	 * Creates the main window for simulation controls. This method is called by
	 * <code>createViewers</code> methods.
	 * 
	 * @param x
	 *            - position x
	 * @param y
	 *            - position y
	 * @param w
	 *            - size x
	 * @param h
	 *            - size y
	 */
	public abstract void createMainWindow(int x, int y, int w, int h);

	/**
	 * Issues a command to the environment. This method is here only for
	 * improved structure.
	 * 
	 * @param command
	 *            - the command to issue.
	 */
	protected abstract void doCommand(COMMAND command);

	/**
	 * Basic functionality for starting a simulation.
	 */
	public void start() {
		if (!active) {
			active = true;
			new Thread(this).start();
		}
	}

	/**
	 * Basic functionality for stopping a simulation.
	 */
	public void stop() {
		active = false;
	}

	/**
	 * Basic simulation to execute one step of the simulation. If the simulation
	 * is not active, this only will execute one step of the simulation and then
	 * stop.
	 */
	public void step() {
		if (!active) {
			oneStep = true;
			active = true;
			new Thread(this).start();
		}
	}
}
