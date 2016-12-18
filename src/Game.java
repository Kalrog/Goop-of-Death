import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Game extends JFrame implements Runnable {
	public static final String[] TEXPATHS = 
			new String[]{"resources/arrow.png",
					"resources/creepy.png",
					"resources/testtexture.png"};
	Texture[] textures;
	boolean running;
	BufferedImage image;
	int[][] world = new int[][] { { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
								  { 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 }, 
								  { 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1 },
								  { 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1 }, 
								  { 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1 },
								  { 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1 }, 
								  { 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1 },
								  { 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 }, 
								  { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1 },
								  { 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1 }, 
								  { 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1 },
								  { 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1 }, 
								  { 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1 },
								  { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, 
								  { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };
	Thread thread;
	Camera cam;
	Screen screen;
	boolean focused = false;

	public Game() {
		thread = new Thread(this);
		image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
		cam = new Camera(2,2,-1,0,0,0.8);
		screen = new Screen(world,640,480);
		textures = new Texture[TEXPATHS.length];
		System.out.println("Loading Textures");
		for(int i = 0; i < TEXPATHS.length;i++){
			textures[i] = new Texture(TEXPATHS[i]);
			System.out.println("Done loading " + TEXPATHS[i]);
		}
		addKeyListener(cam);
		addMouseListener(cam);
		setSize(640, 480);
		setResizable(false);
		setLocation(new Point(50,50));
		setTitle("Goop of Death");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.black);
		setVisible(true);
		createBufferStrategy(3);
		start();
	}

	private synchronized void start() {
		running = true;
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Game game = new Game();
	}

	@Override
	public void run() {
		while (running) {
			cam.update(world,getX(),getY());
			screen.update(cam, image);
			render();
			if(focused != cam.focused){
				focused = cam.focused;
				if(cam.focused){
					BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
					Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
					    cursorImg, new Point(0, 0), "blank cursor");
					getContentPane().setCursor(blankCursor);
				}else{
					getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void render() {
		Graphics g = getBufferStrategy().getDrawGraphics(); 
		g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		g.setColor(Color.BLACK);
		g.setFont(Font.getFont("Arial"));
		g.drawString("X: "+ cam.xPos + " Y: " + cam.yPos , 5, 15); 
		g.drawString("X: "+ cam.xDir + " Y: " + cam.yDir , 5, 25);
		getBufferStrategy().show();
	}

}
