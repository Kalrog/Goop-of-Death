import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Camera implements KeyListener, MouseListener {
	public double xPos, yPos, xDir, yDir, xPlane, yPlane;
	public boolean left, right, forward, back, focused;
	public final double MOVE_SPEED = .08;
	public final double ROTATION_SPEED = .0045;
	Robot rob = null;

	public Camera(double xPos, double yPos, double xDir, double yDir, double xPlane, double yPlane) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.xDir = xDir;
		this.yDir = yDir;
		this.xPlane = xPlane;
		this.yPlane = yPlane;
		focused = true;
		try {
			rob = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public void update(int[][] map, int winx, int winy) {
		if (focused) {
			int mousex =  winx + 200 - MouseInfo.getPointerInfo().getLocation().x;
			rob.mouseMove(200 + winx, 200 + winy);
			double oldxDir = xDir;
			xDir = xDir * Math.cos(ROTATION_SPEED * mousex) - yDir * Math.sin(ROTATION_SPEED * mousex);
			yDir = oldxDir * Math.sin(ROTATION_SPEED * mousex) + yDir * Math.cos(ROTATION_SPEED * mousex);
			double oldxPlane = xPlane;
			xPlane = xPlane * Math.cos(ROTATION_SPEED * mousex) - yPlane * Math.sin(ROTATION_SPEED * mousex);
			yPlane = oldxPlane * Math.sin(ROTATION_SPEED * mousex) + yPlane * Math.cos(ROTATION_SPEED * mousex);
			if (forward) {
				if (map[(int) (xPos + xDir * MOVE_SPEED)][(int) yPos] == 0) {
					xPos += xDir * MOVE_SPEED;
				}
				if (map[(int) xPos][(int) (yPos + yDir * MOVE_SPEED)] == 0)
					yPos += yDir * MOVE_SPEED;
			}
			if (back) {
				if (map[(int) (xPos - xDir * MOVE_SPEED)][(int) yPos] == 0)
					xPos -= xDir * MOVE_SPEED;
				if (map[(int) xPos][(int) (yPos - yDir * MOVE_SPEED)] == 0)
					yPos -= yDir * MOVE_SPEED;
			}
			if (right) {
				if (map[(int) (xPos - yDir * MOVE_SPEED)][(int) yPos] == 0) {
					xPos -= yDir * MOVE_SPEED;
				}
				if (map[(int) xPos][(int) (yPos + xDir * MOVE_SPEED)] == 0)
					yPos += xDir * MOVE_SPEED;
			}
			if (left) {
				if (map[(int) (xPos + yDir * MOVE_SPEED)][(int) yPos] == 0) {
					xPos += yDir * MOVE_SPEED;
				}
				if (map[(int) xPos][(int) (yPos - xDir * MOVE_SPEED)] == 0)
					yPos -= xDir * MOVE_SPEED;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		focused = true;
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			focused = false;
		} else if (e.getKeyCode() == KeyEvent.VK_W) {
			forward = true;
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			back = true;
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			left = true;
		} else if (e.getKeyCode() == KeyEvent.VK_A) {
			right = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			forward = false;
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			back = false;
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			left = false;
		} else if (e.getKeyCode() == KeyEvent.VK_A) {
			right = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
