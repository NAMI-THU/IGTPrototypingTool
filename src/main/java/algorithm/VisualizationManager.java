package algorithm;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import com.jme3.math.Quaternion;
import inputOutput.Tool;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import shapes.ConeMesh;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to manage the 3D-Visualization.
 */
public class VisualizationManager {

    private Label statusLabel;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final TrackingService trackingService = TrackingService.getInstance();

    private ConeMesh[] trackingCones;
    private MeshView[] stlFiles;

    private ScrollPane scrollPane;
    private Group meshGroup;

    private static final double MODEL_SCALE_FACTOR = 10;
    private static final double MODEL_X_OFFSET = 0; // standard
    private static final double MODEL_Y_OFFSET = 0; // standard
    private static final int VIEWPORT_SIZE = 800;
    private static final int VIEWPORT_CENTER = VIEWPORT_SIZE / 2;
    private double mouseOldX, mouseOldY = 0;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);


    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    public MeshView[] getMeshView() {
        return stlFiles;
    }

    public ConeMesh[] getTrackingCones() {
        return trackingCones;
    }

    /**
     * Sets the reference to the ScrollPane.
     *
     * @param scrollPane new scrollPane
     */
    public void setPane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    /**
     * Sets the reference to the MeshGroup.
     *
     * @param meshGroup new meshGroup
     */
    public void setMeshGroup(Group meshGroup) {
        this.meshGroup = meshGroup;
    }

//    public void setViewportSize(double VIEWPORT_SIZE) {
//        this.VIEWPORT_SIZE = VIEWPORT_SIZE;
//        this.VIEWPORT_CENTER = VIEWPORT_SIZE / 2;
//    }


    /**
     * Creates a Tracking Cone for each device
     */
    private void createTrackingCone() {
        if (trackingService.getTrackingDataSource() == null) {
            statusLabel.setText("No Tracking Data Source");
            return;
        }
        statusLabel.setText("");

        // update after CSV/IGT data has been loaded
        trackingService.getTrackingDataSource().update();
        ArrayList<Tool> tools = trackingService.getTrackingDataSource().getLastToolList();

        if (tools != null) {
            trackingCones = new ConeMesh[tools.size()];

            for (int i = 0; i < trackingCones.length; i++) {
                trackingCones[i] = new ConeMesh(36, 4, 10);
            }
        }
    }

    /**
     * loads an STL File for Visualization
     */
    public void loadStlModel() {
        StlMeshImporter importer = new StlMeshImporter();
        FileChooser fc = new FileChooser();
        fc.setTitle("Load STL File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("STL Files", "*.stl"));
        List<File> fileList = fc.showOpenMultipleDialog(new Stage());

        if (fileList != null) {
            stlFiles = new MeshView[fileList.size()];

            for (int i = 0; i < fileList.size(); i++) {
                try {
                    importer.read(fileList.get(i));
                    Mesh mesh = importer.getImport();
                    stlFiles[i] = new MeshView(mesh);

                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error reading STL file");
                }
            }
        }
    }

    /**
     * Visualizes the tracking tools by updating their position and rotation according to
     * the tracking data read from CSV or IGT connection
     */
    public void visualizeTracking() {
        if (trackingService.getTrackingDataSource() == null) {
            statusLabel.setText("No Tracking Data Source");
            return;
        }
        if (stlFiles == null) {
            statusLabel.setText("Load STL Model for Visualization");
            return;
        }
        // timeline has not been started in trackingData view
        if (trackingService.getTimeline() == null) {
            statusLabel.setText("Start Tracking in Tracking Data View first");
            return;
        }
        statusLabel.setText("");

        // loads the next set of tracking data
        trackingService.getTrackingDataSource().update();
        List<ToolMeasure> tools = trackingService.getDataService().loadNextData(1);

        for (int i = 0; i < tools.size(); i++) {
            List<Measurement> measurements = tools.get(i).getMeasurement();
            float[] eulerAngles = new float[3];

            Quaternion rotationMovement = tools.get(i).getMeasurement().get(measurements.size() - 1).getRotation();
            rotationMovement.toAngles(eulerAngles);

            double x = tools.get(i).getMeasurement().get(measurements.size() - 1).getPoint().getX();
            // invert Y & Z axis for correct display!
            double y = tools.get(i).getMeasurement().get(measurements.size() - 1).getPoint().getY() * -1;
            double z = tools.get(i).getMeasurement().get(measurements.size() - 1).getPoint().getZ() * -1;
            // convert Quaternion to Euler
            float yaw = eulerAngles[0];
            float roll = eulerAngles[1];
            float pitch = eulerAngles[2];
            // convert Euler to angles
            double yawAngle = Math.toDegrees(yaw);
            double rollAngle = Math.toDegrees(roll);
            double pitchAngle = Math.toDegrees(pitch);

            boolean showRotations = false;
            if (showRotations) {
                System.out.println(tools.get(i).getName());
                System.out.println("yaw:!" + yaw);
                System.out.println("angle!" + yawAngle);
                System.out.println("roll:!" + roll);
                System.out.println("angle!" + rollAngle);
                System.out.println("pitch:!" + pitch);
                System.out.println("angle!" + pitchAngle);
                System.out.print("\n");
            }

            // apply translation and rotation to each tracker
//            trackingCones[i].getTransforms().setAll(
//                    new Translate(x, y, z),
//                    new Rotate(yawAngle, Rotate.Z_AXIS),
//                    new Rotate(pitchAngle, Rotate.X_AXIS),
//                    new Rotate(rollAngle, Rotate.Y_AXIS)
//            );

//            Alternative1
//            trackingCones[i].setTranslateX(x);
//            trackingCones[i].setTranslateY(y);
//            trackingCones[i].setTranslateZ(z);
//            matrixRotateNode(trackingCones[i], yaw, roll, pitch);

//            Alternative2
            trackingCones[i].setTranslateX(x);
            trackingCones[i].setTranslateY(y);
            trackingCones[i].setTranslateZ(z);
//            addRotate(trackingCones[i], trackingCones[i].ry, rollAngle);
//            addRotate(trackingCones[i], trackingCones[i].rx, pitchAngle);
//            addRotate(trackingCones[i], trackingCones[i].rz, yawAngle);

            checkBounds();
        }
    }

    private void matrixRotateNode(Node n, double alf, double bet, double gam) {
        double A11 = Math.cos(alf) * Math.cos(gam);
        double A12 = Math.cos(bet) * Math.sin(alf) + Math.cos(alf) * Math.sin(bet) * Math.sin(gam);
        double A13 = Math.sin(alf) * Math.sin(bet) - Math.cos(alf) * Math.cos(bet) * Math.sin(gam);
        double A21 = -Math.cos(gam) * Math.sin(alf);
        double A22 = Math.cos(alf) * Math.cos(bet) - Math.sin(alf) * Math.sin(bet) * Math.sin(gam);
        double A23 = Math.cos(alf) * Math.sin(bet) + Math.cos(bet) * Math.sin(alf) * Math.sin(gam);
        double A31 = Math.sin(gam);
        double A32 = -Math.cos(gam) * Math.sin(bet);
        double A33 = Math.cos(bet) * Math.cos(gam);

        double d = Math.acos((A11 + A22 + A33 - 1d) / 2d);
        if (d != 0d) {
            double den = 2d * Math.sin(d);
            Point3D p = new Point3D((A32 - A23) / den, (A13 - A31) / den, (A21 - A12) / den);
            n.setRotationAxis(p);
            n.setRotate(Math.toDegrees(d));
        }
        //where alf is roll, bet is pitch and gam is yaw.
    }

    private void addRotate(Node node, Rotate rotate, double angle) {
        Affine affine = node.getTransforms().isEmpty() ? new Affine() : new Affine(node.getTransforms().get(0));
        double A11 = affine.getMxx(), A12 = affine.getMxy(), A13 = affine.getMxz();
        double A21 = affine.getMyx(), A22 = affine.getMyy(), A23 = affine.getMyz();
        double A31 = affine.getMzx(), A32 = affine.getMzy(), A33 = affine.getMzz();

        Rotate newRotateX = new Rotate(angle, new Point3D(A11, A21, A31));
        Rotate newRotateY = new Rotate(angle, new Point3D(A12, A22, A32));
        Rotate newRotateZ = new Rotate(angle, new Point3D(A13, A23, A33));

        affine.prepend(rotate.getAxis() == Rotate.X_AXIS ? newRotateX :
                rotate.getAxis() == Rotate.Y_AXIS ? newRotateY : newRotateZ);

        node.getTransforms().setAll(affine);
    }

    /**
     * Checks for collision and changes tracker color accordingly
     * Color red implies a collision with the model was detected
     * Color green implies no collision was detected
     */
    private void checkBounds() {
        for (ConeMesh trackingCone : trackingCones) {
            for (MeshView stlFile : stlFiles
            ) {
                if (trackingCone.getBoundsInParent().intersects(stlFile.getBoundsInParent())) {
                    trackingCone.setMaterial(new PhongMaterial(Color.RED));
                }
            }
        }
    }

    /**
     * Adds the Nodes and Controls to the Scene
     */
    public void showFigure() {
        if (trackingService.getTrackingDataSource() == null || stlFiles == null) {
            return;
        }
        statusLabel.setText("");
        // Add MeshView to Group
        Group root = buildScene();
        // Create subScene
        SubScene subScene = createScene3D(root);
        // Add subScene to meshGroup
        meshGroup.getChildren().add(subScene);
        // size will not impact the size of parent and resizing will also work when reducing the AnchorPane object's size
        subScene.setManaged(false);

        handleKeyboard(scrollPane, subScene.getCamera());
        handleMouse(subScene, root);
    }

    /**
     * Adds Nodes and Rotation to Group and centres it
     *
     * @return Group with Model and Tracker
     */
    private Group buildScene() {
        Group root = new Group();

        if (trackingCones == null) {
            createTrackingCone();
        }

        rotateX.setPivotX(VIEWPORT_CENTER + MODEL_X_OFFSET);
        rotateX.setPivotY(VIEWPORT_CENTER + MODEL_Y_OFFSET);
        rotateX.setPivotZ(VIEWPORT_CENTER);

        rotateY.setPivotX(VIEWPORT_CENTER + MODEL_X_OFFSET);
        rotateY.setPivotY(VIEWPORT_CENTER + MODEL_Y_OFFSET);
        rotateY.setPivotZ(VIEWPORT_CENTER);

        rotateZ.setPivotX(VIEWPORT_CENTER + MODEL_X_OFFSET);
        rotateZ.setPivotY(VIEWPORT_CENTER + MODEL_Y_OFFSET);
        rotateZ.setPivotZ(VIEWPORT_CENTER);

        root.getChildren().addAll(stlFiles);
        if (trackingCones != null) {
            root.getChildren().addAll(trackingCones);
        }

        // centre the node
        root.setTranslateX(VIEWPORT_CENTER + MODEL_X_OFFSET);
        root.setTranslateY(VIEWPORT_CENTER + MODEL_Y_OFFSET);
        root.setTranslateZ(VIEWPORT_CENTER);
        root.setScaleX(MODEL_SCALE_FACTOR);
        root.setScaleY(MODEL_SCALE_FACTOR);
        root.setScaleZ(MODEL_SCALE_FACTOR);

        return root;
    }

    /**
     * Creates a new 3d SubScene
     *
     * @param root Group containing the nodes Model and Tracker
     * @return 3d SubScene
     */
    private SubScene createScene3D(Group root) {
        SubScene scene3d = new SubScene(root, VIEWPORT_SIZE, VIEWPORT_SIZE, true, SceneAntialiasing.BALANCED);
        scene3d.widthProperty().bind(((AnchorPane) meshGroup.getParent()).widthProperty());
        scene3d.heightProperty().bind(((AnchorPane) meshGroup.getParent()).heightProperty());
        PerspectiveCamera perspectiveCamera = initCamera();

        scene3d.setFill(Color.DARKGREY);
        scene3d.setCamera(perspectiveCamera);
        scene3d.setPickOnBounds(true);
        return scene3d;
    }

    /**
     * Initiates a new PerspectiveCamera
     *
     * @return PerspectiveCamera
     */
    private PerspectiveCamera initCamera() {
        PerspectiveCamera perspectiveCamera = new PerspectiveCamera(false);
        perspectiveCamera.setTranslateX(0);
        perspectiveCamera.setTranslateY(0);
        perspectiveCamera.setTranslateZ(0);
        perspectiveCamera.setNearClip(0.1);
        perspectiveCamera.setFarClip(1000.0);

        perspectiveCamera.getTransforms().addAll(rotateX, rotateY, new Translate(0, 0, -1000));
        return perspectiveCamera;
    }

    /**
     * Adds Mouse Controls to the Scene
     *
     * @param subScene Scene for MouseEvent
     * @param root     Group containing the nodes to be controlled
     */
    private void handleMouse(SubScene subScene, Group root) {
        subScene.setOnScroll(event -> {
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
                zoomFactor = 2.0 - zoomFactor;
            }
            root.setScaleX(root.getScaleX() * zoomFactor);
            root.setScaleY(root.getScaleY() * zoomFactor);
            root.setScaleZ(root.getScaleZ() * zoomFactor);
            event.consume();
        });
        subScene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        subScene.setOnMouseDragged(event -> {
            rotateX.setAngle(rotateX.getAngle() - (event.getSceneY() - mouseOldY));
            rotateY.setAngle(rotateY.getAngle() + (event.getSceneX() - mouseOldX));
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();

        });
    }

    /**
     * Adds Keyboard Controls to the Scene
     *
     * @param scrollPane        Pane for KeyEvent
     * @param perspectiveCamera Camera to be controlled
     */
    private void handleKeyboard(ScrollPane scrollPane, Camera perspectiveCamera) {

        scrollPane.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case S:
                case DOWN:
                    perspectiveCamera.translateYProperty().set(perspectiveCamera.getTranslateY() + 100);
                    event.consume();
                    break;
                case W:
                case UP:
                    perspectiveCamera.translateYProperty().set(perspectiveCamera.getTranslateY() - 100);
                    event.consume();
                    break;
                case D:
                case RIGHT:
                    perspectiveCamera.translateXProperty().set(perspectiveCamera.getTranslateX() + 100);
                    event.consume();
                    break;
                case A:
                case LEFT:
                    perspectiveCamera.translateXProperty().set(perspectiveCamera.getTranslateX() - 100);
                    event.consume();
                    break;
            }
        });
    }

}
