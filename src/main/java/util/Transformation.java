package util;

public class Transformation {
    private final Matrix3D affineTransformationMatrix;
    private final Vector3D translationVector;
    private final Matrix3D rotationMatrix;

    public Transformation(Matrix3D affineTransformationMatrix, Vector3D translationVector, Matrix3D rotationMatrix) {
        this.affineTransformationMatrix = affineTransformationMatrix;
        this.translationVector = translationVector;
        this.rotationMatrix = rotationMatrix;
    }

    public static Transformation identity(){
        return new Transformation(
                Matrix3D.identity(),
                Vector3D.zero(),
                Quaternion.identity().toRotationMatrix()
        );
    }

    public Matrix3D getAffineTransformationMatrix() {
        return affineTransformationMatrix;
    }
    public Vector3D getTranslationVector() {
        return translationVector;
    }
    public Matrix3D getRotationMatrix() {
        return rotationMatrix;
    }
}
