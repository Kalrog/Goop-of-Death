import java.util.Comparator;

public class Sprite {
	Texture texture;
	double x, y, z, scaleH, scaleV;

	public Sprite(double x, double y, double z, Texture t) {
		this.texture = t;
		this.x = x;
		this.y = y;
		this.z = z;
		scaleH = 1.0;
		scaleV = 1.0;
	}

	public Sprite(double x, double y, double z, double scaleH, double scaleV, Texture t) {
		this.texture = t;
		this.x = x;
		this.y = y;
		this.z = z;
		this.scaleH = scaleH;
		this.scaleV = scaleV;
	}

	public static Comparator<Sprite> getComparator(Camera cam) {
		return new Comparator<Sprite>() {

			@Override
			public int compare(Sprite arg0, Sprite arg1) {
				return (int) Math.round(distance(arg1) - distance(arg0));
			}

			public double distance(Sprite s) {
				return Math.hypot(s.x - cam.xPos, s.y - cam.yPos);
			}
		};

	}
	
}
