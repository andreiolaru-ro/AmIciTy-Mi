package base;

import graphics.AbstractGraphViewer;
import graphics.ControllableView;
import graphics.UpdateListener;
import graphics.ViewerFactory;
import graphics.ViewerFactory.Type;
import graphics.ViewerFactory.WindowLayout;
import graphics.ViewerFactory.WindowLayout.Row;
import graphics.ViewerFactory.WindowParameters;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import logging.Log;
import scenario.AbstractScenario;
import scenario.KCAScenario;
import KCAAgent.DataContent;
import KCAAgent.Fact;
import KCAAgent.Goal.GoalType;
import KCAAgent.Logix.Domain;
import KCAAgent.MessageKCA;
import agent.AgentID;


public class SimulationKCA extends JFrame implements Runnable, UpdateListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static class StepNumber extends JLabel implements UpdateListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public StepNumber()
		{
			super("   ---   ");
			setForeground(Color.black);
		}
		
		@Override
		public void update()
		{
			setText("   step " + Environment.getStep() + "   ");
		}
	}
	
	private final static int		PRINTSTEP	= 1;											// number of steps at which to print **STEP n** in console
	// private final static int STEPWAIT = 5; // sleep after each step
	private final static Log.Level	LEVEL		= Log.Level.INFO;								// initial level of logging, and
	private final static int		LEVELSWITCH	= -1;											// after this number of steps. negative value: never change
	private final static Log.Level	LEVELTO		= Log.Level.INFO;								// switch to this level
																								
//	 private Scenario scenario = new Scenario("test/testSmall2.xml");
	KCAScenario				scenario	= new KCAScenario("test/test_scenario.xml");
//	Scenario				scenario	= new Scenario("test/test1.xml");
	
	private Environment				cm;
	
	private DataContent[]			data		= scenario.getData();
	
	private static StepNumber		sn			= new StepNumber();
	private static JSlider			sw			= new JSlider(SwingConstants.HORIZONTAL, 0, 200, 0);
	
	private ControllableView[]		viewers		= null;
	
	private int						nextcommand	= 0;
	
	private Log						log;
	
	/**
	 *  To save the action of the start and stop button
	 */
	private boolean					active		= false; 
	
	private boolean					oneStep		= false;
	
	private Command[]				commands	= scenario.getCommands();
	private static SimulationKCA kca;
	
	// private Command[] absCommands = scenario.getAbsCommands();
	
	public static void main(String[] args)
	{
		setKca(new SimulationKCA());
	}
	

	public SimulationKCA()
	{
		init1();
		
		WindowLayout layout = new WindowLayout(0, 0, 1280, 1000, 15, 1, 5, true, true); // (1366), windows 7
//		WindowLayout layout = new WindowLayout(70, 0, 1600, 1000, 15, 1, 5, true, true); // larger (1680), windows 7
		// WindowLayout layout = new WindowLayout(70, 0, 1200, 800, 20, 1, 5, true, true); // large (1280), windows 7
		// WindowLayout layout = new WindowLayout(0, 0, 1000, 600, 60, 1, 5, true, true); // small (1024)
		
		Row row;
		
		int ndata = scenario.getData().length;
		
		row = layout.addRow(Type.FACTS_GRID, ndata);
		for(int i = 0; i < ndata; i++)
			row.add(data[i]);
		
		row = layout.addRow(Type.DOMAIN_INTEREST_GRID, 6);
		row.add(new WindowParameters(Type.SPECIALTY_GRID));
		for(Domain dom : Domain.values())
			row.add(dom);
		row.add(new WindowParameters(Type.PRESSURE_GRID));
		row.add(new WindowParameters(Type.AGENT_SELECTION_GRID));
		
		row = layout.addRow(Type.GLOBAL_FACT_NUMBER_GRAPH, ndata + 1, new AbstractGraphViewer.GraphParam(null, new AbstractGraphViewer.GraphLink("LF"), new Integer(1)));
		for(int i = 0; i < ndata; i++)
			row.add(data[i]);
		row.add((Object)null);
		
		row = layout.addRow(null, 6);
		row.add(new WindowParameters(Type.GLOBAL_PRESSURE_GRAPH));
		row.add(new WindowParameters(Type.MAX_PRESSURE_GRAPH));
		row.add(new WindowParameters(Type.MESSAGE_AVG_GRAPH));
		row.add(new WindowParameters(Type.USELESS_FACTS_AVG_GRAF));
		row.add(new WindowParameters(Type.AGENT_BALANCE));
		row.add(new WindowParameters(Type.AGENT_BALANCE_AVG_GRAF));
		
		row = layout.addRow(Type.GLOBAL_GOAL_NUMBER_GRAPH, 3, new AbstractGraphViewer.GraphParam(null, new AbstractGraphViewer.GraphLink("LG"), new Integer(1)));
		row.add((GoalType)null);
		row.add(GoalType.INFORM);
		// row.add(GoalType.GET);
		row.add(GoalType.FREE);
		
		layout.addMain(new WindowParameters(Type.CONTROL, -1, -1, this));
		
		layout.addMain(new WindowParameters(Type.LOG_VIEWER, -1, -1, 0, 0));
		
		// layout.addMain(new WindowParameters(Type.AGENT_DETAILS, -1, -1, 0, 0));
		
		viewers = ViewerFactory.createViewers(cm, layout.toCollection());
		
		init2();
	}
	
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
	
	private void init1()
	{
		cm = new Environment(this, scenario);
		cm.getLogger().setLevel(LEVEL);
		log = new Log(null);
		cm.getLogger().addLog(log);
		nextcommand = 0;
	}
	
	private void init2()
	{
		cm.addUpdateListener(this);
		cm.addUpdateListener(sn);
		
		for(ControllableView viewer : viewers)
			if(viewer != null)
				viewer.relink(cm);
		
		cm.doUpdate();
	}
	
	void doCommand(Command command)
	{
		if(command.getAction() == Command.Action.INJECT)
		{
			log.le("injecting ~ at ~", command.getFact(), command.getLocation());
			AgentID receiver = cm.inject(command.getLocation(), new MessageKCA<Collection<Fact>>(null, MessageKCA.Type.INFORM, command.getFact().toCollection()));
			log.le("received by ~", receiver);
		}
		else if(command.getAction() == Command.Action.REQUEST)
		{
			log.le("requesting ~ from ~", command.getFact(), command.getLocation());
			cm.inject(command.getLocation(), new MessageKCA<Collection<Fact>>(null, MessageKCA.Type.REQUEST, command.getFact().toCollection()));
		}
		else if(command.getAction() == Command.Action.SNAPSHOT)
		{
			// doSnapshot("data_" + command.fact.getData().getId() + "_" + step + ".txt", command.fact.getData().getId());
			// log.li("Snapshot on ~", command.fact.getData().getId());
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
					// TODO
					// boolean contains = cm.cellAt(i, j).getData().contains(new DataContent(new Data(dataID), 0));
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
				cm.getLogger().setLevel(LEVELTO);
			
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
				cm.step();
			} catch (Exception e1)
			{
				// TODO Auto-generated catch block
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
	
	@Override
	public void update()
	{
		this.setTitle("KCA - " + Environment.getStep());
	}


	public static SimulationKCA getKca() {
		return kca;
	}


	public static void setKca(SimulationKCA kca1) {
		SimulationKCA.kca = kca1;
		
	}
}
