package P2PAgent;

public class Item
{
	private int ItemID;
	
	public Item(int cpt)
	{
		this.setItemID(cpt);
	}
	public int getItemID()
	{
		return ItemID;
	}
	public void setItemID(int itemID)
	{
		ItemID = itemID;
	}
}
