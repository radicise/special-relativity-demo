import java.util.stream.IntStream;

public class Matrix {
    public final int m, n;
    private final double[][] matrix;

    public Matrix(int m, int n) {
	matrix = new double[m][n];
	this.m = m;
	this.n = n;
    }

    public Matrix(double[][] matrix) {
	assert matrix.length > 0;
	this.matrix = matrix;
	m = matrix.length;
	n = matrix[0].length;
    }

    public static Matrix identity(int n) {
	double[][] parts = new double[n][n];
	for (int i = 0; i < n; i++) {
	    parts[i][i] = 1.0;
	}
	return new Matrix(parts);
    }

    // We will only transform matrices. Since we are applying
    // the same transformation to a bunch of vectors, we just
    // keep a matrix with all the vectors as columns and
    // transform it once
    public Matrix transform(Matrix other) {
	assert n == other.m;
        double[][] transformed = new double[m][other.n];
	IntStream.range(0, other.n).parallel().forEach((otherCol) -> {
            IntStream.range(0, m).parallel().forEach((thisRow) -> {
		double sum = 0;
		for (int otherRow = 0; otherRow < n; otherRow++) {
		    sum += matrix[thisRow][otherRow] *
			other.matrix[otherRow][otherCol];
		}
		transformed[thisRow][otherCol] = sum;
	    });
	});
	return new Matrix(transformed);
    }

    public String toString() {
	String s = "";
	for (int row = 0; row < m; row++) {
	    s += "[ ";
	    for (int col = 0; col < n; col++) {
		s += matrix[row][col] + " ";
	    }
	    s += "]\n";
	}
	return s;
    }
    public double getEntry(int row, int col) {
        return matrix[row][col];
    }
    public static Matrix weightedAvg(Matrix a, Matrix b, double weight) {
        assert (a.m == b.m);
	assert (a.n == b.n);
	double[][] weightedMatrix = new double[a.m][a.n];
	IntStream.range(0, a.m).parallel().forEach((row) -> {
            IntStream.range(0, a.n).parallel().forEach((col) -> {
                weightedMatrix[row][col] = a.matrix[row][col] + ((b.matrix[row][col] - a.matrix[row][col]) * weight);
	    });
	});
	return new Matrix(weightedMatrix);
    }
    public static void main(String[] args) {
	Matrix a = new Matrix(new double[][] {{5, 6, 7}, {8, 9, 10}, {11, 12, 13}});
	Matrix b = new Matrix(new double[][] {{1, 2}, {3, 4}, {5, 6}});
	Matrix c = new Matrix(new double[][] {{20, 19}, {18, 17}});
	System.out.println(a.transform(b));
	System.out.println(b.transform(c));
    }
    public Matrix subtractFrom(Matrix a) {
        assert (m == a.m);
	assert (n == a.n);
	double[][] mtrx = new double[m][n];
	IntStream.range(0, m).parallel().forEach((row) -> {
            IntStream.range(0, n).parallel().forEach((col) -> {
                mtrx[row][col] = a.matrix[row][col] - matrix[row][col];
	    });
	});
	return new Matrix(mtrx);
    }
    public static Matrix add(Matrix a, Matrix b) {
        assert (a.m == b.m);
	assert (a.n == b.n);
	double[][] mtrx = new double[a.m][a.n];
	IntStream.range(0, a.m).parallel().forEach((row) -> {
            IntStream.range(0, a.n).parallel().forEach((col) -> {
                mtrx[row][col] = a.matrix[row][col] + b.matrix[row][col];
	    });
	});
	return new Matrix(mtrx);
    }
}
