import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

public class Screen {
	double[] depthBuffer;
	public int width, height;
	public BufferedImage testtexture = null;

	public Screen(int w, int h) {
		width = w;
		height = h;
		depthBuffer = new double[width];
		try {
			testtexture = ImageIO.read(new File("resources/arrow.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void update(Camera cam, BufferedImage img, int[][] map, Texture[] textures, ArrayList<Sprite> sprites,
			int offset) {
		Graphics g = img.getGraphics();
		// commented out for fake multi level 3d
		//		g.setColor(Color.lightGray);
		//		g.fillRect(0, 0, img.getWidth(), img.getHeight() / 2);
		//		g.setColor(Color.GRAY);
		//		g.fillRect(0, img.getHeight() / 2, img.getWidth(), img.getHeight() / 2);

		for (int x = 0; x < width; x++) {
			// Direction of the ray sent out
			double rayXDir = cam.xDir + (2 * x / (double) width - 1) * cam.xPlane;
			double rayYDir = cam.yDir + (2 * x / (double) width - 1) * cam.yPlane;
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
			boolean inblock = map[mapX][mapY] > 0;
			boolean wasinblock = map[mapX][mapY] > 0;
			
			while (mapX < map.length && mapX > 0&& mapY < map[0].length && mapY > 0 && inblock) {
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
				inblock = map[mapX][mapY] > 0;
			}
			
			while (!hit && !inblock) {
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
			wallStart = (int) ((height / 2.0) - (wallLength / 2.0) + offset / perpWallDist);
			//if (wallStart < 0)
			//	wallStart = 0;
			// wallEnd = (int) ((height/2.0) - (wallLength/2.0));

			double wallX; //where exactly the wall was hit
			if (side == 0)
				wallX = rayY + perpWallDist * rayYDir;
			else
				wallX = rayX + perpWallDist * rayXDir;
			wallX -= Math.floor((wallX));
			int texturepos = (int) (wallX * textures[map[mapX][mapY] - 1].width);
			if (side == 1 && stepY < 0 || side == 0 && stepX > 0) {
				texturepos = textures[map[mapX][mapY] - 1].width - 1 - texturepos;
				/*if(texturepos < 0)
				  texturepos = 0;
				if(texturepos >= textures[map[mapX][mapY]-1].width-1)
				  texturepos = textures[map[mapX][mapY]-1].width-2;*/
			}
			g.drawImage(textures[map[mapX][mapY] - 1].getLine(texturepos), x, wallStart, 1, wallLength, null);
			if(wasinblock){
			}
			// save depth of the stripe in the depthbuffer
			depthBuffer[x] = perpWallDist;
		}

		if (sprites != null) {
			Collections.sort(sprites, Sprite.getComparator(cam));

			for (int i = 0; i < sprites.size(); i++) {
				// Calculate Sprite position relative to player
				double xSprite = sprites.get(i).x - cam.xPos;
				double ySprite = sprites.get(i).y - cam.yPos;

				//transform sprite with the inverse camera matrix
				// [ planeX   dirX ] -1                                       [ dirY      -dirX ]
				// [               ]       =  1/(planeX*dirY-dirX*planeY) *   [                 ]
				// [ planeY   dirY ]                                          [ -planeY  planeX ]

				// Calculate Determinant of the inverse Camera Matrix
				double invDet = 1.0 / (cam.xPlane * cam.yDir - cam.xDir * cam.yPlane);

				// Transform Sprite x and y coordinate
				double transformX = invDet * (cam.yDir * xSprite - cam.xDir * ySprite);
				double transformY = invDet * ((-cam.yPlane) * xSprite + cam.xPlane * ySprite);

				int spriteScreenX = (int) ((width / 2) * (1 + transformX / transformY));

				// height adjustment according to distance from player
				int zScreen = (int) (sprites.get(i).z / transformY);

				//calculate height of the sprite on screen
				int spriteHeight = Math.abs((int) (height * sprites.get(i).scaleV / transformY));
				//calculate highest pixel to fill in current stripe
				int drawStartY = -spriteHeight / 2 + height / 2 + zScreen;
				//calculate width of the sprite
				int spriteWidth = Math.abs((int) (height * sprites.get(i).scaleH / transformY));
				int drawStartX = -spriteWidth / 2 + spriteScreenX;
				if (drawStartX < 0)
					drawStartX = 0;
				int drawEndX = spriteWidth / 2 + spriteScreenX;
				if (drawEndX >= width)
					drawEndX = width - 1;

				for (int x = drawStartX; x < drawEndX; x++) {
					// Calculate x position on texture
					int texX = (int) (265 * (x - (-(spriteWidth / 2) + spriteScreenX)) * sprites.get(i).texture.width
							/ spriteWidth) / 265;

					/*if (texX < 0) {
						texX = 0;
					}
					
					if (texX > sprites.get(i).texture.width - 2) {
						texX = sprites.get(i).texture.width - 1;
					}*/

					if (transformY > 0 && x > 0 && x < width && transformY < depthBuffer[x])
						g.drawImage(sprites.get(i).texture.getLine(texX), x, drawStartY, 1, spriteHeight, null);

				}

			}
		}
	}

}
