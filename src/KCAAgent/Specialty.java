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
package KCAAgent;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import base.measure.Measure;
import base.measure.MeasureName;
import base.measure.AbstractMeasure.NumericMeasure;
import KCAAgent.Logix.Domain;

public class Specialty implements Measure<Map<Domain, NumericMeasure>>// NumericMeasure
																		// now
{
	private Map<Domain, NumericMeasure>	specMap	= new HashMap<Domain, NumericMeasure>();
	private MeasureName					name;

	public Specialty() {
		this(0, 0, 0);
		name = MeasureName.SPECIALTY;
	}

	public Specialty(double a, double b, double c) {
		specMap.put(Domain.A, new NumericMeasure(a, MeasureName.SPECIALTY));
		specMap.put(Domain.B, new NumericMeasure(b, MeasureName.SPECIALTY));
		specMap.put(Domain.C, new NumericMeasure(c, MeasureName.SPECIALTY));
		Logix.normSpecialty(specMap);
	}

	public Specialty(Map<Domain, NumericMeasure> map) {
		specMap = Logix.normSpecialty(map);
	}

	@Override
	public String toString() {
		String ret = null;
		for (Domain dom : Domain.values())
			if (specMap.containsKey(dom) && (specMap.get(dom).getValue().doubleValue() > 0))
				ret = ((ret == null) ? "" : (ret + " ")) + dom + ":"
						+ (int) (100 * specMap.get(dom).getValue().doubleValue());
		return ret;
	}

	public double getValue(Domain dom) {
		if (specMap.containsKey(dom)) {
			return specMap.get(dom).getValue().doubleValue();
		}
		return 0.0;
	}

	// the color of the data, assumes there are exactly 3 domains
	public Color getColor() {
		double r = getValue(Domain.A);
		double g = getValue(Domain.B);
		double b = getValue(Domain.C);
		return new Color((float) r, (float) g, (float) b);
	}

	public Specialty set(Specialty spec) {
		for (Domain dom : Domain.values())
			this.specMap.put(dom, new NumericMeasure(spec.getValue(dom), MeasureName.SPECIALTY));
		return this;
	}

	@Override
	public Map<Domain, NumericMeasure> getValue() {
		return this.specMap;
	}

	@Override
	public MeasureName getName() {
		return this.name;
	}

	@Override
	public void setValue(Map<Domain, NumericMeasure> value) {
		this.specMap = value;
	}
}
