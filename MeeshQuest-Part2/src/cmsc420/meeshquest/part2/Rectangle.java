package cmsc420.meeshquest.part2;

public class Rectangle {
	private int xlow;
	private int xhigh;
	private int ylow;
	private int yhigh;
	
	public Rectangle(int xlow, int ylow, int xhigh, int yhigh) {
		this.xlow = xlow;
		this.xhigh = xhigh;
		this.ylow = ylow;
		this.yhigh = yhigh;
	}
	
	public boolean contains(int x, int y) {
		if (x <= xhigh && x >= xlow && y <= yhigh && y >= ylow) {
			return true;
		} 
		else {
			return false;
		}
	}
	
	public boolean contains(Rectangle other) {
		if (other.xlow >= this.xlow && other.ylow >= this.ylow && 
			other.xhigh <= this.xhigh && other.yhigh <= this.yhigh) {
			return true;	
		}
		else {
			return false;
		}
	}
	
	public boolean isDisjointFrom(Rectangle other) {
		if (other.xlow > this.xhigh || other.ylow > this.yhigh ||
			other.xhigh < this.xlow || other.yhigh < this.ylow) {
			return true;	
		}
		else {
			return false;
		}
	}
	
	public int getCutDim() {
		if (Math.abs(xhigh-xlow) >= Math.abs(yhigh-ylow)) {
			return 0;
		}
		else {
			return 1;
		}
	}
	
	public Rectangle leftBottomPart(int cd, int x, int y) {
		if (cd == 0) {
			return new Rectangle(this.xlow, this.ylow, x, this.yhigh);
		}
		else {
			return new Rectangle(this.xlow, this.ylow, this.xhigh, y);
		}
	}
	public Rectangle rightTopPart(int cd, int x, int y) {
		if (cd == 0) {
			return new Rectangle(x, this.ylow, this.xhigh, this.yhigh);
		}
		else {
			return new Rectangle(this.xlow, y, this.xhigh, this.yhigh);
		}
	}
}
