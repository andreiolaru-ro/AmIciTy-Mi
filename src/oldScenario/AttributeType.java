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
