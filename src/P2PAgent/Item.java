/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru. See the AUTHORS file for more information.
 * 
 * This file is part of AmIciTy-Mi.
 * 
 * AmIciTy-Mi is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * AmIciTy-Mi is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with AmIciTy-Mi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package P2PAgent;

public class Item implements Comparable<Item> {
	private int	itemID;

	public Item(int itemID) {
		this.itemID = itemID;
	}

	public int getItemID() {
		return this.itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	@Override
	public int compareTo(Item itemOther) {
		Integer itemThis = new Integer(this.itemID);
		Integer intItemOther = new Integer(itemOther.getItemID());
		return itemThis.compareTo(intItemOther);
	}

	@Override
	public int hashCode() {
		Integer itemThis = new Integer(this.itemID);
		return itemThis.hashCode();
	}

}
