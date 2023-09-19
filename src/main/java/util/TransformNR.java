package util;

import java.math.BigDecimal;
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
     * @param m the m
     */
    public TransformNR(Matrix3D m) {
        this.setX(m.get(0, 3));
        this.setY(m.get(1, 3));
        this.setZ(m.get(2, 3));
        this.setRotation(new Quaternion(m));
    }

    /**
     * Instantiates a new transform nr.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @param w the w
     * @param rotx the rotx
     * @param roty the roty
     * @param rotz the rotz
     */
    public TransformNR(double x, double y, double z, double w, double rotx, double roty,
                       double rotz) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
        this.setRotation(new Quaternion(new double[] {w, rotx, roty, rotz}));
    }

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
        this.setRotation(new Quaternion(rotationMatrix));
    }

    /**
     * Instantiates a new transform nr.
     *
     * @param cartesianSpaceVector the cartesian space vector
     * @param quaternionVector the quaternion vector
     */
    public TransformNR(double[] cartesianSpaceVector, double[] quaternionVector) {
        this.setX(cartesianSpaceVector[0]);
        this.setY(cartesianSpaceVector[1]);
        this.setZ(cartesianSpaceVector[2]);
        this.setRotation(new Quaternion(quaternionVector));
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
     * Gets the x.
     *
     * @return the x
     */
    public double getX() {
        return x;
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
     * Gets the z.
     *
     * @return the z
     */
    public double getZ() {
        return z;
    }

    /**
     * Gets the rotation matrix array.
     *
     * @return the rotation matrix array
     */
    public double[][] getRotationMatrixArray() {
        return getRotation().getRotationMatrix();
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
     * Gets the rotation value.
     *
     * @param i the i
     * @param j the j
     * @return the rotation value
     */
    public double getRotationValue(int i, int j) {
        return getRotation().getRotationMatrix()[i][j];
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
     * Times.
     *
     * @param t the t
     * @return the transform nr
     */
    public TransformNR times(TransformNR t) {
        return new TransformNR(getMatrixTransform().times(t.getMatrixTransform()));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        try {
            return getMatrixString(getMatrixTransform()) + getRotation().toString();
        } catch (Exception ex) {
            return "Transform error" + ex.getLocalizedMessage();
        }
    }

    /**
     * Gets the matrix string.
     *
     * @param matrix the matrix
     * @return the matrix string
     */
    public static String getMatrixString(Matrix3D matrix) {
        String s = "{\n";
        double[][] m = matrix.getArray();

        int across = m.length;
        int down = m[0].length;

        for (int i = 0; i < across; i++) {
            s += "{ ";
            for (int j = 0; j < down; j++) {
                if (m[i][j] < 0)
                    s += new DecimalFormat("000.00").format(m[i][j]);
                else
                    s += new DecimalFormat("0000.00").format(m[i][j]);
                if (j < down - 1)
                    s += ",";
                s += "\t";
            }
            s += " }";
            if (i < across - 1)
                s += ",";
            s += "\n";
        }
        return s + "}\n";
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
    public Matrix3D getMatrixTransform() {
        double[][] transform = new double[4][4];
        double[][] rotation = getRotationMatrixArray();


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                transform[i][j] = rotation[i][j];
            }
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

    /**
     * Gets the offset orentation magnitude.
     *
     * @param t the t
     * @return the offset orentation magnitude
     */
    public double getOffsetOrentationMagnitude(TransformNR t) {
        double x = getRotation().getRotationMatrix2QuaturnionX()
                - t.getRotation().getRotationMatrix2QuaturnionX();
        double y = getRotation().getRotationMatrix2QuaturnionY()
                - t.getRotation().getRotationMatrix2QuaturnionY();
        double z = getRotation().getRotationMatrix2QuaturnionZ()
                - t.getRotation().getRotationMatrix2QuaturnionZ();
        double r = Math.sqrt((Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)));
        return r;
    }

    /**
     * Gets the offset vector magnitude.
     *
     * @param t the t
     * @return the offset vector magnitude
     */
    public double getOffsetVectorMagnitude(TransformNR t) {
        double x = getX() - t.getX();
        double y = getY() - t.getY();
        double z = getZ() - t.getZ();
        double r = Math.sqrt((Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)));
        return r;
    }

    /**
     * Inverse.
     *
     * @return the transform nr
     */
    public TransformNR inverse() {
        return new TransformNR(getMatrixTransform().inverse());
    }

    /**
     * Scale.
     *
     * @param scale the scale
     * @return the transform nr
     */
    public TransformNR scale(BigDecimal scale) {
        return scale(scale.doubleValue());
    }

    /**
     * Scale.
     *
     * @param t the scale from 0 to 1.0
     * @return the transform nr
     */
    public TransformNR scale(double t) {
        if (t > 1)
            t = 1;
        if (t <= 0)
            return new TransformNR();

        double tilt = Math.toDegrees(getRotation().getRotationTilt() * t);
        double az = Math.toDegrees(getRotation().getRotationAzimuth() * t);
        double ele = Math.toDegrees(getRotation().getRotationElevation() * t);
        return new TransformNR(getX() * t, getY() * t, getZ() * t, new RotationNR(tilt, az, ele));
    }

    /**
     * Copy.
     *
     * @return the transform nr
     */
    public TransformNR copy() {
        return new TransformNR(getMatrixTransform());
    }

    /**
     * Translate x.
     *
     * @param translation the translation
     * @return
     */
    public TransformNR translateX(double translation) {
        setX(getX() + translation);
        return this;
    }

    /**
     * Translate y.
     *
     * @param translation the translation
     */
    public TransformNR translateY(double translation) {
        setY(getY() + translation);
        return this;

    }

    /**
     * Translate z.
     *
     * @param translation the translation
     */
    public TransformNR translateZ(double translation) {

        setZ(getZ() + translation);
        return this;
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
     * Gets the xml.
     *
     * @return the xml
     */
    /*
     *
     * Generate the xml configuration to generate an XML of this robot.
     */
    public String getXml() {
        String xml =
                "\t<x>" + getX() + "</x>\n" + "\t<y>" + getY() + "</y>\n" + "\t<z>" + getZ() + "</z>\n";
        if (Double.isNaN(getRotation().getRotationMatrix2QuaturnionW())
                || Double.isNaN(getRotation().getRotationMatrix2QuaturnionX())
                || Double.isNaN(getRotation().getRotationMatrix2QuaturnionY())
                || Double.isNaN(getRotation().getRotationMatrix2QuaturnionZ())) {
            xml += "\n\t<!-- ERROR a NaN was detected and replaced with a valid rotation -->\n";
            setRotation(new Quaternion());
        }
        xml += "\t<rotw>" + getRotation().getRotationMatrix2QuaturnionW() + "</rotw>\n" + "\t<rotx>"
                + getRotation().getRotationMatrix2QuaturnionX() + "</rotx>\n" + "\t<roty>"
                + getRotation().getRotationMatrix2QuaturnionY() + "</roty>\n" + "\t<rotz>"
                + getRotation().getRotationMatrix2QuaturnionZ() + "</rotz>";

        return xml;
    }

    /**
     * Sets the rotation.
     *
     * @param rotation the new rotation
     */
    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }



}
