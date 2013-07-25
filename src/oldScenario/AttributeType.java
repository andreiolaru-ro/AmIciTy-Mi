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
package oldScenario;


enum AttributeType {
	INTEGER,
	REAL,
	STRING;
	
	public static AttributeType parseType(String str) {
		if (str.equals("integer")) {
			return AttributeType.INTEGER;
		} else if (str.equals("real")) {
			return AttributeType.REAL;
		} else if (str.equals("string")) {
			return AttributeType.STRING;
		} else {
			assert false : str; // there are no other recognized types
			return null;
		}
	}
	
	@Override
	public String toString() {
		switch (this) {
		case INTEGER :
			return "integer";
		case REAL :
			return "real";
		case STRING :
			return "string";
		default :
			assert false : super.toString(); // should never reach this
			return null;
		}
	}
	
}
