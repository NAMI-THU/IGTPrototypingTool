package util;

public class Matrix3D {
    /**
     * A new 3x3 Array as the representation of the matrix
     */
    protected double[][] matrix = new double[3][3];


    /**
     * Instantiates an identity matrix (diagonals = 1, other elements = 0).
     */
    public Matrix3D() {
        makeIdentityMatrix();
    }

    /**
     * Instantiates a matrix with specified elements.
     *
     * @param flattenedMatrix the 3x3 Matrix as a 1d array. Must have 9 Elements.
     */
    public Matrix3D(double[] flattenedMatrix) {
        if (flattenedMatrix.length != 9) {
            throw new IllegalArgumentException("Array must have 9 Elements, the given array has " + flattenedMatrix.length);
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.matrix[i][j] = flattenedMatrix[3*i + j];
            }
        }
    }

    /**
     * Instantiates a copy of the matrix argument. If the argument is null, an
     * identity matrix is produced.
     *
     * @param mat the matrix to copy (unaffected) or null for identity
     */
    public Matrix3D(Matrix3D mat) {
        set(mat);
    }

    /**
     * Copies the matrix argument. If the argument is null, the current instance
     * is set to identity (diagonals = 1, other elements = 0).
     *
     * @param matrix the matrix to copy (unaffected) or null for identity
     * @return the (modified) current instance (for chaining)
     */
    public Matrix3D set(Matrix3D matrix) {
        if (null == matrix) {
            makeIdentityMatrix();
        } else {
            this.matrix = matrix.matrix;
        }
        return this;
    }

    /**
     * Makes an identity matrix. (The diagonals are 1, the others are 0)
     */
    public void makeIdentityMatrix() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == j) {
                    this.matrix[i][j] = 1;
                } else {
                    this.matrix[i][j] = 0;
                }
            }
        }
    }

    /**
     * Returns the element at the position i,j in the matrix
     *
     * @param i the row index
     * @param j the column index
     * @return the value of the element at (i, j)
     * @throws IllegalArgumentException if either index isn't 0, 1, or 2
     */
    public double get(int i, int j) {
        if (i < 3 && j < 3) {
            return this.matrix[i][j];
        } else {
            throw new IllegalArgumentException("Both parameters must be either 0, 1 or 2");
        }
    }

    /**
     * Sets the element at the position i,j in the matrix
     *
     * @param i the row index
     * @param j the column index
     * @return the value of the element at (i, j)
     * @throws IllegalArgumentException if either index isn't 0, 1, or 2
     */
    public void set(int i, int j, double num) {
        if (i < 3 && j < 3) {
            this.matrix[i][j] = num;
        } else {
            throw new IllegalArgumentException("The first two parameters must be either 0, 1 or 2");
        }
    }

    /**
     * Sets the element at the position i,j in the matrix
     *
     * @param col the column to be multiplied
     * @param scalar the scalar with which the column is multiplied
     */
    public void scalarMultiplyColumn(int col, double scalar) {
        for (int i = 0; i < 3; i++) {
            matrix[i][col] *= scalar;
        }
    }
    /**
     * Gets the sum of all number squared in a column
     *
     * @param col the column over which the sum is computed
     * @return the squared sum of the column
     */
    public double getLengthSquaredFromColumn(int col) {
        double sum = 0.0;
        for (int i = 0; i < 3; i++) {
            sum += matrix[i][col] * matrix[i][col];
        }
        return sum;
    }

    /**
     * Gets the trace (the sum of the diagonal) of the matrix
     *
     * @return the trace (the sum of the diagonal) of the matrix
     */
    public double getTrace() {
        return matrix[0][0] + matrix[1][1] + matrix[2][2];
    }

    /**
     * Helper function to print the 3x3 matrix to the console
     */
    public void print() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(this.matrix[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        double[] arr = {1.0, 4.5, 6.7, 3.4, 5.8, 6.7, 3.4, 4.5, 2.4};
        Matrix3D mat = new Matrix3D(arr);
        mat.print();
        System.out.println(mat.get(1,3));
    }
}
