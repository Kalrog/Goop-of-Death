import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {
	private BufferedImage img;
	public int width,height;
	public Texture(String location){
		try {
			img = ImageIO.read(new File(location));
			width = img.getWidth();
			height = img.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public BufferedImage getLine(int x){
		return img.getSubimage(x, 0, 1, height);
	}
}
