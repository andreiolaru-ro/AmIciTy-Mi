package base;

import KCAAgent.KCAAgent;
import agent.AbstractAgent;
import agent.Location;

public class PauseCommand extends Command {

	protected AbstractAgent	agent	= null;

	@SuppressWarnings("hiding")
	public PauseCommand(Command.Action action, KCAAgent agent, int time) {
		super(action, time);
		this.agent = agent;
	}

	public AbstractAgent getAgent() {
		return agent;
	}

	public void setAgent(AbstractAgent agent) {
		this.agent = agent;
	}

	@Override
	public String toString() {
		return "PauseCommand [agent=" + agent + "]";
	}
	
	
}
