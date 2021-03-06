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

import java.util.HashMap;
import java.util.Map;

import base.measure.Measure;
import base.measure.MeasureName;
import base.measure.AbstractMeasure.NumericMeasure;

/**
 * 
 * Class which define the location of an agent on the view
 * 
 */
public class Location implements Measure<Map<String, NumericMeasure>> {
	private double						x;
	private double						y;
	private Map<String, NumericMeasure>	coord;	// map which contains all the
												// informations about the
												// location of an agent

	@SuppressWarnings("hiding")
	public Location(double x, double y) {
		this.x = x;
		this.y = y;
		coord = new HashMap<String, NumericMeasure>();
		coord.put("x", new NumericMeasure(x, MeasureName.LOCATION));
		coord.put("y", new NumericMeasure(y, MeasureName.LOCATION));
	}

	public Location(Location lastLocation) {
		this.x = lastLocation.getX();
		this.y = lastLocation.getY();
		coord = new HashMap<String, NumericMeasure>();
		coord.put("x", new NumericMeasure(x, MeasureName.LOCATION));
		coord.put("y", new NumericMeasure(y, MeasureName.LOCATION));
	}

	public double getDistance(Location loc) {
		double dx = loc.x - x;
		double dy = loc.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		coord.remove("x");
		coord.put("x", new NumericMeasure(x, MeasureName.LOCATION));
		this.x = x;
	}

	public void setY(double y) {
		coord.remove("y");
		coord.put("y", new NumericMeasure(y, MeasureName.LOCATION));
		this.y = y;
	}

	@Override
	public String toString() {
		return x + "," + y;
	}

	@Override
	public Map<String, NumericMeasure> getValue() {
		return coord;
	}

	@Override
	public void setValue(Map<String, NumericMeasure> measure) throws Exception {
		this.coord = measure;
		this.x = coord.get("x").getValue().doubleValue();
		this.y = coord.get("y").getValue().doubleValue();
	}

	@Override
	public MeasureName getName() {
		return MeasureName.LOCATION;
	}
}
