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

/**
 * 
 * enum of the different measures that an agent can have
 * 
 */
public enum MeasureName {
	SPECIALTY("specialty"),

	LOCATION("location"),

	CAPACITY("capacity"),

	AGENT_PRESSURE("agent pressure"),

	LOWPRESSURE("low pressure"),

	HIGHPRESSURE("high pressure"),

	PERSISTENCE("persistence"),

	NEIGHBOUR_DATA_FACT_PERSISTENCE("neighbour data fact persistence"),

	NEW_FACT_GOAL_IMPORTANCE("new fact goal importance"),

	SPEC_UPDATE_RATIO("spec update ratio"),

	SECONDARY_PRESSURE_FADE("secondary pressure fade"),

	SECONDARY_PERSISTENCE_FADE("secondary persistence fade"),

	AGENT_BALANCE("agent balance"),

	PROBABILITY("probability"),

	AGENT_USELESS_FACT("agent useless fact");

	private String	nameEnum;

	private MeasureName(String name) {
		this.nameEnum = name;
	}

	@Override
	public String toString() {
		return nameEnum;
	}
}
