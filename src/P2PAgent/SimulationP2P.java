package P2PAgent;

import logging.Log;
import scenario.P2PScenario;
import base.Command;
import base.Simulation;


public class SimulationP2P extends Simulation<EnvironmentP2P, Command>//à changer
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private static SimulationP2P p2p;
	private P2PScenario scenario = new P2PScenario("");
	
	public static void main(String[] args)
	{
		setP2P(new SimulationP2P());
	}
	
	
	public SimulationP2P()
	{
		commands = scenario.getCommands;
		init1();
		start();
	}
	
	private void init1()
	{
		// TODO Auto-generated method stub
		cm= new EnvironmentP2P(this, scenario);
		cm.getLogger().setLevel(LEVEL);
		log = new Log(null);
		cm.getLogger().addLog(log);
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
	protected void doCommand(Command command)
	{
		// TODO Auto-generated method stub
		
	}
}
