package KCAAgent;

import java.util.Comparator;

public class DataContent
{
	public static class DataContentComparator implements Comparator<DataContent>
	{
		@Override
		public int compare(DataContent dc1, DataContent dc2)
		{
			return (dc1.id < dc2.id) ? -1 : 1;
		}
	}
	
	
	// there is no actual content.
	
	int id;
	
	public DataContent(@SuppressWarnings("hiding") int id)
	{
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataContent other = (DataContent) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return "D" + id;
	}
}
