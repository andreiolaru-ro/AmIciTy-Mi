package P2PAgent;

import base.Simulation;


public class SimulationP2P extends Simulation<EnvironmentP2P>
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private static SimulationP2P p2p;
	
	
	public static void main(String[] args)
	{
		setP2P(new SimulationP2P());
	}
	
	
	public SimulationP2P()
	{
		
	}
	
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
}
