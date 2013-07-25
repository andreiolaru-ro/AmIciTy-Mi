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

import graphics.UpdateListener;

import javax.swing.JFrame;

import logging.Log;

public abstract class Simulation<ENVIRONMENT extends Environment<?, ?>, COMMAND extends Command>
		extends JFrame implements Runnable, UpdateListener {
	/**
	 * 
	 */
	private static final long			serialVersionUID	= 1L;
	protected final static int			PRINTSTEP			= 1;				// number
																				// of
																				// steps
																				// at
																				// which
																				// to
																				// print
																				// **STEP
																				// n**
																				// in
																				// console
	protected final static Log.Level	LEVEL				= Log.Level.INFO;	// initial
																				// level
																				// of
																				// logging,
																				// and
	protected final static int			LEVELSWITCH			= -1;				// after
																				// this
																				// number
																				// of
																				// steps.
																				// negative
																				// value:
																				// never
																				// change
	protected final static Log.Level	LEVELTO				= Log.Level.INFO;	// switch
																				// to
																				// this
																				// level
	protected ENVIRONMENT				environment;
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
