package P2PAgent;


public class Item implements Comparable<Item>
{
	private int itemID;
	
	public Item(int itemID)
	{
		this.itemID=itemID;
	}
	public int getItemID()
	{
		return this.itemID;
	}
	public void setItemID(int itemID)
	{
		this.itemID = itemID;
	}
	@Override
	public int compareTo(Item itemOther)
	{
		// TODO Auto-generated method stub
		Integer itemThis=new Integer(this.itemID);
		Integer intItemOther= new Integer(itemOther.getItemID());
		return itemThis.compareTo(intItemOther);
	}
	
	@Override
	public int hashCode() 
	{
		Integer itemThis=new Integer(this.itemID);
		return itemThis.hashCode();
	}

}

