package base;


public class Command
{
	public static enum Action {
		INJECT, REQUEST, SNAPSHOT, INJECT_ITEM_WANTED, INJECT_ITEM;
	}
	
	protected Command.Action action	= null;
	protected int time;
	

	public void setTime( int time) {
		this.time = time;
	}

	public int getTime() {
		return time;
	}


	public void setAction( Command.Action action) {
		this.action = action;
	}

	public Command.Action getAction() {
		return action;
	}
}
