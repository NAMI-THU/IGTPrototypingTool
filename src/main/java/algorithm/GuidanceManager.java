package algorithm;

import controller.GuidanceHandler;
import javafx.scene.PerspectiveCamera;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import util.Matrix3D;
import util.Quaternion;
import util.Vector3D;

import java.util.List;

/**
 * This class manages the main logic of the whole guidance.
 * <p>
 * It covers several mathematical operations, such as calculating the error vector,
 * calculating the angular deviation, the depth of the needle and the correct camera movement.
 * </p>
 * */
public class GuidanceManager {

    /** The entry point on the skin. */
    private Vector3D entryPoint;

    /** The target point e.g. the tumor position. */
    private Vector3D targetPoint;

    /** The target sphere representating the target point. */
    private Sphere targetSphere;

    /** Hold the smoothed camera rotation between frames, used when smoothing is enabled. */
    private Quaternion smoothedRotation;

    /** The feet position of the needle in tool space, approximately 10cm in z-direction. */
    private final static Vector3D TOOL_FEET_POS = new Vector3D(0, 0, 100);

    /** Representating the radius of the target sphere */
    private final static double TARGET_SPHERE_RADIUS = 0.75;

    /** Representating the scale of the UI movement in the guidance circle for tip alignment phase. */
    private final static double TIP_ALIGNMENT_SCALE = 2;

    /** Representating the scale of the UI movement in the guidance circle for angulation phase. */
    private final static double ANGULATION_SCALE = 160;

    /**
     * Representating the offset where the camera will be set in tip alignment phase for the z-axis.
     * F.e -700 means -70cm behind the needle tip position in z-direction.
     * */
    private final static double TIP_ALIGNMENT_CAMERA_OFFSET_Z = -700;

    private final GuidanceHandler guidanceHandler;

    private final TrackingService trackingService = TrackingService.getInstance();

    public GuidanceManager(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
    }

    /**
     * Main method which covers the whole guidance procedure.
     * <p>
     * It calls the corresponding methods for the current phase, along with additional calculations
     * such as depth or geometric distance.
     * </p>
     * */
    public void alignment(List<TrackingTool> trackingTools) {
        if (!guidanceHandler.isGuidanceRunning()) {
            return;
        }

        if (trackingService.getDataService() == null) {
            return;
        }

        double maxDepth = targetPoint.sub(entryPoint).getMag();

        for (TrackingTool data : trackingTools) {
            List<TrackingData> measurement = data.getMeasurement();
            for (TrackingData trackingData : measurement) {

                // Access
                Vector3D tipPos = trackingData.getPos();

                Quaternion quaternion = trackingData.getRotation();

                Matrix3D rotationMatrix = quaternion.toRotationMatrix();

                // Depth
                double depth = calculateDepth(tipPos);
                depthInterfaceAdjustments(depth, maxDepth);

                // Geometric distance
                double euclideanDistance = tipPos.distTo(targetPoint);
                renderDistanceLabel(euclideanDistance);

                // Tip alignment
                if (guidanceHandler.getCurrentPhase().equals(GuidanceHandler.Phase.ALIGNMENT)) {
                    tipAlignment(tipPos);
                    cameraTipAlignmentPhase(tipPos);

                    disableTargetSphere();

                    guidanceHandler.setPhaseSwitched(false);
                }

                // Angulation
                if (guidanceHandler.getCurrentPhase().equals(GuidanceHandler.Phase.ANGLE)) {

                    angulation(tipPos, rotationMatrix);
                    cameraAngulationPhase(tipPos, quaternion);

                    disableTargetSphere();

                    guidanceHandler.setPhaseSwitched(false);
                }

                // Scene-depth
                if (guidanceHandler.getCurrentPhase().equals(GuidanceHandler.Phase.DEPTH)) {
                    // First time
                    if (guidanceHandler.isPhaseSwitched()) {
                        addTargetSphere();
                    }

                    cameraAngulationPhase(tipPos, quaternion);

                    // Hit error
                    Vector3D predictedPoint = calculatePredictedPoint(tipPos, rotationMatrix, depth, maxDepth);
                    double hitError = calculateErrorTerm(predictedPoint);
                    renderHitErrorLabel(hitError);

                    guidanceHandler.setPhaseSwitched(false);
                }
            }
        }
    }

    /*----------------------------------------DEPTH----------------------------------------*/

    /**
     * Calculates the current depth of the needle along the optimal path.
     * <p>
     * The scalar projection of the movement onto the path direction tells you, how much of the movement
     * goes into the right direction, and therefore how far along the optimal path the needle currently is.
     * </p>
     * @param tipPos The needle tip position.
     * @return The depth in milimeters.
     * */
    private double calculateDepth(Vector3D tipPos) {
        Vector3D pathDir = (targetPoint.sub(entryPoint)).normalize();

        Vector3D movement = tipPos.sub(entryPoint);

        return movement.dot(pathDir);
    }

    /**
     * Adjusts the height of the depth rectangle and calls to render the current depth onto the screen.
     *
     * @param depth The current depth in milimeters.
     * @param maxDepth The maximum depth in milimeters.
     * */
    private void depthInterfaceAdjustments(double depth, double maxDepth) {
        double progress = calculateDepthProgress(depth, maxDepth);

        double height = adjustDepthRectangleHeight(progress);

        adjustDepthRectangle(height);

        renderDepthLabel(depth);
    }

    /**
     * This method calculates the progress in percent, based off the current depth and the max depth.
     *
     * @param depth The current depth in milimeters.
     * @param maxDepth The maximum depth in milimeters.
     * @return The depth progress in percent.
     * */
    private double calculateDepthProgress(double depth, double maxDepth) {
        return depth / maxDepth;
    }

    /**
     * This method adjusts the height of the depth rectangle based of the current progress.
     *
     * @param progress The progress in percent.
     * */
    private double adjustDepthRectangleHeight(double progress) {
        double height;

        // Behind the entry point
        if (progress < 0.0) {
            height = (1 - Math.min(Math.abs(progress), 1.0)) * 100;
            // Above the target point
        } else if (progress > 1.0) {
            height = 300;
            // Inside the body or exactly on the entry point, not on target point yet
        } else {
            height = 100 + Math.min(progress, 1.0) * 200;
        }
        return height;
    }

    /*----------------------------------------TIP ALIGNMENT----------------------------------------*/

    /**
     * Aligns the target cross with the given needle tip position.
     *
     * @param tipPos The needle tip position.
     * */
    private void tipAlignment(Vector3D tipPos) {
        Vector3D errorVector = calculateTipAlignmentErrorVector(tipPos);

        Vector2D projectedErrorVector = projectErrorVectorToScreenSpace(errorVector);

        translateTargetCross(projectedErrorVector.getX(), projectedErrorVector.getY());
    }

    /**
     * Calculates the translation vector directing from the tip position to the entry point.
     *
     * @param tipPos The needle tip position.
     * @return The error vector from the current tip position to the entry point.
     * */
    public Vector3D calculateTipAlignmentErrorVector(Vector3D tipPos) {
        return entryPoint.sub(tipPos);
    }

    /**
     * This method projects the 3D error vector to screen space depending on the selected plane.
     *
     * @param errorVector The error vector in 3D.
     * @return The projected error vector in 2D.
     * */
    private Vector2D projectErrorVectorToScreenSpace(Vector3D errorVector) {
        double uiH;
        double uiV;

        switch (guidanceHandler.getPlaneSelected()) {
            case XY -> {
                uiH = errorVector.getY() * TIP_ALIGNMENT_SCALE;
                uiV = -errorVector.getX() * TIP_ALIGNMENT_SCALE;
            }
            case YZ -> {
                uiH = errorVector.getY() * TIP_ALIGNMENT_SCALE;
                uiV = errorVector.getZ() * TIP_ALIGNMENT_SCALE;
            }
            // WARNING: ZX-plane not tested in the lab yet!
            // Values below are assumed and may need adjustment after testing.
            case ZX -> {
                uiH = -errorVector.getZ() * TIP_ALIGNMENT_SCALE;
                uiV = -errorVector.getX() * TIP_ALIGNMENT_SCALE;
            }
            default -> throw new IllegalStateException("Undefined plane");
        }

        return new Vector2D(uiH, uiV);
    }

    /*----------------------------------------ANGULATION----------------------------------------*/

    /**
     * This method is the main function for the guidance circle in the angulation phase.
     * <p>
     * It measures, how far the needle is angulated relative to the ideal path.
     * The tip is treated as the pivot: the aim is to rotate the needle (current) so it becomes
     * even to the should vector, meaning the angle between the two vectors is
     * exactly 0 degrees. With that, the needle (current) looks into the same direction as the path.
     * </p>
     *
     * @param tipPos The needle tip position.
     * @param rotationMatrix The needle rotation matrix.
     * */
    public void angulation(Vector3D tipPos, Matrix3D rotationMatrix) {
        Vector3D feetPos = getNeedleFeetPosition(tipPos, rotationMatrix);

        Vector3D path = targetPoint.sub(entryPoint).normalize();

        Vector3D orientation = tipPos.sub(feetPos);

        double needleLength = orientation.getMag();

        path.multLocal(needleLength);

        // Calculate point, where feet pos should be positioned
        Vector3D wantedPoint = tipPos.sub(path);

        Vector3D refUp = new Vector3D(0, 1, 0);

        // If path is almost parallel to refUp
        if (Math.abs(path.normalize().dot(refUp)) > 0.99) {
            refUp = new Vector3D(1, 0, 0);
        }

        // Get the rotated right and up axis based off the path
        Vector3D right = path.cross(refUp).normalize();
        Vector3D up = right.cross(path).normalize();

        Vector3D current = tipPos.sub(feetPos).normalize();
        Vector3D should = tipPos.sub(wantedPoint).normalize();

        // Get the rotation axis
        Vector3D rotationAxis = current.cross(should);

        // Get the horizontal and vertical angular deviation
        double x = rotationAxis.dot(right) * ANGULATION_SCALE;
        double y = -rotationAxis.dot(up)   * ANGULATION_SCALE;

        translateTargetCircle(x, y);
    }

    /**
     * This method calculates the feet position of the needle from tool to tracking space.
     *
     * @param tipPos The needle tip position.
     * @param rotationMatrix The needle rotation matrix.
     * @return The needle feet position in tracking space.
     * */
    private Vector3D getNeedleFeetPosition(Vector3D tipPos, Matrix3D rotationMatrix) {
        return tipPos.add(rotationMatrix.mult(TOOL_FEET_POS));
    }

    /*----------------------------------------SCENE-OVERLAY---------------------------------------- */

    /**
     * This method sets the camera position in the tip alignment phase.
     * <p>
     * It sets the camera position to the mapped needle tip position.
     * A z-axis offset is being taken into account.
     * </p>
     * @param tipPos The needle tip position.
     * */
    private void cameraTipAlignmentPhase(Vector3D tipPos) {
        Vector3D mappedPos = trackingToScene(tipPos);
        guidanceHandler.getCamera().getPerspectiveCamera().getTransforms().clear();
        guidanceHandler.getCamera().setPos(new Vector3D(
                mappedPos.getX(), mappedPos.getY(), mappedPos.getZ() + TIP_ALIGNMENT_CAMERA_OFFSET_Z));
    }

    /**
     * This method calculates the camera transformation representing the needle with its rotation.
     * <p>
     * It calculates the right vector using the needle forward vector and an initialized up vector.
     * Because the up and right vector are not retrieved by the needle rotation matrix,
     * the camera is only covering pitch and yaw but ignoring the roll.
     * </p>
     * @param camera The perspective camera.
     * @param forward The look direction of the needle.
     * */
    private void setLookDirection(PerspectiveCamera camera, Vector3D forward) {
        Vector3D worldUp = new Vector3D(0, 1, 0);

        Vector3D right = worldUp.cross(forward);

        // Forward parallel to worldUp vector
        if (right.getMag() < 1e-6) {
            worldUp = new Vector3D(0, 0, 1);
            right = worldUp.cross(forward);
        }
        right = right.normalize();
        Vector3D up = forward.cross(right).normalize();

        camera.getTransforms().setAll(new Affine(
                right.getX(), up.getX(), forward.getX(), 0,
                right.getY(), up.getY(), forward.getY(), 0,
                right.getZ(), up.getZ(), forward.getZ(), 0
        ));
    }

    /**
     * This method represents the camera behavior in the angulation and scene depth phase.
     * <p>
     * Other than in tip alignment phase, it updates both the camera position and its look direction.
     * It also smooths the camera rotation by a factor, if enabled.
     * </p>
     * @param tipPos The needle tip position.
     * @param quaternion The needle quaternion.
     * */
    private void cameraAngulationPhase(Vector3D tipPos, Quaternion quaternion) {
        Quaternion rotationToUse;

        if (guidanceHandler.isSlerpEnabled()) {
            if (smoothedRotation == null) {
                smoothedRotation = quaternion;
            } else {
                smoothedRotation = smoothedRotation.slerp(
                        smoothedRotation, quaternion, guidanceHandler.getSlerpFactor());
            }
            rotationToUse = smoothedRotation;
        } else {
            rotationToUse = quaternion;
        }

        Vector3D forward = retrieveForwardVector(rotationToUse.toRotationMatrix());
        Vector3D mappedDir = trackingToScene(forward);
        Vector3D mappedPos = trackingToScene(tipPos);

        setLookDirection(guidanceHandler.getCamera().getPerspectiveCamera(), mappedDir);

        guidanceHandler.getCamera().setPos(new Vector3D(
                mappedPos.getX(), mappedPos.getY(), mappedPos.getZ()));
    }

    /**
     * This method maps a vector from tracking to scene space.
     *
     * @param v The vector to map.
     * @return The mapped vector.
     * */
    private Vector3D trackingToScene(Vector3D v) {
        return switch (guidanceHandler.getPlaneSelected()) {
            case XY -> new Vector3D(-v.getY(), v.getX(), v.getZ());
            case YZ -> new Vector3D(-v.getY(), -v.getZ(), v.getX());
            // WARNING: ZX-plane not tested in the lab yet!
            // Values below are assumed and may need adjustment after testing.
            case ZX -> new Vector3D(-v.getZ(), -v.getX(), v.getY());
        };
    }

    /**
     * This method retrieves the forward vector, which represents the look direction of the needle.
     *
     * @param rotationMatrix The needle rotation matrix.
     * @return The forward vector.
     * */
    private Vector3D retrieveForwardVector(Matrix3D rotationMatrix) {
        Vector3D forward = new Vector3D(
                rotationMatrix.get(0, 2),
                rotationMatrix.get(1, 2),
                rotationMatrix.get(2, 2));

        forward.normalizeLocal();

        return forward;
    }

    /**
     * This method calculates the point where the needle tip would land if it stays in the same direction.
     * <p>
     * It uses the remaining depth to travel to the target as the distance between the needle tip position
     * and the predicted point.
     * </p>
     * @param tipPos The needle tip position.
     * @param rotationMatrix The needle rotation matrix.
     * @param depth The current depth in milimeters.
     * @param maxDepth The maximum depth in milimeters.
     * @return The predicted point.
     * */
    private Vector3D calculatePredictedPoint(Vector3D tipPos, Matrix3D rotationMatrix, double depth, double maxDepth) {
        Vector3D forward = retrieveForwardVector(rotationMatrix);

        forward.multLocal(maxDepth - depth);

        return tipPos.add(forward);
    }

    /**
     * This method calculates the distance between the predicted point and the target point.
     *
     * @param predictedPoint The predicted point.
     * @return The distance between the predicted point and the target point.
     * */
    private double calculateErrorTerm(Vector3D predictedPoint) {
        return predictedPoint.distTo(targetPoint);
    }

    /**
     * Creates a sphere and adds it at the target position. It could representate a tumor, for example.
     */
    private void addTargetSphere() {
        Vector3D mappedPos = trackingToScene(targetPoint);
        if (targetSphere == null) {
            targetSphere = new Sphere();
            targetSphere.setRadius(TARGET_SPHERE_RADIUS);

            PhongMaterial mat = new PhongMaterial();
            mat.setDiffuseColor(Color.DARKVIOLET);

            WritableImage glow = new WritableImage(1, 1);
            glow.getPixelWriter().setColor(0, 0, Color.DARKVIOLET);
            mat.setSelfIlluminationMap(glow);

            targetSphere.setMaterial(mat);

            guidanceHandler.getWorld().getChildren().add(targetSphere);
        }

        targetSphere.setTranslateX(mappedPos.getX());
        targetSphere.setTranslateY(mappedPos.getY());
        targetSphere.setTranslateZ(mappedPos.getZ());
    }

    /**
     * Removes the target sphere from the background scene overlay.
     * */
    private void disableTargetSphere() {
        guidanceHandler.getWorld().getChildren().remove(targetSphere);
        targetSphere = null;
    }

    /**
     * Updates the entry and target point for this guidance procedure.
     *
     * @param entryPoint The entry point.
     * @param targetPoint The target point.
     * */
    public void updatePlannedPoints(Vector3D entryPoint, Vector3D targetPoint) {
        this.entryPoint = entryPoint;
        this.targetPoint = targetPoint;
    }

    /*----------------------------------------UI UPDATE CALLS---------------------------------------- */

    private void adjustDepthRectangle(double height) {
        guidanceHandler.getDepthRectangle().setHeight(height);
    }

    private void translateTargetCross(double value1, double value2) {
        guidanceHandler.getTargetCross().setTranslateX(value1);
        guidanceHandler.getTargetCross().setTranslateY(value2);
    }

    private void translateTargetCircle(double value1, double value2) {
        guidanceHandler.getTargetCircle().setTranslateX(value1);
        guidanceHandler.getTargetCircle().setTranslateY(value2);
    }

    private void renderDepthLabel(double depth) {
        String formattedDepth = String.format("%.2f", depth);
        guidanceHandler.getDepthLabel().setText(formattedDepth + " mm");
    }

    private void renderDistanceLabel(double distance) {
        String formattedDistance = String.format("%.2f", distance);
        guidanceHandler.getDistanceLabel().setText(formattedDistance + " mm");
    }

    private void renderHitErrorLabel(double hitError) {
        String formattedHitError = String.format("%.2f", hitError);
        guidanceHandler.getHitErrorLabel().setText(formattedHitError + " mm");
    }

}
