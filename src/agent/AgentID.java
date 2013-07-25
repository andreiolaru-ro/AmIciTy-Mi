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
package agent;

public class AgentID implements Comparable<AgentID> {
	public static int	iteratorId	= 0;

	private String		name;
	private Integer		id;

	@SuppressWarnings("hiding")
	public AgentID(String name) {
		this.name = name;
		this.id = new Integer(AgentID.iteratorId);

		AgentID.iteratorId++;
	}

	@SuppressWarnings("hiding")
	public AgentID(Integer id) {
		this.name = id.toString();
		this.id = id;

		AgentID.iteratorId++;
	}

	public AgentID(double x, double y) {
		this.name = "(" + x + "," + y + ")";
		this.id = new Integer(AgentID.iteratorId);

		AgentID.iteratorId++;
	}

	@Override
	public int compareTo(AgentID agent) {
		return id.compareTo(agent.id);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof AgentID && ((AgentID) obj).name.equals(name)
				&& ((AgentID) obj).id.equals(id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return "#" + id + " #" + name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
