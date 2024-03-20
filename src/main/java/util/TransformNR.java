package util;

import java.text.DecimalFormat;


// Taken from com.neuronrobotics.sdk.addons.kinematics.math.TransformNR
// GNU Lesser General Public License v3.0
/**
 * The Class TransformNR.
 */
public class TransformNR {

    /** The x. */
    private double x;

    /** The y. */
    private double y;

    /** The z. */
    private double z;

    /** The rotation. */
    private Quaternion rotation;


    /**
     * Instantiates a new transform nr.
     *
     * @param cartesianSpaceVector the cartesian space vector
     * @param rotationMatrix the rotation matrix
     */
    public TransformNR(double[] cartesianSpaceVector, double[][] rotationMatrix) {
        this.setX(cartesianSpaceVector[0]);
        this.setY(cartesianSpaceVector[1]);
        this.setZ(cartesianSpaceVector[2]);
        this.setRotation(new Quaternion().fromRotationMatrix(new Matrix3D(rotationMatrix)));
    }

    /**
     * Instantiates a new transform nr.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @param q the q
     */
    public TransformNR(double x, double y, double z, Quaternion q) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
        this.setRotation(q);
    }

    /**
     * Instantiates a new transform nr.
     *
     * @param cartesianSpaceVector the cartesian space vector
     * @param q the q
     */
    public TransformNR(double[] cartesianSpaceVector, Quaternion q) {
        this.setX(cartesianSpaceVector[0]);
        this.setY(cartesianSpaceVector[1]);
        this.setZ(cartesianSpaceVector[2]);
        this.setRotation(q);
    }

    /**
     * Instantiates a new transform nr.
     */
    public TransformNR() {
        this.setX(0);
        this.setY(0);
        this.setZ(0);
        this.setRotation(new Quaternion());
    }

    /**
     * Gets the matrix string.
     *
     * @param matrix the matrix
     * @return the matrix string
     */
    public static String getMatrixString(Matrix3D matrix) {
        StringBuilder s = new StringBuilder("{\n");
        double[][] m = matrix.asArray();

        int across = m.length;
        int down = m[0].length;

        for (int i = 0; i < across; i++) {
            s.append("{ ");
            for (int j = 0; j < down; j++) {
                if (m[i][j] < 0)
                    s.append(new DecimalFormat("000.00").format(m[i][j]));
                else
                    s.append(new DecimalFormat("0000.00").format(m[i][j]));
                if (j < down - 1)
                    s.append(",");
                s.append("\t");
            }
            s.append(" }");
            if (i < across - 1)
                s.append(",");
            s.append("\n");
        }
        return s + "}\n";
    }

    /**
     * Gets the x.
     *
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x.
     *
     * @param translation the new x
     */
    public TransformNR setX(double translation) {
        if (Double.isNaN(translation))
            throw new RuntimeException("Value can not be NaN");
        x = translation;
        return this;
    }

    /**
     * Gets the y.
     *
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y.
     *
     * @param translation the new y
     */
    public TransformNR setY(double translation) {
        if (Double.isNaN(translation))
            throw new RuntimeException("Value can not be NaN");
        y = translation;
        return this;
    }

    /**
     * Gets the z.
     *
     * @return the z
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets the z.
     *
     * @param translation the new z
     */
    public TransformNR setZ(double translation) {
        if (Double.isNaN(translation))
            throw new RuntimeException("Value can not be NaN");
        z = translation;
        return this;
    }

    /**
     * Gets the rotation matrix array.
     *
     * @return the rotation matrix array
     */
    public double[][] getRotationMatrixArray() {
        return getRotation().toRotationMatrix().asArray();
    }

    /**
     * Gets the rotation matrix.
     *
     * @return the rotation matrix
     */
    public Quaternion getRotationMatrix() {
        return getRotation();
    }

    /**
     * Gets the rotation.
     *
     * @return the rotation
     */
    public Quaternion getRotation() {

        return rotation;
    }

    /**
     * Sets the rotation.
     *
     * @param rotation the new rotation
     */
    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        try {
            return getMatrixString(getTransformationMatrix()) + getRotation().toString();
        } catch (Exception ex) {
            return "Transform error" + ex.getLocalizedMessage();
        }
    }

    /**
     * Gets the position array.
     *
     * @return the position array
     */
    public double[] getPositionArray() {
        return new double[] {getX(), getY(), getZ()};
    }

    /**
     * Gets the matrix transform.
     *
     * @return the matrix transform
     */
    public Matrix3D getTransformationMatrix() {
        double[][] transform = new double[4][4];
        double[][] rotation = getRotationMatrixArray();


        for (int i = 0; i < 3; i++) {
            System.arraycopy(rotation[i], 0, transform[i], 0, 3);
        }
        for (int i = 0; i < 3; i++) {
            transform[3][i] = 0;
        }
        transform[3][3] = 1;
        transform[0][3] = getX();
        transform[1][3] = getY();
        transform[2][3] = getZ();


        return new Matrix3D(transform);
    }



}
