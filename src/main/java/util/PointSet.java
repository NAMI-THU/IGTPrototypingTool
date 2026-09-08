package util;

/**
 * This class bundles the entry and target vectors of a loaded .MPS file with its file name.
 * */
public class PointSet {

    /** Entry position */
    Vector3D v1;

    /** Target position */
    Vector3D v2;

    /** Path name of .MPS */
    String fileName;

    public PointSet(Vector3D v1, Vector3D v2, String fileName) {
        this.v1 = v1;
        this.v2 = v2;
        this.fileName = fileName;
    }

    public String getName() {
        return fileName;
    }

    public Vector3D getV1() {
        return v1;
    }

    public Vector3D getV2() {
        return v2;
    }

}
