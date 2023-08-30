package util;

public class Vector3D {

    /**
     * The x component of the vector
     */
    protected double x;

    /**
     * The y component of the vector
     */
    protected double y;

    /**
     * The z component of the vector
     */
    protected double z;

    /**
     * Create a vector where all components are set to zero
     */
    public Vector3D() {
        x = 0;
        y = 0;
        z = 0;
    }

    /**
     * Create a vector where the components are set to the parameters
     * @param x the desired x component of the vector
     * @param y the desired y component of the vector
     * @param z the desired z component of the vector
     */
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Helper function to show the vector on the console
     */
    public void print() {
        System.out.println("(" + x + ", " + y + ", " + z + ")");
    }
}
