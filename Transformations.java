public class Transformations {
	public static final double dv = 0.1;
	public static final Matrix boostX;
	public static final Matrix boostXInv;
	public static final Matrix boostY;
	public static final Matrix boostYInv;
	static {
		double lf = 1.0d / Math.sqrt(1 - (dv * dv));
		boostX = new Matrix(new double[][]{new double[]{lf, -(lf * dv), 0.0d},
			new double[]{-(lf * dv), lf, 0.0d},
			new double[]{0.0d, 0.0d, 1.0d}
		});
		boostY = new Matrix(new double[][]{new double[]{lf, 0.0d, -(lf * dv)},
			new double[]{0.0d, 1.0d, 0.0d},
			new double[]{-(lf * dv), 0.0d, lf}
		});
		boostXInv = new Matrix(new double[][]{new double[]{lf, lf * dv, 0.0d},
			new double[]{lf * dv, lf, 0.0d},
			new double[]{0.0d, 0.0d, 1.0d}
		});
		boostYInv = new Matrix(new double[][]{new double[]{lf, 0.0d, lf * dv},
			new double[]{0.0d, 1.0d, 0.0d},
			new double[]{lf * dv, 0.0d, lf}
		});
	}
}
