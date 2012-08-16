package base;


public class Command
{
	public static int iterator = 0 ;
	
	public static enum Action {
		INJECT, REQUEST, SNAPSHOT, INJECT_ITEM_WANTED, INJECT_ITEM, PAUSE, UNPAUSE;
	}
	
	protected Command.Action action	= null;
	protected int time;
	protected int id ;
	
	
	
	public Command(Action action, int time) {
		super();
		this.action = action;
		this.time = time;
		this.id = iterator;
		iterator++;
	}

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Command [action=" + action + ", time=" + time + "]";
	}
	
	
}
