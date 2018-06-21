package org.medcare.robot;

//import Jama.Matrix;

public class FrameTransformation {
	// public double[] positionVector= new double[3];
	// public double[][] rotationMatrix= new double[3][3];

	private double transformMatrix[][] = new double[4][4];

	// @param m is the 4x4 transformation matrix
	public FrameTransformation(double[][] transformMatrix) {
		this.setTransformMatrix(transformMatrix);
	}
	
	/*public void setTransformMatrix(double[][] rotationMatrix, double[] positionVector) {
		this.transformMatrix[0][0] = rotationMatrix[0][0];
		this.transformMatrix[1][0] = rotationMatrix[1][0];
		this.transformMatrix[2][0] = rotationMatrix[2][0];
		this.transformMatrix[0][1] = rotationMatrix[0][1];
		this.transformMatrix[1][1] = rotationMatrix[1][1];
		this.transformMatrix[2][1] = rotationMatrix[2][1];
		this.transformMatrix[0][2] = rotationMatrix[0][2];
		this.transformMatrix[1][2] = rotationMatrix[1][2];
		this.transformMatrix[2][2] = rotationMatrix[2][2];
		this.transformMatrix[0][3] = positionVector[0];
		this.transformMatrix[1][3] = positionVector[1];
		this.transformMatrix[2][3] = positionVector[2];
		this.transformMatrix[3][0] = 0.0;
		this.transformMatrix[3][1] = 0.0;
		this.transformMatrix[3][2] = 0.0;
		this.transformMatrix[3][3] = 1.0;
	}*/

	public void setTransformMatrix(double transformMatrix[][]) {
		this.transformMatrix = transformMatrix;
	}

	public double[][] getTransformMatrix() {
		return transformMatrix;
	}
	

	/*
	 * double[][] array = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}}; Matrix A = new
	 * Matrix(array); Matrix Ainv=A.inverse(); // Log.debug(A);
	 * 
	 * @SuppressWarnings("unused") private double[][] frameMultiplication(double
	 * matrixA[][], double matrixB[][]) { for (int i = 0; i < matrixA.length;
	 * i++) { for (int j = 0; j < matrixB.length - 1; j++) { for (int k = 0; k <
	 * matrixB.length; k++) { this.transformMatrix[i][j] += matrixA[i][k] *
	 * matrixB[k][j]; } } } return this.transformMatrix; }
	 */

}
