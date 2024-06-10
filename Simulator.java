import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.stream.IntStream;
public class Simulator {
    public static final double SCALING = 100.0d;
    public static volatile Matrix totalTransform;
    public static volatile Matrix totalTransformInv;
    public static volatile double time = 0.0d;
    public static volatile Matrix rotator;
    public static volatile Matrix derotator;
    //public static final double timeStep = 0.2d;
    public static final double endOfTime = 10000.0d;
    public static Vehicle currentVehicle;
    public static Universe universe;
    public static volatile long observerTime;
    public static void main(String[] argv) {
	totalTransform = Matrix.identity(4);
	totalTransformInv = Matrix.identity(4);
	rotator = Matrix.identity(4);
	derotator = Matrix.identity(4);
	Display displ = new Display(512, 512, 256, 256, 10000.0d * (1.0d / Simulator.SCALING), universe = new Universe(), 2.56d * (SCALING / 100.0d));
	JFrame frm = new JFrame();
	frm.add(displ);
	// frm.setSize(512, 512+10);
	frm.pack();
	frm.setVisible(true);
	frm.setFocusable(true);
	displ.setFocusable(true);
	displ.addKeyListener(new KeyHandler());
	displ.requestFocus();
	displ.createBufferStrategy(2);
	BufferStrategy bufferStrategy = displ.getBufferStrategy();
	Graphics gr = bufferStrategy.getDrawGraphics();
	//long mn = (long) (timeStep * 1000000000.0d);
	//double ts = ((double) mn) / 1000000000.0d;
	//int nanos = (int) (mn % 1000000L);
	//mn /= 1000000;
	currentVehicle = new Vehicle();
	try {
	    double[] elapsed = new double[]{0.0d};
	    Matrix timeTransform = new Matrix(new double[][]{elapsed, new double[]{0.0d}, new double[]{0.0d}, new double[]{0.0d}});
	    Matrix shft;
	    double[][] s = new double[4][4];
	    s[0][0] = 1.0d;
	    s[1][1] = 1.0d;
	    s[2][2] = 1.0d;
	    s[3][3] = 1.0d;
	    Matrix sm = new Matrix(s);
	    long t1 = System.currentTimeMillis();
	    long t2;
	    while (true) {
                shft = totalTransformInv.transform(timeTransform);
		s[0][3] = shft.getEntry(0, 0);
		s[1][3] = shft.getEntry(1, 0);
		s[2][3] = shft.getEntry(2, 0);
		//s[3][3] = 1.0d + shft.getEntry(3, 0);
		totalTransform = totalTransform.transform(sm);
		s[0][3] = -(s[0][3]);
		s[1][3] = -(s[1][3]);
		s[2][3] = -(s[2][3]);
		//s[3][3] = 2.0d - s[3][3];
		totalTransformInv = sm.transform(totalTransformInv);
		/*
		System.out.println("shft:");
		System.out.println(shft);
		
		System.out.println("totalTransform:");
		System.out.println(totalTransform);
		System.out.println("totalTransformInv:");
		System.out.println(totalTransformInv);

		Matrix fn = totalTransformInv.transform(Vehicle.pts[0]);
		Matrix fb = totalTransformInv.transform(Vehicle.pts[3]);
		System.out.println(fb.subtractFrom(fn));

		System.out.println(totalTransform.transform(totalTransformInv));

		
		System.out.println("sm:");
		System.out.println(sm);
		System.out.println("rotator:");
		System.out.println(rotator);
		*/
		displ.paint(gr);
		bufferStrategy.show();
		//Thread.sleep(mn, nanos);
		t2 = System.currentTimeMillis();
		elapsed[0] = ((double) (t1 - t2)) / 1000.0d;
		// System.out.println(elapsed[0]);
		observerTime += (t2 - t1);
		t1 = t2;
	    }
	} catch (Exception E) {
	    System.err.println(E);
	    E.printStackTrace();
	    System.exit(1);
	}
    }
}
class Vehicle {
    static final double SIZE = 0.001d * Simulator.SCALING;
    static Matrix[] pts;
    static Matrix ptsMatrix;
    static Matrix[] ptst;
    static Matrix[] ptstt;
    static {
	pts = new Matrix[16];
	double[][] ptsMatrixInner = new double[4][pts.length];
	ptst = new Matrix[pts.length];
	ptstt = new Matrix[pts.length];
	    for (int i = 0; i < pts.length; i++) {
		pts[i] = new Matrix(new double[][] {
			new double[] {0.0d},
			new double[] {Math.cos(((double) i) * (Math.PI / 8.0d)) * SIZE},
			new double[] {Math.sin(((double) i) * (Math.PI / 8.0d)) * SIZE},
			new double[] {1.0d}
		    });
		ptsMatrixInner[0][i] = 0.0d;
		ptsMatrixInner[1][i] = Math.cos(((double) i) * (Math.PI / 8.0d)) * SIZE;
		ptsMatrixInner[2][i] = Math.sin(((double) i) * (Math.PI / 8.0d)) * SIZE;
		ptsMatrixInner[3][i] = 1.0d;
		ptst[i] = new Matrix(new double[][] {
			new double[] {0.0d},
			new double[] {Math.cos(((double) i) * (Math.PI / 8.0d)) * SIZE},
			new double[] {Math.sin(((double) i) * (Math.PI / 8.0d)) * SIZE},
			new double[] {1.0d}
		    });
		ptstt[i] = new Matrix(new double[][] {
			new double[] {Simulator.endOfTime},
			new double[] {Math.cos(((double) i) * (Math.PI / 8.0d)) * SIZE},
			new double[] {Math.sin(((double) i) * (Math.PI / 8.0d)) * SIZE},
			new double[] {1.0d}
		    });
            }
	    ptsMatrix = new Matrix(ptsMatrixInner);
    }
    WorldLine[] lines;
    Color color;
    Vehicle() {
    	lines = new WorldLine[pts.length];
	for (int i = 0; i < lines.length; i++) {
	    lines[i] = new WorldLine();
	    lines[i].addEvent(pts[i]);
	}
	color = new Color(255, 0, 0);
    }
    Vehicle(double x, double y) {
	Matrix adjustment = new Matrix(new double[][]{new double[]{0.0d}, new double[]{x}, new double[]{y}, new double[]{0.0d}});
	lines = new WorldLine[pts.length];
	for (int i = 0; i < pts.length; i++) {
		lines[i] = new WorldLine();
		lines[i].addEvent(Matrix.add(ptst[i], adjustment));
		lines[i].addEvent(Matrix.add(ptstt[i], adjustment));
	}
	color = new Color(156 + (int) (Math.random() * 100.0d), 156 + (int) (Math.random() * 100.0d), 156 + (int) (Math.random() * 100.0d));
    }
    void addEvent(Matrix transformation) {
	for (int i = 0; i < pts.length; i++) {
	    lines[i].addEvent(transformation.transform(pts[i]));
	}
    }
}
class Universe {
    ArrayList<Vehicle> vehicles;
    Universe() {
	vehicles = new ArrayList<Vehicle>();
	for (int i = 0; i < 80; i++) {	
	    vehicles.add(new Vehicle(20.0d * (Math.random() - 0.5d) * (Simulator.SCALING / 200.0d), 20.0d * (Math.random() - 0.5d) * (Simulator.SCALING / 200.0d)));
	}
    }
}
class KeyHandler implements KeyListener {
    public KeyHandler() {
    }
    public void keyPressed(KeyEvent ke) {
	// todo: hold down
	switch (ke.getKeyChar()) {// TODO Concurrency
	case ('w'):
	    Simulator.currentVehicle.addEvent(Simulator.totalTransformInv);
	    Simulator.totalTransform = Simulator.derotator.transform(Transformations.boostY).transform(Simulator.rotator.transform(Simulator.totalTransform));
	    Simulator.totalTransformInv = Simulator.totalTransformInv.transform(Simulator.derotator).transform(Transformations.boostYInv).transform(Simulator.rotator);
	    break;
	case ('s'):
	    Simulator.currentVehicle.addEvent(Simulator.totalTransformInv);
	    Simulator.totalTransform = Simulator.derotator.transform(Transformations.boostYInv).transform(Simulator.rotator.transform(Simulator.totalTransform));
	    Simulator.totalTransformInv = Simulator.totalTransformInv.transform(Simulator.derotator).transform(Transformations.boostY).transform(Simulator.rotator);
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
	case ('t'):
	    Simulator.currentVehicle.addEvent(Simulator.totalTransformInv);
	    Simulator.rotator = Matrix.identity(4);
	    Simulator.derotator = Matrix.identity(4);
	    Simulator.totalTransform = Matrix.identity(4);
	    Simulator.totalTransformInv = Matrix.identity(4);
	    Simulator.universe.vehicles.add(Simulator.currentVehicle);
	    Simulator.currentVehicle = new Vehicle();
	    Simulator.observerTime = 0l;
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
    Matrix toScreen;
    double viewDistance;
    Universe universe;
    static final Color BLACK = new Color(0, 0, 0);
    static final Color WHITE = new Color(255, 255, 255);
    Display(int x, int y, int mX, int mY, double s, Universe u, double v) {
	width = x;
	height = y;
	setPreferredSize(new Dimension (width, height));
	scale = s;
	middleX = mX;
	middleY = mY;
	toScreen = new Matrix(new double[][] {
		new double[] {1.0d, 0.0d, 0.0d, 0.0d},
		new double[] {0.0d, scale, 0.0d, middleX},
		new double[] {0.0d, 0.0d, -scale, middleY},
	    });
	universe = u;
	viewDistance = v;
    }
    public void paint(Graphics g) {
	g.setColor(BLACK);
	g.fillRect(middleX - (int) (viewDistance * scale), middleY - (int) (viewDistance * scale), (int) (viewDistance * scale * 2), (int) (viewDistance * scale * 2));
	Matrix rot = Simulator.rotator;
	g.setColor(WHITE);
	Long ot = Simulator.observerTime;
	g.drawString("Observer time: " + Long.toString(ot / 1000l) + "." + Long.toString(ot % 1000l) + "seconds", 20, 40);
	g.drawString("Buoy time: " + Double.toString(Simulator.totalTransformInv.getEntry(0, 3)) + "seconds", 20, 80);
	int m = universe.vehicles.size();
	Matrix tt = toScreen.transform(rot.transform(Simulator.totalTransform));
	int[][] centerVehicle = toScreen.transform(Vehicle.ptsMatrix).toIntArray();
	g.drawPolygon(centerVehicle[1], centerVehicle[2], centerVehicle[1].length);
	int[][][] pos = new int[m][2][Vehicle.pts.length];
	IntStream.range(0, m).parallel().forEach((vi) -> {
		Vehicle v = universe.vehicles.get(vi);
		double time = Simulator.time;
		for (int i = 0; i < v.lines.length; i++) {
		    Matrix l = v.lines[i].resolvePosition(tt, time);
		    pos[vi][0][i] = (int)l.getEntry(1, 0);
		    pos[vi][1][i] = (int)l.getEntry(2, 0);
		};

		/*
		  if (i == (m - 1)) {
		  if ((pos1 != null) && (pos2 != null)) {
		  //v.nose.print(System.out);
		  //System.out.println(pos1);
		  System.out.println(pos3.subtractFrom(pos1));
		  }
		  }
		*/
	    });
	for (int i = 0; i < m; i++) {
	    Vehicle v = universe.vehicles.get(i);
	    g.setColor(v.color);
	    g.drawPolygon(pos[i][0], pos[i][1], pos[i][0].length);
	}
    }
}
