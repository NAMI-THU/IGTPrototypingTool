package algorithm;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import com.jme3.math.Quaternion;
import inputOutput.Tool;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import shapes.TrackingCone;
import shapes.TrackingSphere;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to manage the 3D-Visualization.
 */
public class VisualizationManager {

    private static final double MODEL_SCALE_FACTOR = 10;
    private static final double MODEL_X_OFFSET = 0; // standard
    private static final double MODEL_Y_OFFSET = 0; // standard
    private static final int VIEWPORT_SIZE = 800;
    private static final int VIEWPORT_CENTER = VIEWPORT_SIZE / 2;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final TrackingService trackingService = TrackingService.getInstance();
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    private final BooleanProperty visualizeCone = new SimpleBooleanProperty(true);
    private Label statusLabel;
    private TrackingCone[] trackingCones;
    private TrackingSphere[] trackingSpheres;
    private MeshView[] stlFiles;
    private ScrollPane scrollPane;
    private Group meshGroup;
    private double mouseOldX, mouseOldY = 0;

    // For orientation
    TrackingSphere s = new TrackingSphere(2, 5, Color.RED);

    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    public MeshView[] getMeshView() {
        return stlFiles;
    }

    public TrackingCone[] getTrackingCones() {
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

    public BooleanProperty visualizeCone() {
        return visualizeCone;
    }

    /**
     * Creates a Tracker Shape for each device
     */
    private void createTrackerShape() {
        // Hier werden NUR die Shapes erstellt
        if (trackingService.getTrackingDataSource() == null) {
            statusLabel.setText("No Tracking Data Source");
            return;
        }
        statusLabel.setText("");

        // update after CSV/IGT data has been loaded
        trackingService.getTrackingDataSource().update();
        ArrayList<Tool> tools = trackingService.getTrackingDataSource().getLastToolList();

        if (tools != null) {
            trackingCones = new TrackingCone[tools.size()];

            for (int i = 0; i < trackingCones.length; i++) {
                trackingCones[i] = new TrackingCone(36, 4, 10);
            }

            trackingSpheres = new TrackingSphere[tools.size()];
            Color[] col = {Color.BLUE, Color.RED};
            for (int i = 0; i < trackingSpheres.length; i++) {
                trackingSpheres[i] = new TrackingSphere(2, 5, col[i], col[i]);
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
                    stlFiles[i].getTransforms().addAll(
                            //Rotate the Model by 180 degrees for correct display
                            new Rotate(180, Rotate.X_AXIS)
                    );
                    logger.log(Level.INFO, "STL file read from: " + fileList.get(i).getAbsolutePath());
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

            Quaternion rotationMovement = measurements.get(measurements.size() - 1).getRotation();
            rotationMovement.toAngles(eulerAngles);

            double x = measurements.get(measurements.size() - 1).getPoint().getX();
            double y = measurements.get(measurements.size() - 1).getPoint().getY();
            double z = measurements.get(measurements.size() - 1).getPoint().getZ();
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
                System.out.println("yawAngle!" + yawAngle);
                System.out.println("roll:!" + roll);
                System.out.println("rollAngle!" + rollAngle);
                System.out.println("pitch:!" + pitch);
                System.out.println("pitchAngle!" + pitchAngle);
                System.out.print("\n");
            }

            if (trackingCones != null && trackingSpheres != null) {
                trackingCones[i].setTranslateX(x);
                trackingCones[i].setTranslateY(y);
                trackingCones[i].setTranslateZ(z);
                matrixRotateNode(trackingCones[i], -pitch, -yaw, -roll);

                trackingSpheres[i].setTranslateX(x);
                trackingSpheres[i].setTranslateY(y);
                trackingSpheres[i].setTranslateZ(z);
                matrixRotateNode(trackingSpheres[i], -pitch, -yaw, -roll);
            }

            if (stlFiles != null) {
                checkBounds();
            }
        }
    }

    /**
     * Rotate the Node by providing three rotations (Yaw, Roll, Pitch)
     * author Jose Pereda
     *
     * @param n   Node to rotate
     * @param alf Euler Angle alf
     * @param bet Euler Angle bet
     * @param gam Euler Angle gam
     */
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
    }

    /**
     * Checks for collision and changes tracker color accordingly
     * Color red implies a collision with the model was detected
     * Color green implies no collision was detected
     */
    private void checkBounds() {
        if (visualizeCone.get()) {
            for (TrackingCone trackingCone : trackingCones) {
                for (MeshView stlFile : stlFiles
                ) {
                    if (trackingCone.getBoundsInParent().intersects(stlFile.getBoundsInParent())) {
                        trackingCone.setMaterial(new PhongMaterial(Color.RED));
                    }
                }
            }
        }
    }

    /**
     * Adds the Nodes and Controls to the Scene
     */
    public void showFigure() {
        if (trackingService.getTrackingDataSource() == null) {
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

        // If no stlFiles are loaded
        if(stlFiles != null) {
            root.getChildren().addAll(stlFiles);
        }

        if (trackingCones == null && trackingSpheres == null) {
            createTrackerShape();
        }

        // If you want to have the tracking cone and the sphere
        if (trackingCones != null && trackingSpheres != null) {
            root.getChildren().addAll(trackingCones);
            root.getChildren().addAll(trackingSpheres);
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
                    perspectiveCamera.translateYProperty().set(perspectiveCamera.getTranslateY() - 100);
                    event.consume();
                    break;
                case W:
                case UP:
                    perspectiveCamera.translateYProperty().set(perspectiveCamera.getTranslateY() + 100);
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
