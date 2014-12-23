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
package P2PAgent;

import graphics.ControllableView;
import graphics.UpdateListener;
import graphics.ViewerFactory.WindowLayout;
import graphics.ViewerFactory.WindowLayout.Row;
import graphics.ViewerFactory.WindowParameters;
import graphics.ViewerFactoryP2P;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import logging.Log;
import scenario.AbstractScenario;
import scenario.P2PScenario;
import agent.AgentID;
import base.Environment;
import base.Simulation;

public class SimulationP2P extends Simulation<EnvironmentP2P, CommandP2P> {
	/**
	 * 
	 */
	private static final long					serialVersionUID	= 1L;
	private static SimulationP2P				p2p;

	public final static String					scenarioName		= "scenarios/p2pScenario.xml";

	P2PScenario									scenario			= new P2PScenario(scenarioName);

	private ControllableView<EnvironmentP2P>[]	viewers				= null;
	private static StepNumber					sn					= new StepNumber();
	private static JSlider						sw					= new StepDuration();

	private static class StepNumber extends JLabel implements UpdateListener {
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;

		public StepNumber() {
			super("   ---   ");
			setForeground(Color.black);
		}

		@Override
		public void update() {
			setText("   step " + Environment.getStep() + "   ");
		}
	}

	public static void main(String[] args) {
		setP2P(new SimulationP2P());
	}

	public SimulationP2P() {
		commands = scenario.getCommands();
		init1();
		WindowLayout layout = new WindowLayout(0, 0, 1280, 1000, 15, 1, 5, true, true);
		Row row = null;

		row = layout.addRow(graphics.ViewerFactory.Type.ITEM_GRAF, 4);

		row.add(new WindowParameters(graphics.ViewerFactory.Type.AGENT_SELECTION_GRID));
		row.add(new WindowParameters(graphics.ViewerFactory.Type.ITEM_GRAF));
		row.add(new WindowParameters(graphics.ViewerFactory.Type.ITEM_LOCATION_GRAF));
		row.add(new WindowParameters(graphics.ViewerFactory.Type.ITEM_WANTED_GRAF));
		layout.addMain(new WindowParameters(graphics.ViewerFactory.Type.CONTROL, -1, -1, this));
		layout.addMain(new WindowParameters(graphics.ViewerFactory.Type.LOG_VIEWER, -1, -1, 0, 0));

		// layout.addMain(new WindowParameters(Type.AGENT_DETAILS, -1, -1, 0,
		// 0));

		viewers = ViewerFactoryP2P.createViewers(environment, layout.toCollection());
		init2();
	}

	private void init1() {
		environment = new EnvironmentP2P(this, scenario);
		environment.getLogger().setLevel(LEVEL);
		log = new Log(null);
		environment.getLogger().addLog(log);
		nextcommand = 0;
		// to initialize the variable item and itemWanted in P2PAgent
		int step = Environment.getStep();
		// System.out.println((step));
		while (nextcommand < commands.length && commands[nextcommand].getTime() == step) {
			doCommand(commands[nextcommand++]);
		}
		nextcommand = 0;
	}

	private void init2() {
		environment.addUpdateListener(this);
		environment.addUpdateListener(sn);

		for (ControllableView<EnvironmentP2P> viewer : viewers)
			if (viewer != null)
				viewer.relink(environment);
		environment.doUpdate();
	}

	public static void setP2P(SimulationP2P p2p) {
		SimulationP2P.p2p = p2p;
	}

	public static SimulationP2P getP2P() {
		return SimulationP2P.p2p;

	}

	@Override
	public void run() {
		active = true;// don't forget to change it after

		int step = Environment.getStep();

		while (step < scenario.getNsteps() && active) {
			step = Environment.getStep();
			if (step == LEVELSWITCH)
				environment.getLogger().setLevel(LEVELTO);

			if (step % PRINTSTEP == 0) {
				log.le("===================== STEP ~ ======================", new Integer(step));
			} else {
				log.li("===================== STEP ~ ======================", new Integer(step));
			}
			while (nextcommand < commands.length && commands[nextcommand].getTime() == step) {
				doCommand(commands[nextcommand++]);
			}
			try {
				environment.step();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(sw.getValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (oneStep) {
				oneStep = false;
				break;
			}
		}
		active = false;
	}

	@Override
	public void update() {

	}

	@Override
	public void createMainWindow(int x, int y, int w, int h) {
		this.setLayout(new BorderLayout());

		Panel box = new Panel();
		box.setLayout(new BoxLayout(box, BoxLayout.LINE_AXIS));
		box.setBackground(Color.white);
		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				start();
			}
		});
		box.add(start);
		JButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		box.add(stop);
		JButton step = new JButton("Step");
		step.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				step();
			}
		});
		box.add(step);
		// JButton reset = new JButton("Reset !");
		// reset.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e)
		// {
		// init1();
		// init2();
		// }
		// });
		// box.add(reset);

		box.add(sn);

		final JButton randomize = new JButton("Randomize " + AbstractScenario.getSeed());
		randomize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractScenario.resetRandom();
				randomize.setText("Randomize " + AbstractScenario.getSeed());
			}
		});
		box.add(randomize);
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(".");

				if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					// scenario.save(fc.getSelectedFile());
				}
			}
		});
		box.add(save);

		JButton override = new JButton("Override");
		override.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String scenarioNameMod = new String(scenarioName);
				scenarioNameMod = scenarioNameMod.replaceAll(".xml", "-mod.xml");
				scenario.save(new File(scenarioNameMod));
			}
		});
		box.add(override);

		this.add(box, BorderLayout.CENTER);

		box = new Panel();
		box.add(sw);
		this.add(box, BorderLayout.SOUTH);

		this.setTitle("KCA");
		this.setLocation(x, y);
		this.setSize(w, h);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);

	}

	@Override
	protected void doCommand(CommandP2P command) {
		// TODO Auto-generated method stub
		if (command.getAction() == CommandP2P.Action.INJECT_ITEM) {
			log.le("injecting item owned ~", command.getItem().getItemID());
			AgentID receiver = environment.injectItem(command.getAgentID(), command.getItem());
			log.le("received by agent ~", receiver.getId());
		} else if (command.getAction() == CommandP2P.Action.INJECT_ITEM_WANTED) {
			log.le("injecting item wanted ~", command.getItem().getItemID());
			AgentID receiver = environment
					.injectItemWanted(command.getAgentID(), command.getItem());
			log.le("received by agent ~", receiver.getId());
		}
	}
}
