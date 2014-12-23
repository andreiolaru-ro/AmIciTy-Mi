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

import base.Environment;
import base.Message;
import base.agent.AgentID;

public class MessageP2P<T> extends Message<T> implements Comparable<MessageP2P<T>> {
	public enum Type {
		REQUEST_ITEM, SEND_ITEM, // send the items requested
		SEND_LOCATION, // send the locations of the items requested
		ASK_LOCATION;// ask the location of an item for an other agent
	}

	private Type	type;

	public MessageP2P(AgentID from, Type type, T content) {
		super();
		this.step = Environment.getStep();
		this.sequence = Environment.getSequence();
		this.from = from;
		this.type = type;
		this.content = content;
	}

	public Type getType() {
		return type;
	}

	@Override
	public int compareTo(MessageP2P<T> m) {

		if (m == null)
			throw new NullPointerException();
		if (from == null)
			return -1; // prioritize messages from the exterior (human users)
		if (type.ordinal() != m.type.ordinal())
			return type.ordinal() - m.type.ordinal();
		// TODO return something based on pressure; do not calculate interest
		// here!
		if (step != m.step)
			return this.step - m.step;
		return (this.sequence - m.sequence);

	}

	@Override
	public String toString() {
		String stringContent = content.toString();
		return "[" + from + ":" + type + ":" + stringContent + "]";
	}

}
