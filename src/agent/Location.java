package agent;

public class Location {
	private double x;
	private double y;
	
	@SuppressWarnings("hiding")
	public Location(double x, double y) {
		this.x = x;
		this.y = y;
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
	
	@Override
	public String toString()
	{
		return x + "," + y;
	}
}
