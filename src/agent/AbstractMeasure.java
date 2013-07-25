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
 * abstract class which implements the interface Measure and which permits to
 * create the different type of measure like FloatMeasure for example
 * 
 * @param <T>
 */
public abstract class AbstractMeasure<T> implements Measure<T> {
	protected T				value;
	protected MeasureName	name;

	public AbstractMeasure(T value, MeasureName name) {
		this.value = value;
		this.name = name;
	}

	@Override
	public T getValue() {
		return this.value;
	}

	@SuppressWarnings({ "hiding", "unchecked" })
	@Override
	public void setValue(Object value) throws Exception {

		try {
			if (value.getClass() == this.value.getClass())
				this.value = (T) value;
			else
				throw new Exception("bad class");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public MeasureName getName() {
		return this.name;
	}

	public static class NumericMeasure extends AbstractMeasure<Double> {

		public NumericMeasure(Double value, MeasureName name) {
			super(value, name);
		}

		public NumericMeasure(double value, MeasureName name) {
			super(new Double(value), name);
		}
	}

	public static class FloatMeasure extends AbstractMeasure<Float> {

		public FloatMeasure(Float value, MeasureName name) {
			super(value, name);
		}

		public FloatMeasure(float value, MeasureName name) {
			super(new Float(value), name);
		}

	}

	/*
	 * public static class VectorMeasure extends AbstractMeasure<List<Measure>>
	 * { public VectorMeasure(List<Measure> value,NameMeasure name) {
	 * super(value, name); }
	 * 
	 * public Measure getValue(String name) { for (Measure measure : this.value)
	 * { if (measure.getName().equals(name)) return measure; } return null; }
	 * 
	 * public void addMeasure(Measure measure) { this.value.add(measure); }
	 * 
	 * }
	 */

}
