package org.medcare.robot;

public class RasSpacePosition {

    private double[][] normal = new double[3][3];
    private double[] origin = new double[3];

    public RasSpacePosition(double[][] normal, double[] origin) {
        this.normal = normal;
        this.origin = origin;
    }

    public RasSpacePosition() {
        // TODO Auto-generated constructor stub
    }

    public void setPosition(double[][] normal, double[] origin) {
        this.normal = normal;
        this.origin = origin;
    }

    public double[][] getNormals() {
        return normal;
    }

    public double[] origin() {
        return origin;
    }

}
