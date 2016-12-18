import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Screen {
	public int[][] map;
	public int width, height;
	public BufferedImage testtexture = null;

	public Screen(int[][] m, int w, int h) {
		map = m;
		width = w;
		height = h;
		try {
			testtexture = ImageIO.read(new File("resources/arrow.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void update(Camera cam, BufferedImage img) {
		Graphics g = img.getGraphics();
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, img.getWidth(), img.getHeight() / 2);
		g.setColor(Color.GRAY);
		g.fillRect(0, img.getHeight() / 2, img.getWidth(), img.getHeight() / 2);

		for (int i = 0; i < width; i++) {
			// Direction of the ray sent out
			double rayXDir = cam.xDir + (2 * i / (double)width - 1) * cam.xPlane;
			double rayYDir = cam.yDir + (2 * i / (double)width - 1) * cam.yPlane;
			// Position of the ray
			double rayX = cam.xPos;
			double rayY = cam.yPos;
			// Current Map Box
			int mapX = (int) rayX;
			int mapY = (int) rayY;
			// Distance from current position to next x or y side
			double sideDistX;
			double sideDistY;
			// Length of a ray going from one x or y side to the next
			// Length of ray going a x distance of 1
			double deltaDistX = Math.sqrt(1 + (rayYDir * rayYDir) / (rayXDir * rayXDir));
			// Length of ray going a y distance of 1
			double deltaDistY = Math.sqrt((rayXDir * rayXDir) / (rayYDir * rayYDir) + 1);
			// Direction of steps in x and y (positive: 1 or negative: -1)
			int stepX;
			int stepY;

			boolean hit = false; // was a wall hit?
			int side = 0; // what side was the wall hit on

			if (rayXDir < 0) {
				// ray is traveling in the negative x direction
				stepX = -1;
				sideDistX = (rayX - mapX) * deltaDistX;
			} else {
				// ray is traveling in the positive x direction
				stepX = 1;
				sideDistX = (mapX + 1 - rayX) * deltaDistX;
			}

			if (rayYDir < 0) {
				// ray is traveling in the negative y direction
				stepY = -1;
				sideDistY = (rayY - mapY) * deltaDistY;
			} else {
				// ray is traveling in the positive y direction
				stepY = 1;
				sideDistY = (mapY + 1 - rayY) * deltaDistY;
			}

			while (!hit) {
				if (sideDistX < sideDistY) {
					// Next X side (sideDistX) is closer than next Y side -> Go
					// one X step
					// Next time the X side (sideDistX) will be one
					// X-step-length (deltaDistX) further away then it is right
					// now
					sideDistX += deltaDistX;
					// Also go one further on the map
					mapX += stepX;
					// Also if you hit a wall now you will have hit it's
					// horizontal side
					side = 0;
				} else {
					// Next Y side (sideDistY) is closer than next X side -> Go
					// one Y step
					// Next time the Y side (sideDistY) will be one
					// Y-step-length (deltaDistY) further away then it is right
					// now
					sideDistY += deltaDistY;
					// Also go one further on the map
					mapY += stepY;
					// Also if you hit a wall now you will have hit it's
					// vertical side
					side = 1;
				}
				hit = map[mapX][mapY] > 0;
			}
			double perpWallDist;
			if (side == 0)
				perpWallDist = (mapX - rayX + (1 - stepX) / 2) / rayXDir;
			else
				perpWallDist = (mapY - rayY + (1 - stepY) / 2) / rayYDir;
			
			int wallLength, wallStart, wallEnd;

			if (perpWallDist > 0) {
				wallLength = (int) (height / perpWallDist);
			} else {
				wallLength = height;
			}
			wallStart = (int) ((height / 2.0) - (wallLength / 2.0));
			//if (wallStart < 0)
			//	wallStart = 0;
			// wallEnd = (int) ((height/2.0) - (wallLength/2.0));
			
			double wallX; //where exactly the wall was hit
		      if (side == 0) wallX = rayY + perpWallDist * rayYDir;
		      else           wallX = rayX + perpWallDist * rayXDir;
		      wallX -= Math.floor((wallX));
		      int texturepos = (int) (wallX * testtexture.getWidth());
		      if(side == 1 && stepY < 0 || side == 0 && stepX > 0){
		    	  texturepos = testtexture.getWidth()- 1 - texturepos;
		    	  if(texturepos < 0)
		    		  texturepos = 0;
		      }
			g.drawImage(testtexture.getSubimage(texturepos, 0, 1, testtexture.getHeight()), i, wallStart,1,wallLength, null);
		}
	}
}
