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
package base.agent;

public abstract class LocationAgent extends AbstractAgent {

	protected Location	location;

	public LocationAgent(AgentID id, Location location) {
		super(id);
		this.location = location;

	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location.setX(location.getX());
		this.location.setY(location.getY());

	}

}
