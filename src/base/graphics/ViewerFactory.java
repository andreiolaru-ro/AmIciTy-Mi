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
package base.graphics;

public abstract class ViewerFactory {
	protected static double	balanceMinimum	= 0.4;

	public enum Type {
		CONTROL,

		// AGENT_DETAILS,

		// SELECTED_AGENT_DETAILS,

		AGENT_SELECTION_GRID,

		SPECIALTY_GRID,

		PRESSURE_GRID,

		FACTS_GRID,

		DOMAIN_INTEREST_GRID,

		PAUSE_GRID,

		// DATA_GRID,

		PRESSURE_SURFACE,

		DATA_FACTS_SURFACE,

		LOG_VIEWER,

		GLOBAL_FACT_NUMBER_GRAPH,

		GLOBAL_GOAL_NUMBER_GRAPH,

		GLOBAL_PRESSURE_GRAPH,

		MAX_PRESSURE_GRAPH,

		MESSAGE_AVG_GRAPH,

		USELESS_FACTS_AVG_GRAF,

		AGENT_BALANCE,

		AGENT_BALANCE_AVG_GRAF,

		ITEM_GRAF,

		ITEM_WANTED_GRAF,

		ITEM_LOCATION_GRAF
	}
}
