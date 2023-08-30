package util;

public class Quaternion {
    /**
     * The real component (x0).
     */
    protected double x0;
    /**
     * The 1st imaginary component (x1).
     */
    protected double x1;
    /**
     * The 2nd imaginary component (x2).
     */
    protected double x2;
    /**
     * The 3rd imaginary component (x3).
     */
    protected double x3;


    /**
     * Creates an identity quaternion: all components are zero except
     * {@code x0}, which is set to 1.
     */
    public Quaternion() {
        x0 = 1;
        x1 = 0;
        x2 = 0;
        x3 = 0;
    }

    /**
     * Creates a quaternion with the specified components.
     *
     * @param x0 the x0 component
     * @param x1 the x1 component
     * @param x2 the x2 component
     * @param x3 the x3 component
     */
    public Quaternion(double x0, double x1, double x2, double x3) {
        this.x0 = x0;
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = x3;
    }

    /**
     * Creates a quaternion from Tait-Bryan angles, applying the rotations
     * in x-z-y extrinsic order or y-z'-x" intrinsic order.
     *
     * @param angles an array of Tait-Bryan angles (in degree, exactly 3
     *     elements, the X angle in {@code angles[0]}, the Y angle in {@code
     *     angles[1]}, and the Z angle in {@code angles[2]}, not null,
     *     unaffected)
     */
    public Quaternion(double[] angles) {
        fromDegreeAngles(angles);
    }

    /**
     * Returns the x0 (real) component.
     *
     * @return the value of the {@link #x0} component
     */
    public double getX0() {
        return x0;
    }

    /**
     * Returns the x1 component.
     *
     * @return the value of the {@link #x1} component
     */
    public double getX1() {
        return x1;
    }

    /**
     * Returns the x2 component.
     *
     * @return the value of the {@link #x2} component
     */
    public double getX2() {
        return x2;
    }

    /**
     * Returns the x3 component.
     *
     * @return the value of the {@link #x3} component
     */
    public double getX3() {
        return x3;
    }

    /**
     * Sets all 4 components to specified values.
     *
     * @param x0 the x0 component
     * @param x1 the x1 component
     * @param x2 the x2 component
     * @param x3 the x3 component
     * @return the (modified) current instance
     */
    public Quaternion set(double x0, double x1, double x2, double x3) {
        this.x0 = x0;
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = x3;
        return this;
    }

    /**
     * Copies all 4 components from another quaternion.
     *
     * @param q the quaternion to copy (not null, unaffected)
     * @return the (modified) current instance
     */
    public Quaternion set(Quaternion q) {
        this.x0 = q.x0;
        this.x1 = q.x1;
        this.x2 = q.x2;
        this.x3 = q.x3;
        return this;
    }
    /**
     * Sets all components to zero except {@code w}, which is set to 1.
     */
    public void makeIdentityQuaternion() {
        this.x1 = this.x2 = this.x3 = 0;
        this.x0 = 1;
    }

    /**
     * Compares with the identity quaternion, without distinguishing -0 from 0.
     * The current instance is unaffected.
     *
     * @return true if the current quaternion equals the identity, otherwise
     *     false
     */
    public boolean isIdentity() {
        return this.x0 == 1 && this.x1 == 0 && this.x2 == 0 && this.x3 == 0;
    }

    /**
     * Sets the quaternion from the specified Tait-Bryan angles, applying the
     * rotations in x-z-y extrinsic order or y-z'-x" intrinsic order.
     *
     * @param angles an array of Tait-Bryan angles (in radians, exactly 3
     *     elements, the X angle in {@code angles[0]}, the Y angle in {@code
     *     angles[1]}, and the Z angle in {@code angles[2]}, not null,
     *     unaffected)
     * @return the (modified) current instance (for chaining)
     * @throws IllegalArgumentException if {@code angles.length != 3}
     */
    public Quaternion fromRadiansAngles(double[] angles) {
        if (angles.length != 3) {
            throw new IllegalArgumentException("Angles array must have three elements");
        }
        return fromRadiansAngles(angles[0], angles[1], angles[2]);
    }

    /**
     * Sets the quaternion from the specified Tait-Bryan angles, applying the
     * rotations in x-z-y extrinsic order or y-z'-x" intrinsic order.
     *
     * @param angles an array of Tait-Bryan angles (in degree, exactly 3
     *     elements, the X angle in {@code angles[0]}, the Y angle in {@code
     *     angles[1]}, and the Z angle in {@code angles[2]}, not null,
     *     unaffected)
     * @return the (modified) current instance (for chaining)
     * @throws IllegalArgumentException if {@code angles.length != 3}
     */
    public Quaternion fromDegreeAngles(double[] angles) {
        if (angles.length != 3) {
            throw new IllegalArgumentException("Angles array must have three elements");
        }
        return fromDegreeAngles(angles[0], angles[1], angles[2]);
    }

    /**
     * Sets the quaternion from the specified Tait-Bryan angles, applying the
     * rotations in x-z-y extrinsic order or y-z'-x" intrinsic order.
     *
     * @see
     * <a href="http://www.euclideanspace.com/maths/geometry/rotations/conversions/eulerToQuaternion/index.htm">http://www.euclideanspace.com/maths/geometry/rotations/conversions/eulerToQuaternion/index.htm</a>
     *
     * @param xAngle the X angle (in radians)
     * @param yAngle the Y angle (in radians)
     * @param zAngle the Z angle (in radians)
     * @return the (modified) current instance (for chaining)
     */
    public Quaternion fromRadiansAngles(double xAngle, double yAngle, double zAngle) {
        double c1 = Math.cos(yAngle / 2);
        double s1 = Math.sin(yAngle / 2);
        double c2 = Math.cos(zAngle / 2);
        double s2 = Math.sin(zAngle / 2);
        double c3 = Math.cos(xAngle / 2);
        double s3 = Math.sin(xAngle / 2);

        double c1c2 = c1*c2;
        double s1s2 = s1*s2;

        this.x0 = (c1c2 * c3 - s1s2 * s3);
        this.x1 = (c1c2 * s3 + s1s2 * c3);
        this.x2 = (s1 * c2 * c3 + c1 * s2 * s3);
        this.x3 = (c1 * s2 * c3 - s1 * c2 * s3);

        //normalizeLocal();
        return this;
    }

    /**
     * Sets the quaternion from three angles in degree
     *
     * @param xAngle the X angle (in degree)
     * @param yAngle the Y angle (in degree)
     * @param zAngle the Z angle (in degree)
     * @return the (modified) current instance (for chaining)
     */
    public Quaternion fromDegreeAngles(double xAngle, double yAngle, double zAngle) {
        double xRadians = Math.PI * xAngle / 180;
        double yRadians = Math.PI * yAngle / 180;
        double zRadians = Math.PI * zAngle / 180;
        return fromRadiansAngles(xRadians, yRadians, zRadians);
    }

    /**
     * Converts to equivalent Tait-Bryan angles, to be applied in x-z-y
     * intrinsic order or y-z'-x" extrinsic order, for instance by
     * {@link #fromRadiansAngles(double[])} (double[])}. The current instance is unaffected.
     *
     * @see
     * <a href=
     * "http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToEuler/index.htm">
     * http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToEuler/index.htm
     * </a>
     *
     * @param angles storage for the result, or null for a new float[3]
     * @return an array of 3 angles (in radians, either <code>angles</code> or a
     *     new float[3], the X angle in angles[0], the Y angle in angles[1], and
     *     the Z angle in angles[2])
     * @throws IllegalArgumentException if {@code angles.length != 3}
     */
    public double[] toRadiansAngles(double[] angles) {
        if (angles == null) {
            angles = new double[3];
        } else if (angles.length != 3) {
            throw new IllegalArgumentException("Angles array must have three elements");
        }

        double sqx0 = x0 * x0;
        double sqx1 = x1 * x1;
        double sqx2 = x2 * x2;
        double sqx3 = x3 * x3;
        double unit = sqx1 + sqx2 + sqx3 + sqx0; // if normalized is one, otherwise
        // is correction factor
        double test = x1 * x2 + x3 * x0;
        if (test > 0.499 * unit) { // singularity at north pole
            angles[1] = 2 * Math.atan2(x1, x0);
            angles[2] = Math.PI / 2.0;
            angles[0] = 0;
        } else if (test < -0.499 * unit) { // singularity at south pole
            angles[1] = -2 * Math.atan2(x1, x0);
            angles[2] = -Math.PI / 2.0;
            angles[0] = 0;
        } else {
            angles[1] = Math.atan2(2 * x2 * x0 - 2 * x1 * x3, sqx1 - sqx2 - sqx3 + sqx0); // yaw or heading
            angles[2] = Math.asin(2 * test / unit); // roll or bank
            angles[0] = Math.atan2(2 * x1 * x0 - 2 * x2 * x3, -sqx1 + sqx2 - sqx3 + sqx0); // pitch or attitude
        }
        return angles;
    }
    /**
     * Converts to equivalent Tait-Bryan angles in degree
     *
     * @param angles storage for the result, or null for a new float[3]
     * @return an array of 3 angles (in degree)
     */
    public double[] toDegreesAngles(double[] angles) {
        double[] temp = toRadiansAngles(angles);
        for (int i = 0; i < temp.length; i++) {
            temp[i] *= 180 / Math.PI;
        }
        return temp;
    }

    /**
     * Sets the quaternion from a rotation matrix with the specified elements.
     *
     * @param matrix a Matrix3D object
     * @return the (modified) current instance (for chaining)
     */
    public Quaternion fromRotationMatrix(Matrix3D matrix) {
        // the trace is the sum of the diagonal elements; see
        // http://mathworld.wolfram.com/MatrixTrace.html
        double t = matrix.getTrace();

        // we protect the division by s by ensuring that s>=1
        if (t >= 0) { // |w| >= .5
            double s = Math.sqrt(t + 1); // |s|>=1 ...
            x0 = 0.5f * s;
            s = 0.5f / s;
            x1 = (matrix.get(2,1) - matrix.get(1,2)) * s;
            x2 = (matrix.get(0,2) - matrix.get(2,0)) * s;
            x3 = (matrix.get(1,0) - matrix.get(0,1)) * s;
        } else if ((matrix.get(0,0) > matrix.get(1,1)) && (matrix.get(0,0)> matrix.get(2,2))) {
            double s = Math.sqrt(1.0f + matrix.get(0,0) - matrix.get(1,1) - matrix.get(2,2)); // |s|>=1
            x1 = s * 0.5f; // |x| >= .5
            s = 0.5f / s;
            x2 = (matrix.get(1,0) + matrix.get(0,1)) * s;
            x3 = (matrix.get(0,2) + matrix.get(2,0)) * s;
            x0 = (matrix.get(2,1) - matrix.get(1,2)) * s;
        } else if (matrix.get(1,1) > matrix.get(2,2)) {
            double s = Math.sqrt(1.0f + matrix.get(1,1) - matrix.get(0,0) - matrix.get(2,2)); // |s|>=1
            x2 = s * 0.5f; // |y| >= .5
            s = 0.5f / s;
            x1 = (matrix.get(1,0) + matrix.get(0,1)) * s;
            x3 = (matrix.get(2,1) + matrix.get(1,2)) * s;
            x0 = (matrix.get(0,2) - matrix.get(2,0)) * s;
        } else {
            double s = Math.sqrt(1.0f + matrix.get(2,2) - matrix.get(0,0) - matrix.get(1,1)); // |s|>=1
            x3 = s * 0.5f; // |z| >= .5
            s = 0.5f / s;
            x1 = (matrix.get(0,2) + matrix.get(2,0)) * s;
            x2 = (matrix.get(2,1) + matrix.get(1,2)) * s;
            x0 = (matrix.get(1,0) - matrix.get(0,1)) * s;
        }

        return this;
    }

    /**
     * Helper function to print the values of the quaternion
     */
    public void print() {
        System.out.println("X0: " + x0);
        System.out.println("X1: " + x1);
        System.out.println("X2: " + x2);
        System.out.println("X3: " + x3);
    }

    public static void main(String[] args) {
        Quaternion q = new Quaternion();
        //q.print();

        /*
        Quaternion sec = q.fromDegreeAngles(90,0,0);
        sec.print();

        double[] angles = {0,90,0};
        Quaternion third = new Quaternion(angles);
        third.print();

        double[] angles2 = third.toDegreesAngles(null);
        for (double v : angles2) {
            System.out.println(v);
        }
         */
        double[] arr = {0.0, 1.0, 1.0, 0.0, 0.0, -1.0, 0.0, 1.0, 0.0};
        Matrix3D rotMat = new Matrix3D(arr);
        System.out.println("-------------------");
        Quaternion fourth = q.fromRotationMatrix(rotMat);
        fourth.print();
    }
}
