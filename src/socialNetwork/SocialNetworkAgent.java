package socialNetwork;

import java.util.Map;

import agent.AgentID;
import agent.Location;
import agent.LocationAgent;
import agent.Measure;
import agent.MeasureName;
import base.Message;

public class SocialNetworkAgent extends LocationAgent {
	
	protected Location location; 
	
	public SocialNetworkAgent(Location location2) {
		// TODO Auto-generated constructor stub
		this.location = location2;
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

	
}
