package P2PAgent;

import base.Command;

public class CommandP2P extends Command
{
	private Item item=null;
	private Item itemWanted=null;
	
	public CommandP2P(CommandP2P.Action action, Item item, Item itemWanted, int time)
	{
		this.action=action;
		this.time=time;
		this.setItem(item);
		this.setItemWanted(itemWanted);
	}
	
	public CommandP2P(CommandP2P.Action action, Item item, Item itemWanted)
	{
		this(action, item, itemWanted, 0);
	}
	
	public CommandP2P(Command.Action action, int ms)
	{
		this(action, null, null, ms);
	}

	public Item getItem()
	{
		return item;
	}

	public void setItem(Item item)
	{
		this.item = item;
	}

	public Item getItemWanted()
	{
		return itemWanted;
	}

	public void setItemWanted(Item itemWanted)
	{
		this.itemWanted = itemWanted;
	}
}
