import java.util.stream.IntStream;

public class Matrix {
    public int m, n;
    public int[][] matrix;
    private int[][] transformed;

    public Matrix(int m, int n) {
	matrix = new int[m][n];
	this.m = m;
	this.n = n;
    }

    public Matrix(int[][] matrix) {
	assert matrix.length > 0;
	this.matrix = matrix;
	m = matrix.length;
	n = matrix[0].length;
    }

    // We will only transform matrices. Since we are applying
    // the same transformation to a bunch of vectors, we just
    // keep a matrix with all the vectors as columns and
    // transform it once
    public Matrix transform(Matrix other) {
	assert n == other.m;
	transformed = new int[m][other.n];
	IntStream.range(0, other.n).parallel().forEach((otherCol) -> {
		IntStream.range(0, m).parallel().forEach((thisRow) -> {
			int sum = 0;
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

    public static void main(String[] args) {
	Matrix a = new Matrix(new int[][] {{5, 6, 7}, {8, 9, 10}, {11, 12, 13}});
	Matrix b = new Matrix(new int[][] {{1, 2}, {3, 4}, {5, 6}});
	Matrix c = new Matrix(new int[][] {{20, 19}, {18, 17}});
	System.out.println(a.transform(b));
	System.out.println(b.transform(c));
    }
}
