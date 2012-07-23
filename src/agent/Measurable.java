package agent;

import java.util.Map;

public interface Measurable
{
	Measure getMeasure();
	Map<String,Measure> getAllMeasures();
}
