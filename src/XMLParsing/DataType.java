package XMLParsing;



enum DataType {
	INTEGER,
	REAL,
	STRING;
	
	public static DataType parseType(String str) {
		if (str.equals("integer")) {
			return DataType.INTEGER;
		} else if (str.equals("real")) {
			return DataType.REAL;
		} else if (str.equals("string")) {
			return DataType.STRING;
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
