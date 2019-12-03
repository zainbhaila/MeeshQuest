package cmsc420.meeshquest.part2;

public class City implements Comparable<City> {
	public String name;
	public String x;
	public String y;
	public String color;
	public String radius;
	
	City(String name, String x, String y, String color, String radius) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.color = color;
		this.radius = radius;
	}

	@Override
	public int compareTo(City arg0) {
		return this.name.compareTo(((City) arg0).name);
	}

	public int compareToCoords(City arg0) {
		String x1 = this.x;
		String y1 = this.y;
		String x2 = arg0.x;
		String y2 = arg0.y;
		if (Float.parseFloat(x1) == Float.parseFloat(x2)) {
			return Float.compare(Float.parseFloat(y1), Float.parseFloat(y2));
		}
		else {
			return Float.compare(Float.parseFloat(x1), Float.parseFloat(x2));
		}
	}
}
