package scenario;

import java.util.ArrayList;
import java.util.List;

public interface Parameters {
	List<String> getValues();
	boolean isAllValueNull();
	ArrayList<String> getForeach();
	Integer getSelect();
}
