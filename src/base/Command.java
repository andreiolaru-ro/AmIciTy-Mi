package base;


public class Command
{
	public static enum Action {
		INJECT, REQUEST, SNAPSHOT
	}
	
	protected Command.Action	action	= null;
	protected int time;
	
	@SuppressWarnings("hiding")
	public void setTime( int time) {
		this.time = time;
	}

	public int getTime() {
		return time;
	}

	@SuppressWarnings("hiding")
	public void setAction( Command.Action action) {
		this.action = action;
	}

	public Command.Action getAction() {
		return action;
	}
}
