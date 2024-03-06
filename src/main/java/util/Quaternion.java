package util;

public class Quaternion {
    /**
     * The real component (x0).
     */
    protected double w;
    /**
     * The 1st imaginary component (x1).
     */
    protected double x;
    /**
     * The 2nd imaginary component (x2).
     */
    protected double y;
    /**
     * The 3rd imaginary component (x3).
     */
    protected double z;


    /**
     * Creates an identity quaternion: all components are zero except
     * {@code x0}, which is set to 1.
     */
    public Quaternion() {
        w = 1;
        x = 0;
        y = 0;
        z = 0;
    }

    /**
     * Creates a quaternion with the specified components.
     *
     * @param w the x0 component
     * @param x the x1 component
     * @param y the x2 component
     * @param z the x3 component
     */
    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
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

    public Quaternion(Matrix3D matrix) {
        fromRotationMatrix(matrix);
    }

    /**
     * Returns the w (real) component.
     *
     * @return the value of the {@link #w} component
     */
    public double getW() {
        return w;
    }

    /**
     * Returns the x component.
     *
     * @return the value of the {@link #x} component
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y component.
     *
     * @return the value of the {@link #y} component
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the z component.
     *
     * @return the value of the {@link #z} component
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets all 4 components to specified values.
     *
     * @param w the w (real) component
     * @param x the x component
     * @param y the y component
     * @param z the z component
     * @return the (modified) current instance
     */
    public Quaternion set(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Copies all 4 components from another quaternion.
     *
     * @param q the quaternion to copy (not null, unaffected)
     * @return the (modified) current instance
     */
    public Quaternion set(Quaternion q) {
        this.w = q.w;
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        return this;
    }
    /**
     * Sets all components to zero except {@code w}, which is set to 1.
     */
    public void makeIdentityQuaternion() {
        this.x = this.y = this.z = 0;
        this.w = 1;
    }

    /**
     * Compares with the identity quaternion, without distinguishing -0 from 0.
     * The current instance is unaffected.
     *
     * @return true if the current quaternion equals the identity, otherwise
     *     false
     */
    public boolean isIdentity() {
        return this.w == 1 && this.x == 0 && this.y == 0 && this.z == 0;
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

        this.w = (c1c2 * c3 - s1s2 * s3);
        this.x = (c1c2 * s3 + s1s2 * c3);
        this.y = (s1 * c2 * c3 + c1 * s2 * s3);
        this.z = (c1 * s2 * c3 - s1 * c2 * s3);

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

        double sqx0 = w * w;
        double sqx1 = x * x;
        double sqx2 = y * y;
        double sqx3 = z * z;
        double unit = sqx1 + sqx2 + sqx3 + sqx0; // if normalized is one, otherwise
        // is correction factor
        double test = x * y + z * w;
        if (test > 0.499 * unit) { // singularity at north pole
            angles[1] = 2 * Math.atan2(x, w);
            angles[2] = Math.PI / 2.0;
            angles[0] = 0;
        } else if (test < -0.499 * unit) { // singularity at south pole
            angles[1] = -2 * Math.atan2(x, w);
            angles[2] = -Math.PI / 2.0;
            angles[0] = 0;
        } else {
            angles[1] = Math.atan2(2 * y * w - 2 * x * z, sqx1 - sqx2 - sqx3 + sqx0); // yaw or heading
            angles[2] = Math.asin(2 * test / unit); // roll or bank
            angles[0] = Math.atan2(2 * x * w - 2 * y * z, -sqx1 + sqx2 - sqx3 + sqx0); // pitch or attitude
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
            w = 0.5f * s;
            s = 0.5f / s;
            x = (matrix.get(2,1) - matrix.get(1,2)) * s;
            y = (matrix.get(0,2) - matrix.get(2,0)) * s;
            z = (matrix.get(1,0) - matrix.get(0,1)) * s;
        } else if ((matrix.get(0,0) > matrix.get(1,1)) && (matrix.get(0,0)> matrix.get(2,2))) {
            double s = Math.sqrt(1.0f + matrix.get(0,0) - matrix.get(1,1) - matrix.get(2,2)); // |s|>=1
            x = s * 0.5f; // |x| >= .5
            s = 0.5f / s;
            y = (matrix.get(1,0) + matrix.get(0,1)) * s;
            z = (matrix.get(0,2) + matrix.get(2,0)) * s;
            w = (matrix.get(2,1) - matrix.get(1,2)) * s;
        } else if (matrix.get(1,1) > matrix.get(2,2)) {
            double s = Math.sqrt(1.0f + matrix.get(1,1) - matrix.get(0,0) - matrix.get(2,2)); // |s|>=1
            y = s * 0.5f; // |y| >= .5
            s = 0.5f / s;
            x = (matrix.get(1,0) + matrix.get(0,1)) * s;
            z = (matrix.get(2,1) + matrix.get(1,2)) * s;
            w = (matrix.get(0,2) - matrix.get(2,0)) * s;
        } else {
            double s = Math.sqrt(1.0f + matrix.get(2,2) - matrix.get(0,0) - matrix.get(1,1)); // |s|>=1
            z = s * 0.5f; // |z| >= .5
            s = 0.5f / s;
            x = (matrix.get(0,2) + matrix.get(2,0)) * s;
            y = (matrix.get(2,1) + matrix.get(1,2)) * s;
            w = (matrix.get(1,0) - matrix.get(0,1)) * s;
        }

        return this;
    }

    /**
     * Converts to an equivalent rotation matrix. The current instance is
     * unaffected.
     *
     * <p>Note: the result is created from a normalized version..
     *
     * @return {@code result}, configured as a 3x3 rotation matrix
     */
    public Matrix3D toRotationMatrix() {
        Matrix3D result = new Matrix3D();

        double norm = norm();
        double s = (norm == 1f) ? 2f : (norm > 0f) ? 2f / norm : 0;

        // compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
        // will be used 2-4 times each.
        double xs = x * s;
        double ys = y * s;
        double zs = z * s;
        double xx = x * xs;
        double xy = x * ys;
        double xz = x * zs;
        double xw = w * xs;
        double yy = y * ys;
        double yz = y * zs;
        double yw = w * ys;
        double zz = z * zs;
        double zw = w * zs;

        // using s=2/norm (instead of 1/norm) saves 9 multiplications by 2 here
        result.set(0,0,1 - (yy + zz));
        result.set(0,1,xy - zw);
        result.set(0,2,xz + yw);
        result.set(1,0,xy + zw);
        result.set(1,1,1 - (xx + zz));
        result.set(1,2,yz - xw);
        result.set(2,0,xz - yw);
        result.set(2,1,yz + xw);
        result.set(2,2,1 - (xx + yy));

        return result;
    }

    /**
     * Returns the norm, defined as the dot product of the quaternion with
     * itself. The current instance is unaffected.
     *
     * @return the sum of the squared components (not negative)
     */
    public double norm() {
        return w * w + x * x + y * y + z * z;
    }

    /**
     * Subtracts the argument and returns difference as a new instance. The
     * current instance is unaffected.
     *
     * @param q the quaternion to subtract (not null, unaffected)
     * @return a new Quaternion
     */
    public Quaternion subtract(Quaternion q) {
        return new Quaternion(w - q.w, x - q.x, y - q.y, z - q.z);
    }

    /**
     * Interpolates between the specified quaternions and stores the result in
     * the current instance.
     *
     * @param q1 the desired value when interp=0 (not null, unaffected)
     * @param q2 the desired value when interp=1 (not null, may be modified)
     * @param t the fractional change amount
     * @return the (modified) current instance (for chaining)
     */
    public Quaternion slerp(Quaternion q1, Quaternion q2, float t) {
        // Create a local quaternion to store the interpolated quaternion
        if (q1.x == q2.x && q1.y == q2.y && q1.z == q2.z && q1.w == q2.w) {
            this.set(q1);
            return this;
        }

        double result = (q1.x * q2.x) + (q1.y * q2.y) + (q1.z * q2.z) + (q1.w * q2.w);

        if (result < 0.0f) {
            // Negate the second quaternion and the result of the dot product
            q2.x = -q2.x;
            q2.y = -q2.y;
            q2.z = -q2.z;
            q2.w = -q2.w;
            result = -result;
        }

        // Set the first and second scale for the interpolation
        double scale0 = 1 - t;
        double scale1 = t;

        // Check if the angle between the 2 quaternions was big enough to
        // warrant such calculations
        if ((1 - result) > 0.1f) {// Get the angle between the 2 quaternions,
            // and then store the sin() of that angle
            double theta = Math.acos(result);
            double invSinTheta = 1f / Math.sin(theta);

            // Calculate the scale for q1 and q2, according to the angle and
            // its sine
            scale0 = Math.sin((1 - t) * theta) * invSinTheta;
            scale1 = Math.sin((t * theta)) * invSinTheta;
        }

        // Calculate the x, y, z and w values for the quaternion by using a
        // special
        // form of linear interpolation for quaternions.
        this.x = (scale0 * q1.x) + (scale1 * q2.x);
        this.y = (scale0 * q1.y) + (scale1 * q2.y);
        this.z = (scale0 * q1.z) + (scale1 * q2.z);
        this.w = (scale0 * q1.w) + (scale1 * q2.w);

        // Return the interpolated quaternion
        return this;
    }

    /**
     * Helper function to print the values of the quaternion
     */
    public String toString() {
        return "W: " + w + " X: " + x + " Y: " + y + " Z: " + z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quaternion that)) return false;

        if (Double.compare(w, that.w) != 0) return false;
        if (Double.compare(x, that.x) != 0) return false;
        if (Double.compare(y, that.y) != 0) return false;
        return Double.compare(z, that.z) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(w);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
