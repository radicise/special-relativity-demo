public class Transformations {
	public static final double dv = 0.001 * Simulator.SCALING;
	public static final double dth = Math.PI / 18.0d;
	public static final Matrix boostX;
	public static final Matrix boostXInv;
	public static final Matrix boostY;
	public static final Matrix boostYInv;
    public static final Matrix rotateClock;
    public static final Matrix rotateCounterClock;
	static {
		double lf = 1.0d / Math.sqrt(1 - (dv * dv));
		boostX = new Matrix(new double[][]{
			new double[]{lf, -(lf * dv), 0.0d, 0.0d},
			new double[]{-(lf * dv), lf, 0.0d, 0.0d},
			new double[]{0.0d, 0.0d, 1.0d, 0.0d},
			new double[]{0.0d, 0.0d, 0.0d, 1.0d}
		});
		boostY = new Matrix(new double[][]{
			new double[]{lf, 0.0d, -(lf * dv), 0.0d},
			new double[]{0.0d, 1.0d, 0.0d, 0.0d},
			new double[]{-(lf * dv), 0.0d, lf, 0.0d},
			new double[]{0.0d, 0.0d, 0.0d, 1.0d}
		});
		boostXInv = new Matrix(new double[][]{
			new double[]{lf, lf * dv, 0.0d, 0.0d},
			new double[]{lf * dv, lf, 0.0d, 0.0d},
			new double[]{0.0d, 0.0d, 1.0d, 0.0d},
			new double[]{0.0d, 0.0d, 0.0d, 1.0d}
		});
		boostYInv = new Matrix(new double[][]{
			new double[]{lf, 0.0d, lf * dv, 0.0d},
			new double[]{0.0d, 1.0d, 0.0d, 0.0d},
			new double[]{lf * dv, 0.0d, lf, 0.0d},
			new double[]{0.0d, 0.0d, 0.0d, 1.0d}
		});

		rotateClock = new Matrix(new double[][]{
			new double[]{1.0d, 0.0d, 0.0d, 0.0d},
			new double[]{0.0d, Math.cos(dth), -Math.sin(dth), 0.0d},
			new double[]{0.0d, Math.sin(dth), Math.cos(dth), 0.0d},
			new double[]{0.0d, 0.0d, 0.0d, 1.0d}
		});
		rotateCounterClock = new Matrix(new double[][]{
			new double[]{1.0d, 0.0d, 0.0d, 0.0d},
			new double[]{0.0d, Math.cos(dth), Math.sin(dth), 0.0d},
			new double[]{0.0d, -Math.sin(dth), Math.cos(dth), 0.0d},
			new double[]{0.0d, 0.0d, 0.0d, 1.0d}
		});
	}
}
