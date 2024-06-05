import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
public class Simulator {
	public static void main(String[] argv) {
		Display displ = new Display(512, 512, 256, 256, 100.0d, new Universe(), 2.5d);
		JFrame frm = new JFrame();
		frm.addKeyListener(new KeyHandler());
		frm.add(displ);
		frm.setSize(512 + 50, 512 + 150);
		frm.setVisible(true);
		Graphics gr = displ.getGraphics();
		while (true) {
			//displ.paint(gr);
		}
	}
}
class Vehicle {
	WorldLine nose;
	WorldLine wing1;
	WorldLine wing2;
	WorldLine saggitalEnding;
	Vehicle() {
		nose = new WorldLine();
		nose.addEvent(new Matrix(new double[][]{new double[]{0.0d},
			new double[]{0.0d},
			new double[]{0.1d},
			new double[]{1.0d}
		}));
		wing1 = new WorldLine();
		wing1.addEvent(new Matrix(new double[][]{new double[]{0.0d},
			new double[]{0.1d},
			new double[]{-0.1d},
			new double[]{1.0d}
		}));
		wing2 = new WorldLine();
		wing2.addEvent(new Matrix(new double[][]{new double[]{0.0d},
			new double[]{-0.1d},
			new double[]{-0.1d},
			new double[]{1.0d}
		}));
		saggitalEnding = new WorldLine();
		saggitalEnding.addEvent(new Matrix(new double[][]{new double[]{0.0d},
			new double[]{0.0d},
			new double[]{0.0d},
			new double[]{1.0d}
		}));
	}
}
class Universe {
	ArrayList<Vehicle> vehicles;
	Universe() {
		vehicles = new ArrayList<Vehicle>();
		vehicles.add(new Vehicle());
	}
}
class KeyHandler implements KeyListener {
	KeyHandler() {
	}
	public void keyPressed(KeyEvent ke) {
		return;
	}
	public void keyReleased(KeyEvent ke) {
		return;
	}
	public void keyTyped(KeyEvent ke) {
		return;
	}
}
class Display extends Canvas {
	int width;
	int height;
	int middleX;
	int middleY;
	double scale;
	double viewDistance;
	Universe universe;
	static final Color BLACK = new Color(0, 0, 0);
	static final Color WHITE = new Color(255, 255, 255);
	Display(int x, int y, int mX, int mY, double s, Universe u, double v) {
		width = x;
		height = y;
		scale = s;
		middleX = mX;
		middleY = mY;
		universe = u;
		viewDistance = v;
	}
	public void paint(Graphics g) {
		g.setColor(BLACK);
		g.fillRect(middleX - (int) (viewDistance * scale), middleY - (int) (viewDistance * scale), (int) (viewDistance * scale * 2), (int) (viewDistance * scale * 2));
		g.setColor(WHITE);
		for (Vehicle v : universe.vehicles) {
			// TODO Transformations
			Matrix pos1 = v.nose.events.get(0);
			Matrix pos2 = v.wing1.events.get(0);
			Matrix pos3 = v.saggitalEnding.events.get(0);
			Matrix pos4 = v.wing2.events.get(0);
			g.drawLine(middleX + (int) (pos1.getEntry(1, 0) * scale), middleY + (int) (pos1.getEntry(2, 0) * scale), middleX + (int) (pos2.getEntry(1, 0) * scale), middleY + (int) (pos2.getEntry(2, 0) * scale));
			g.drawLine(middleX + (int) (pos2.getEntry(1, 0) * scale), middleY + (int) (pos2.getEntry(2, 0) * scale), middleX + (int) (pos3.getEntry(1, 0) * scale), middleY + (int) (pos3.getEntry(2, 0) * scale));
			g.drawLine(middleX + (int) (pos3.getEntry(1, 0) * scale), middleY + (int) (pos3.getEntry(2, 0) * scale), middleX + (int) (pos4.getEntry(1, 0) * scale), middleY + (int) (pos4.getEntry(2, 0) * scale));
			g.drawLine(middleX + (int) (pos4.getEntry(1, 0) * scale), middleY + (int) (pos4.getEntry(2, 0) * scale), middleX + (int) (pos1.getEntry(1, 0) * scale), middleY + (int) (pos1.getEntry(2, 0) * scale));
		}
	}
}
