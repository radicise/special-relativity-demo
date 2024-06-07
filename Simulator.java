import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
public class Simulator {
    public static final double SCALING = 200.0d;
    public static volatile Matrix totalTransform;
    public static volatile double time = 0.0d;
    public static volatile Matrix rotator;
    public static volatile Matrix derotator;
    public static final double timeStep = 0.05d;
    public static void main(String[] argv) {
	totalTransform = Matrix.identity(4);
	rotator = Matrix.identity(4);
	derotator = Matrix.identity(4);
	Display displ = new Display(512, 512, 256, 256, 10000.0d * (1.0d / Simulator.SCALING), new Universe(), 2.5d * (SCALING / 100.0d));
	JFrame frm = new JFrame();
	frm.addKeyListener(new KeyHandler());
	frm.add(displ);
	frm.setSize(512 + 50, 512 + 150);
	frm.setVisible(true);
	Graphics gr = displ.getGraphics();
	long mn = (long) (timeStep * 1000000000.0d);
	double ts = ((double) mn) / 1000000000.0d;
	int nanos = (int) (mn % 1000000L);
	mn /= 1000000;
	try {
	    Matrix timeTransform = new Matrix(new double[][]{new double[]{-ts}, new double[]{0.0d}, new double[]{0.0d}, new double[]{0.0d}});
	    Matrix shft;
	    double[][] s = new double[4][4];
	    s[0][0] = 1.0d;
	    s[1][1] = 1.0d;
	    s[2][2] = 1.0d;
	    s[3][3] = 1.0d;
	    Matrix sm = new Matrix(s);
	    while (true) {
                shft = totalTransform.transform(timeTransform);
		s[0][3] = shft.getEntry(0, 0);
		s[1][3] = shft.getEntry(1, 0);
		s[2][3] = shft.getEntry(2, 0);
		s[3][3] = 1.0d + shft.getEntry(3, 0);
		totalTransform = totalTransform.transform(sm);
		System.out.println("shft:");
		System.out.println(shft);
		System.out.println("totalTransform:");
		System.out.println(totalTransform);
		System.out.println("sm:");
		System.out.println(sm);
		System.out.println("rotator:");
		System.out.println(rotator);
		displ.paint(gr);
		Thread.sleep(mn, nanos);
	    }
	} catch (InterruptedException E) {
	    System.exit(1);
	}
    }
}
class Vehicle {
    static final double SIZE = 0.001d * Simulator.SCALING;
    WorldLine nose;
    WorldLine wing1;
    WorldLine wing2;
    WorldLine saggitalEnding;
    Vehicle() {
	nose = new WorldLine();
	nose.addEvent(new Matrix(new double[][]{new double[]{0.0d},
		    new double[]{0.0d},
		    new double[]{SIZE},
		    new double[]{1.0d}
		}));
	wing1 = new WorldLine();
	wing1.addEvent(new Matrix(new double[][]{new double[]{0.0d},
		    new double[]{SIZE},
		    new double[]{-SIZE},
		    new double[]{1.0d}
		}));
	wing2 = new WorldLine();
	wing2.addEvent(new Matrix(new double[][]{new double[]{0.0d},
		    new double[]{-SIZE},
		    new double[]{-SIZE},
		    new double[]{1.0d}
		}));
	saggitalEnding = new WorldLine();
	saggitalEnding.addEvent(new Matrix(new double[][]{new double[]{0.0d},
		    new double[]{0.0d},
		    new double[]{0.0d},
		    new double[]{1.0d}
		}));
    }
    Vehicle(double time, double x, double y) {
	nose = new WorldLine();
	nose.addEvent(new Matrix(new double[][]{new double[]{0.0d},
		    new double[]{x + 0.0d},
		    new double[]{y + SIZE},
		    new double[]{1.0d}
		}));
	nose.addEvent(new Matrix(new double[][]{new double[]{time},
		    new double[]{x + 0.0d},
		    new double[]{y + SIZE},
		    new double[]{1.0d}
		}));
	wing1 = new WorldLine();
	wing1.addEvent(new Matrix(new double[][]{new double[]{0.0d},
		    new double[]{x + SIZE},
		    new double[]{y - SIZE},
		    new double[]{1.0d}
		}));
	wing1.addEvent(new Matrix(new double[][]{new double[]{time},
		    new double[]{x + SIZE},
		    new double[]{y - SIZE},
		    new double[]{1.0d}
		}));
	
	wing2 = new WorldLine();
	wing2.addEvent(new Matrix(new double[][]{new double[]{0.0d},
		    new double[]{x - SIZE},
		    new double[]{y - SIZE},
		    new double[]{1.0d}
		}));
	wing2.addEvent(new Matrix(new double[][]{new double[]{time},
		    new double[]{x - SIZE},
		    new double[]{y - SIZE},
		    new double[]{1.0d}
		}));
	
	saggitalEnding = new WorldLine();
	saggitalEnding.addEvent(new Matrix(new double[][]{new double[]{0.0d},
		    new double[]{x + 0.0d},
		    new double[]{y + 0.0d},
		    new double[]{1.0d}
		}));
	saggitalEnding.addEvent(new Matrix(new double[][]{new double[]{time},
		    new double[]{x + 0.0d},
		    new double[]{y + 0.0d},
		    new double[]{1.0d}
		}));
    }
}
class Universe {
    ArrayList<Vehicle> vehicles;
    Universe() {
	vehicles = new ArrayList<Vehicle>();
	for (int x = (-20); x < 20; x++) {
		for (int y = (-20); y < 20; y++) {
			vehicles.add(new Vehicle(40763, 2.0d * (double) x, 2.0d * (double) y));
		}
	}
    }
}
class KeyHandler implements KeyListener {
    public KeyHandler() {
    }
    public void keyPressed(KeyEvent ke) {
	// todo: hold down
	switch (ke.getKeyChar()) {
	case ('w'):
	    Simulator.totalTransform = Simulator.derotator.transform(Transformations.boostYInv).transform(Simulator.rotator.transform(Simulator.totalTransform));
	    break;
	case ('s'):
	    Simulator.totalTransform = Simulator.derotator.transform(Transformations.boostY).transform(Simulator.rotator.transform(Simulator.totalTransform));
	    break;
	/*
	case 'a':
	    Simulator.totalTransform = Transformations.boostX.transform(Simulator.totalTransform);
	    break;
	case 'd':
	    Simulator.totalTransform = Transformations.boostXInv.transform(Simulator.totalTransform);
	    break;
	*/
	case ('d'):
	    Simulator.rotator = Transformations.rotateClock.transform(Simulator.rotator);
            Simulator.derotator = Transformations.rotateCounterClock.transform(Simulator.derotator);

	    break;
	case ('a'):
	    Simulator.rotator = Transformations.rotateCounterClock.transform(Simulator.rotator);
            Simulator.derotator = Transformations.rotateClock.transform(Simulator.derotator);
	    break;
	}
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
	    Matrix tt = Simulator.totalTransform;
	    double time = Simulator.time;
	    Matrix pos1 = v.nose.resolvePosition(tt, time);
	    Matrix pos2 = v.wing1.resolvePosition(tt, time);
	    Matrix pos3 = v.saggitalEnding.resolvePosition(tt, time);
	    Matrix pos4 = v.wing2.resolvePosition(tt, time);
	    if (pos1 != null) {
		    pos1 = Simulator.rotator.transform(pos1);
            }
	    if (pos2 != null) {
		    pos2 = Simulator.rotator.transform(pos2);
            }
	    if (pos3 != null) {
		    pos3 = Simulator.rotator.transform(pos3);
            }
	    if (pos4 != null) {
		    pos4 = Simulator.rotator.transform(pos4);
            }
	    if ((pos1 != null) && (pos2 != null)) {
		g.drawLine(middleX + (int) (pos1.getEntry(1, 0) * scale), middleY - (int) (pos1.getEntry(2, 0) * scale), middleX + (int) (pos2.getEntry(1, 0) * scale), middleY - (int) (pos2.getEntry(2, 0) * scale));
	    }
	    if ((pos3 != null) && (pos2 != null)) {
		g.drawLine(middleX + (int) (pos2.getEntry(1, 0) * scale), middleY - (int) (pos2.getEntry(2, 0) * scale), middleX + (int) (pos3.getEntry(1, 0) * scale), middleY - (int) (pos3.getEntry(2, 0) * scale));
	    }
	    if ((pos3 != null) && (pos4 != null)) {
		g.drawLine(middleX + (int) (pos3.getEntry(1, 0) * scale), middleY - (int) (pos3.getEntry(2, 0) * scale), middleX + (int) (pos4.getEntry(1, 0) * scale), middleY - (int) (pos4.getEntry(2, 0) * scale));
	    }
	    if ((pos1 != null) && (pos4 != null)) {
		g.drawLine(middleX + (int) (pos4.getEntry(1, 0) * scale), middleY - (int) (pos4.getEntry(2, 0) * scale), middleX + (int) (pos1.getEntry(1, 0) * scale), middleY - (int) (pos1.getEntry(2, 0) * scale));
	    }
	    if (pos1 != null) {
		g.drawRect(middleX + (int) (pos1.getEntry(1, 0) * scale), middleY - (int) (pos1.getEntry(2, 0) * scale), 1, 1);
	    }
	    if (pos2 != null) {
		g.drawRect(middleX + (int) (pos2.getEntry(1, 0) * scale), middleY - (int) (pos2.getEntry(2, 0) * scale), 1, 1);
	    }
	    if (pos3 != null) {
		g.drawRect(middleX + (int) (pos3.getEntry(1, 0) * scale), middleY - (int) (pos3.getEntry(2, 0) * scale), 1, 1);
	    }
	    if (pos4 != null) {
		g.drawRect(middleX + (int) (pos4.getEntry(1, 0) * scale), middleY - (int) (pos4.getEntry(2, 0) * scale), 1, 1);
	    }



	g.drawLine(middleX + (int) (0.0d * scale), middleY - (int) (Vehicle.SIZE * scale), middleX + (int) (Vehicle.SIZE * scale), middleY - (int) (-Vehicle.SIZE * scale));
	g.drawLine(middleX + (int) (Vehicle.SIZE * scale), middleY - (int) (-Vehicle.SIZE * scale), middleX + (int) (0.0d * scale), middleY - (int) (0.0d * scale));
	g.drawLine(middleX + (int) (0.0d * scale), middleY - (int) (0.0d * scale), middleX + (int) (-Vehicle.SIZE * scale), middleY - (int) (-Vehicle.SIZE * scale));
	g.drawLine(middleX + (int) (-Vehicle.SIZE * scale), middleY - (int) (-Vehicle.SIZE * scale), middleX + (int) (0.0d * scale), middleY - (int) (Vehicle.SIZE * scale));
	g.drawRect(middleX + (int) (0.0d * scale), middleY - (int) (Vehicle.SIZE * scale), 1, 1);
	g.drawRect(middleX + (int) (Vehicle.SIZE * scale), middleY - (int) (-Vehicle.SIZE * scale), 1, 1);
	g.drawRect(middleX + (int) (0.0d * scale), middleY - (int) (0.0d * scale), 1, 1);
	g.drawRect(middleX + (int) (-Vehicle.SIZE * scale), middleY - (int) (-Vehicle.SIZE * scale), 1, 1);
	}
    }
}
