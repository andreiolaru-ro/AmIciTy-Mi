package P2PAgent;

import logging.Log;
import scenario.P2PScenario;
import agent.AgentID;
import base.Environment;
import base.Simulation;


public class SimulationP2P extends Simulation<EnvironmentP2P, CommandP2P>
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private static SimulationP2P p2p;
	private P2PScenario scenario = new P2PScenario("scenarios/p2pScenario.xml");
	
	public static void main(String[] args)
	{
		setP2P(new SimulationP2P());
	}
	
	
	public SimulationP2P()
	{
		commands = scenario.getCommands();
		init1();
		cm.check();
		start();
	}
	
	private void init1()
	{
		// TODO Auto-generated method stub
		cm= new EnvironmentP2P(this, scenario);
		cm.getLogger().setLevel(LEVEL);
		log = new Log(null);
		cm.getLogger().addLog(log);
		nextcommand = 0;
		//to initialize the variable item and itemWanted in P2PAgent
		int step= Environment.getStep();
		//System.out.println((step));
		while(nextcommand < commands.length && commands[nextcommand].getTime() == step)
		{
			doCommand(commands[nextcommand++]);
		}
		nextcommand = 0;
	}


	@SuppressWarnings("hiding")
	public static void setP2P(SimulationP2P p2p)
	{
		SimulationP2P.p2p=p2p;
	}
	public static SimulationP2P getP2P()
	{
		return SimulationP2P.p2p;
		
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		active=true;//don't forget to change it after
		
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
				//e1.printStackTrace();
			}
			if(oneStep)
			{
				oneStep = false;
				break;
			}
		}
		active = false;
		cm.check();
	}


	@Override
	public void update()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createMainWindow(int x, int y, int w, int h)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doCommand(CommandP2P command)
	{
		// TODO Auto-generated method stub
		if(command.getAction() == CommandP2P.Action.INJECT_ITEM)
		{
			log.le("injecting ~ at ~", command.getItem());
			AgentID receiver = cm.injectItem(command.getAgentID(), command.getItem());
			log.le("received by ~", receiver);
		}
		else if(command.getAction() == CommandP2P.Action.INJECT_ITEM_WANTED)
		{
			log.le("injecting ~ at ~", command.getItem());
			AgentID receiver = cm.injectItemWanted(command.getAgentID(), command.getItem());
			log.le("received by ~", receiver);
		}
	}
}

