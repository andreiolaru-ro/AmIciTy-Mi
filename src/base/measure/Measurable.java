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
package base.measure;

import java.util.Map;

/**
 * 
 * interface to implement some specific methods to have a generic agent
 * 
 * @param <T>
 */

public interface Measurable<T> {
	/**
	 * get one specific measure of an agent
	 * 
	 * @param measure
	 *            the name of the measure that we want to get
	 * @return the specific measure
	 */
	Measure<T> getMeasure(MeasureName measure);

	/**
	 * get all measures of an agent
	 * 
	 * @return a map which contains all the measures of the agent
	 */
	Map<MeasureName, Measure<T>> getAllMeasures();
}
