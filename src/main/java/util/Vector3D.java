package util;

public class Vector3D {

    /**
     * The 3 components of the vector in an array
     */
    protected double[] vector = new double[3];

    /**
     * Create a vector where all components are set to zero
     */
    public Vector3D() {
        for (int i = 0; i < 3; i++) {
            vector[i] = 0;
        }
    }

    /**
     * Create a vector where the components are set to the parameters
     * @param x the desired x component of the vector
     * @param y the desired y component of the vector
     * @param z the desired z component of the vector
     */
    public Vector3D(double x, double y, double z) {
        vector[0] = x;
        vector[1] = y;
        vector[2] = z;
    }

    /**
     * Create a vector where the components are set to a 3-dimensional array
     * @param arr the vector as a 3d array
     */
    public Vector3D(double[] arr) {
        if (arr.length == 3) {
            vector[0] = arr[0];
            vector[1] = arr[1];
            vector[2] = arr[2];
        }
    }

    /**
     * Method to set the vectors components
     *
     * @param i the position of the component
     * @param num the desired value of the component
     */
    public void set(int i, double num) {
        if (i >= 0 && i < 3) {
            vector[i] = num;
        }
    }
    /**
     * Return the x component of the vector
     *
     * @return the x component of the vector
     */
    public double getX() { return vector[0]; }

    /**
     * Return the y component of the vector
     *
     * @return the y component of the vector
     */
    public double getY() { return vector[1]; }

    /**
     * Return the z component of the vector
     *
     * @return the z component of the vector
     */
    public double getZ() { return vector[2]; }

    /**
     * Gets the magnitude of the vector
     *
     * @return the magnitude of the vector
     */
    public double getMag() {
        double sum = 0;
        for (int i = 0; i < vector.length; i++) {
            sum += vector[i] * vector[i];
        }
        return Math.sqrt(sum);
    }

    /**
     * Sets the magnitude of the vector
     *
     * @param new_mag the desired magnitude
     */
    public void setMag(double new_mag) {
        double mag = getMag();
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] * new_mag / mag;
        }
    }

    /**
     * Adds a vector to the current vector
     * Modifies the current vector
     *
     * @param other the other vector to be added to this one
     */
    public void addLocal(Vector3D other) {
        for (int i = 0; i < this.vector.length; i++) {
            this.vector[i] += other.vector[i];
        }
    }

    /**
     * Adds a vector to the current vector
     * Returns a new Vector
     *
     * @param other the other vector to be added to this one
     * @return the new vector
     */
    public Vector3D add(Vector3D other) {
        Vector3D newVec = new Vector3D();
        for (int i = 0; i < this.vector.length; i++) {
            newVec.vector[i] = this.vector[i] + other.vector[i];
        }
        return newVec;
    }

    /**
     * Subtracts the other vector from this one
     * Returns a new Vecotr
     *
     * @param other the other vector
     * @return the new vector
     */
    public Vector3D sub(Vector3D other) {
        Vector3D newVec = new Vector3D();
        for (int i = 0; i < this.vector.length; i++) {
            newVec.vector[i] = this.vector[i] - other.vector[i];
        }
        return newVec;
    }
    /**
     * Returns the scalar product of this and another vector
     *
     * @param other the other vector
     * @return the scalar
     */
    public double dot(Vector3D other) {
        double sum = 0;
        for (int i = 0; i < this.vector.length; i++) {
            sum += this.vector[i] * other.vector[i];
        }
        return sum;
    }

    /**
     * Scalar multiply the current vector with a number
     * Changes the current vector
     *
     * @param scalar the scalar
     */
    public void multLocal(double scalar) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] *= scalar;
        }
    }

    /**
     * Get the distance from the current vector to another vector
     *
     * @param other the other vector
     * @return the distance from the current to the other vector
     */
    public double distTo(Vector3D other) {
        double sum = 0;
        for (int i = 0; i < this.vector.length; i++) {
            sum += Math.pow(this.vector[i] - other.vector[i],2);
        }
        return Math.sqrt(sum);
    }
    /**
     * Helper function to show the vector on the console
     */
    public String toString() {
        return "(" + vector[0] + ", " + vector[1] + ", " + vector[2] + ")";
    }
}
