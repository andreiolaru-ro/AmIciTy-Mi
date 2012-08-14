package socialNetwork;

import java.util.Map;

import agent.AgentID;
import agent.Location;
import agent.LocationAgent;
import agent.Measure;
import agent.MeasureName;
import base.Message;

public class SocialNetworkAgent extends LocationAgent {
	
	public SocialNetworkAgent(AgentID id, Location location2) {
		super(id, location2);
	}

	@Override
	public Measure getMeasure(MeasureName measure) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map getAllMeasures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void agentPrint() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void step() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void sendMessage(AgentID to, Message<?> msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessage(Message<?> message) {
		// TODO Auto-generated method stub
		
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public boolean isSelected()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void toggleSelected()
	{
		// TODO Auto-generated method stub
		
	}

	
}
