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
package KCAAgent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.WindowConstants;

import logging.Log;
import net.xqhs.windowLayout.LayoutIndications.BarPosition;
import net.xqhs.windowLayout.rows.RowsLayout;
import net.xqhs.windowLayout.rows.RowsLayoutIndications;
import KCAAgent.Goal.GoalType;
import KCAAgent.Logix.Domain;
import base.Command;
import base.Environment;
import base.Message;
import base.Simulation;
import base.agent.AgentID;
import base.agent.LocationAgent;
import base.graphics.AbstractGraphViewer;
import base.graphics.AbstractGraphViewer.GraphParam;
import base.graphics.ControllableView;
import base.scenario.AbstractScenario;

/**
 * Main class for managing a KCA-type simulation
 */
public class SimulationKCA extends Simulation<EnvironmentKCA, CommandKCA>
{
	/**
	 * The serial version UID.
	 */
	private static final long					serialVersionUID	= 1L;
	
	/**
	 * The name of the scenario to use.
	 */
	// public final static String scenarioName = "scenarios/kcaScenario.xml";
	// public final static String scenarioName = "scenarios/ooo.xml";
	public final static String					scenarioName		= "scenarios/kca_test3r.xml";
	
	KCAScenario									scenario			= new KCAScenario(scenarioName);
	
	private DataContent[]						data				= scenario.getData();
	
	private static StepNumber					sn					= new StepNumber();
	private static JSlider						sw					= new StepDuration();
	
	private ControllableView<EnvironmentKCA>[]	viewers				= null;
	
	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		new SimulationKCA();
	}
	
	public SimulationKCA()
	{
		commands = scenario.getCommands();
		
		init1();
		
		RowsLayout layout = new RowsLayout((RowsLayoutIndications) new RowsLayoutIndications()
				.indicateNMainsIndications(1).indicateControlH(70).indicateMainPercent(30).indicateRowCount(5)
				.makeSquare().indicateBar(BarPosition.LEFT, 100, 0).indicateBar(BarPosition.TOP, 70, 0).indicateW(1050)
				.indicateH(900));
		
		// larger (1680), windows 7
		// WindowLayout layout = new WindowLayout(70, 0, 1600, 1000, 15, 1, 5,
		// true, true);
		
		// large (1280), windows 7
		// WindowLayout layout = new WindowLayout(70, 0, 1200, 800, 20, 1, 5,
		// true, true);
		
		// small (1024)
		// WindowLayout layout = new WindowLayout(0, 0, 1000, 600, 60, 1, 5,
		// true, true);
		
		layout.addRow(base.graphics.ViewerFactory.Type.FACTS_GRID.toString(), data);
		
		String[] types = new String[4 + Domain.values().length];
		int i = 0;
		for(@SuppressWarnings("unused")
		Domain dom : Domain.values())
			types[i++] = base.graphics.ViewerFactory.Type.DOMAIN_INTEREST_GRID.toString();
		types[i++] = base.graphics.ViewerFactory.Type.SPECIALTY_GRID.toString();
		types[i++] = base.graphics.ViewerFactory.Type.PRESSURE_GRID.toString();
		types[i++] = base.graphics.ViewerFactory.Type.AGENT_SELECTION_GRID.toString();
		types[i++] = base.graphics.ViewerFactory.Type.PAUSE_GRID.toString();
		layout.addRow(-1, -1, types, Domain.values());
		
		GraphParam[] param = new GraphParam[data.length];
		i = 0;
		for(Object datum : data)
			param[i++] = new AbstractGraphViewer.GraphParam(null, new AbstractGraphViewer.GraphLink("LF"), 1, datum);
		
		layout.addRow(base.graphics.ViewerFactory.Type.GLOBAL_FACT_NUMBER_GRAPH.toString(), param);
		
		layout.addRow(new String[] { base.graphics.ViewerFactory.Type.GLOBAL_PRESSURE_GRAPH.toString(),
				base.graphics.ViewerFactory.Type.MAX_PRESSURE_GRAPH.toString(),
				base.graphics.ViewerFactory.Type.MESSAGE_AVG_GRAPH.toString(),
				base.graphics.ViewerFactory.Type.USELESS_FACTS_AVG_GRAF.toString(),
				base.graphics.ViewerFactory.Type.AGENT_BALANCE_AVG_GRAF.toString(), });
		
		Object[] items = new Object[] { null, GoalType.INFORM, /* GoalType.GET, */GoalType.FREE };
		GraphParam[] param1 = new GraphParam[items.length];
		i = 0;
		for(Object item : items)
			param1[i++] = new AbstractGraphViewer.GraphParam(null, new AbstractGraphViewer.GraphLink("LG"), 1, item);
		layout.addRow(base.graphics.ViewerFactory.Type.GLOBAL_GOAL_NUMBER_GRAPH.toString(), param1);
		
		layout.addMain(-1, 100, base.graphics.ViewerFactory.Type.CONTROL.toString(), this);
		layout.addMain(-1, -1, base.graphics.ViewerFactory.Type.LOG_VIEWER.toString(), null);
		
		// layout.addMain(new WindowParameters(Type.AGENT_DETAILS, -1, -1, 0,
		// 0));
		
		viewers = ViewerFactoryKCA.createViewers(environment, layout.getAllWindows());
		
		init2();
	}
	
	@Override
	public void createMainWindow(int x, int y, int w, int h)
	{
		this.setLayout(new BorderLayout());
		
		Panel box = new Panel();
		box.setLayout(new BoxLayout(box, BoxLayout.LINE_AXIS));
		box.setBackground(Color.white);
		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				start();
			}
		});
		box.add(start);
		JButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				stop();
			}
		});
		box.add(stop);
		JButton step = new JButton("Step");
		step.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				step();
			}
		});
		box.add(step);
		
		box.add(sn);
		
		final JButton randomize = new JButton("Randomize " + AbstractScenario.getSeed());
		randomize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				AbstractScenario.resetRandom();
				randomize.setText("Randomize " + AbstractScenario.getSeed());
			}
		});
		box.add(randomize);
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser(".");
				
				if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					scenario.save(fc.getSelectedFile());
				}
			}
		});
		box.add(save);
		
		JButton override = new JButton("Overwrite");
		override.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
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
		this.pack();
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	private void init1()
	{
		environment = new EnvironmentKCA(this, scenario);
		environment.getLogger().setLevel(LEVEL);
		log = new Log(null);
		environment.getLogger().addLog(log);
		nextcommand = 0;
	}
	
	private void init2()
	{
		environment.addUpdateListener(this);
		environment.addUpdateListener(sn);
		
		for(ControllableView<EnvironmentKCA> viewer : viewers)
			if(viewer != null)
				viewer.relink(environment);
		
		environment.doUpdate();
	}
	
	@Override
	protected void doCommand(CommandKCA command)
	{
		if(command.getAction() == Command.Action.INJECT)
		{
			log.le("injecting ~ at ~", command.getFact(), command.getLocation());
			AgentID receiver = environment.inject(command.getLocation(), new MessageKCA(null, MessageKCA.Type.INFORM,
					command.getFact().toCollection()));
			if(receiver != null)
				log.le("received by ~", receiver);
			else
				log.le("received by noone, every agents in pause");
		}
		else if(command.getAction() == Command.Action.REQUEST)
		{
			log.le("requesting ~ from ~", command.getFact(), command.getLocation());
			environment.inject(command.getLocation(), new MessageKCA(null, MessageKCA.Type.REQUEST, command.getFact()
					.toCollection()));
		}
		else if(command.getAction() == Command.Action.SNAPSHOT)
		{
			// doSnapshot("data_" + command.fact.getData().getId() + "_" + step
			// + ".txt", command.fact.getData().getId());
			// log.li("Snapshot on ~", command.fact.getData().getId());
		}
		else if(command.getAction() == Command.Action.PAUSE)
		{
			// if(!command.getAgent().isPause())
			command.getAgent().getLog().lf("agents paused ");
			command.getAgent().pause();
		}
		else if(command.getAction() == Command.Action.UNPAUSE)
		{
			// if(command.getAgent().isPause())
			command.getAgent().getLog().lf("agents unpaused ");
			command.getAgent().unpause();
		}
		else if(command.getAction() == Command.Action.MOVE)
		{
			LocationAgent agent = (LocationAgent) command.getAgent();
			agent.setLocation(command.getLocation());
		}
	}
	
	void doSnapshot(String name)
	{
		PrintStream ps = null;
		try
		{
			ps = new PrintStream(name);
			ps.println("\\begin{verbatim}");
			for(int i = 0; i < scenario.getHeight(); i++)
			{
				for(int j = 0; j < scenario.getWidth(); j++)
				{
					// boolean contains = cm.cellAt(i, j).getData().contains(new
					// DataContent(new Data(dataID), 0));
					// ps.print(contains ? "." : " ");
				}
				ps.println();
			}
			ps.println("\\end{verbatim}");
			ps.close();
		} catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
	}
	
	void doReply(Message<?> msg)
	{
		log.le("received ~", msg);
	}
	
	@Override
	public void run()
	{
		int step = Environment.getStep();
		while(step < scenario.getNsteps() && active)
		{
			step = Environment.getStep();
			
			if(step == LEVELSWITCH)
				environment.getLogger().setLevel(LEVELTO);
			
			if(step % PRINTSTEP == 0)
			{
				log.le("===================== STEP ~ ======================", new Integer(step));
			}
			else
			{
				log.li("===================== STEP ~ ======================", new Integer(step));
			}
			
			// for(int i = 0; i < absCommands.length; i++)
			// if(absCommands[i].time == step)
			// doCommand(absCommands[i]);
			
			while(nextcommand < commands.length && commands[nextcommand].getTime() == step)
			{
				doCommand(commands[nextcommand++]);
			}
			try
			{
				environment.step();
			} catch(Exception e1)
			{
				e1.printStackTrace();
			}
			try
			{
				Thread.sleep(sw.getValue());
			} catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			if(oneStep)
			{
				oneStep = false;
				break;
			}
		}
		active = false;
	}
	
	@Override
	public void update()
	{
		this.setTitle("KCA - " + Environment.getStep());
	}
	
}
