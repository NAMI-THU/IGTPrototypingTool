package algorithm;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import inputOutput.Tool;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import shapes.TrackingSphere;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to manage the 3D-Visualization.
 */
public class SceneBuilder {
//    private static SceneBuilder sceneBuilder;

    Label statusLabel;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    TrackingService trackingService = TrackingService.getInstance();

    private TrackingSphere[] trackingSpheres;
    private MeshView[] stlFiles;

    private ScrollPane scrollPane;
    private Group meshGroup;

    private static final double MODEL_SCALE_FACTOR = 10;
    private static final double MODEL_X_OFFSET = 0; // standard
    private static final double MODEL_Y_OFFSET = 0; // standard
    private static final int VIEWPORT_SIZE = 700;
    private static final int VIEWPORT_CENTER = VIEWPORT_SIZE / 2;
    private double mouseOldX, mouseOldY = 0;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    private final List<SceneBuilderObserver> observers = new ArrayList<>();


    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    public MeshView[] getMeshView() {
        return stlFiles;
    }

    public TrackingSphere[] getTrackingSpheres() {
        return trackingSpheres;
    }

    /**
     * Register an observer. Observers are triggered when the references to any attribute are changed.
     *
     * @param observer The observer that shall be triggered
     */
    public void registerObserver(SceneBuilderObserver observer) {
        observers.add(observer);
        // Inform them about the initial status
        observer.onViewChanged(scrollPane != null, meshGroup != null);
    }

    /**
     * Sets the reference to the ScrollPane.
     *
     * @param scrollPane new scrollPane
     */
    public void changePane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;

        // Trigger all observers
        observers.forEach(observer -> observer.onViewChanged(true, false));
    }

    /**
     * Sets the reference to the MeshGroup.
     *
     * @param meshGroup new meshGroup
     */
    public void changeMeshGroup(Group meshGroup) {
        this.meshGroup = meshGroup;

        // Trigger all observers
        observers.forEach(observer -> observer.onViewChanged(false, true));
    }

    /**
     * Creates a Tracking Sphere for each device
     */
    private void loadTrackingSpheres() {
        if (trackingService.getTrackingDataSource() == null) {
            statusLabel.setText("No Tracking Data Source");
            return;
        }
        statusLabel.setText("");

        // update after CSV/IGT data has been loaded
        trackingService.getTrackingDataSource().update();
        ArrayList<Tool> tools = trackingService.getTrackingDataSource().getLastToolList();

        if (tools != null) {
            trackingSpheres = new TrackingSphere[tools.size()];

            for (int i = 0; i < trackingSpheres.length; i++) {
                trackingSpheres[i] = new TrackingSphere(4, 8, Color.RED);
            }
        }
    }

    /**
     * loads an STL File for Visualization
     */
    private void loadStlModel() {
        StlMeshImporter importer = new StlMeshImporter();
        FileChooser fc = new FileChooser();
        fc.setTitle("Load STL File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("STL Files", "*.stl"));
        List<File> fileList = fc.showOpenMultipleDialog(new Stage());

        if (fileList != null) {
            stlFiles = new MeshView[fileList.size()];
            int x = 0;

            for (File file : fileList) {
                try {
                    importer.read(file);
                    Mesh mesh = importer.getImport();
                    stlFiles[x] = new MeshView(mesh);

                    x++;

                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error reading STL file");
                }
            }
        }
//        meshViewEmpty.set(false);
    }

    /**
     * Start the visualization of tracking data that is read from CSV or IGT connection
     */
    public void startVisualization() {
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
        if (tools.isEmpty()) return;
        int j = 0;

        for (ToolMeasure tool : tools) {

            // TODO: foreach trackingDevice / sphere? -- 1 tool = 1 sphere

//            position.put(tool.getName(), new Label(tool.getName() + ": [-]"));
//            posBox.getChildren().add(position.get(tool.getName()));
//            rotation.put(tool.getName(), new Label(tool.getName() + ": [-]"));
//            rotBox.getChildren().add(rotation.get(tool.getName()));

            List<Measurement> li = tool.getMeasurement();

            //use the last 5 measurements, otherwise blending will be a problem during motion
            for (int i = 1; i < 5; i++) {
                if (li.size() - i < 0) {
                    break;
                }

                double x = li.get(li.size() - i).getPoint().getX();
                // invert Y axis for correct display!
                double y = li.get(li.size() - i).getPoint().getY() * -1;
                // invert tracking data, so fits the experiment's setup
                double z = li.get(li.size() - i).getPoint().getZ() * -1;

                // display position and rotation of tool
                if (i == 1) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    double qX = li.get(li.size() - i).getRotation().getX();
                    double qY = li.get(li.size() - i).getRotation().getY();
                    double qZ = li.get(li.size() - i).getRotation().getZ();
                    double qR = li.get(li.size() - i).getRotation().getW();

                    float[] angles = new float[3];

                    Quaternion quaternion1 = li.get(li.size() - i).getRotation();
//                    System.out.println(quaternion1);

                    Quaternion quaternion2 = tool.getAverageRotation();
                    System.out.println(quaternion2);

                    // toAngles returns this quaternion converted to Euler rotation angles (Y yaw 0,Z roll 1,X pitch 2)
                    quaternion2.toAngles(angles);
//                    System.out.println(Arrays.toString(angles));

                    float yaw = angles[0] * FastMath.RAD_TO_DEG;
                    float roll = angles[1] * FastMath.RAD_TO_DEG;
                    float pitch = angles[2] * FastMath.RAD_TO_DEG;

                    //TODO
                    //  The unit quaternion (w, x, y, z) represents a rotation about the axis (x, y, z) of an angle of 2*cos-1(w).
                    //  Note that this is only true of unit quaternions. Non-unit quaternions do not represent rotations.

                    trackingSpheres[j].getTransforms().setAll(
                            new Translate(x, y, z),
                            new Rotate(pitch, Rotate.X_AXIS),
                            new Rotate(roll, Rotate.Y_AXIS),
                            new Rotate(yaw, Rotate.Z_AXIS)
                    );

                    //TODO
                    //  X -> Pitch
                    //  Y -> Roll
                    //  Z -> Yaw

//                    position.get(tool.getName()).setText(tool.getName() + ": ["
//                            + df.format(x) + ";"
//                            + df.format(y) + ";"
//                            + df.format(z) + "]");
//                    rotation.get(tool.getName()).setText(tool.getName() + ": ["
//                            + df.format(qY) + ";"
//                            + df.format(qZ) + ";"
//                            + df.format(qR) + "]"
//                            + pitch + "° ;"
//                            + roll + "° ;"
//                            + yaw + "° ]");
                }
            }
            // loop through trackingDevices - spheres
            j++;
        }
    }

    public void showFigure(Group meshGroup, ScrollPane scrollPane) {
        if (trackingService.getTrackingDataSource() == null) {
            statusLabel.setText("Select Tracking Data Source first");
            return;
        }
        statusLabel.setText("");
        // Load STL file
        loadStlModel();
        // Create spheres for each trackingDevice
        loadTrackingSpheres();
        // Add MeshView to Group
        Group root = buildScene();
        // Create subScene
        SubScene subScene = createScene3D(root, meshGroup);
        // Add subScene to meshGroup
        meshGroup.getChildren().add(subScene);
//        trackingDataController.getMeshGroup().getChildren().add(subScene2);

        // size will not impact the size of parent and resizing will also work when reducing the AnchorPane object's size
        subScene.setManaged(false);

        handleKeyboard(scrollPane, subScene.getCamera());
        handleMouse(subScene, root);
    }

    public void showFigureTest() {
        if (trackingService.getTrackingDataSource() == null || stlFiles == null) {
            return;
        }

        statusLabel.setText("");
        // Add MeshView to Group
        Group root = buildScene();
        // Create subScene
        SubScene subScene = createScene3D(root, meshGroup);
        // Add subScene to meshGroup
        meshGroup.getChildren().add(subScene);
//        trackingDataController.getMeshGroup().getChildren().add(subScene2);

        // size will not impact the size of parent and resizing will also work when reducing the AnchorPane object's size
        subScene.setManaged(false);

        handleKeyboard(scrollPane, subScene.getCamera());
        handleMouse(subScene, root);
    }

    /**
     * Adds Nodes and Rotation to Group and centres it
     *
     * @return Group with STL Model and TrackingSpheres
     */
    private Group buildScene() {
        Group root = new Group();

        //TODO: find a way to load subsequent STL

//        for (MeshView view : stlFiles) {
//            // centre every mesh
//            view.setTranslateX(VIEWPORT_CENTER + MODEL_X_OFFSET);
//            view.setTranslateY(VIEWPORT_CENTER + MODEL_Y_OFFSET);
//            view.setTranslateZ(VIEWPORT_CENTER);
//            view.setScaleX(MODEL_SCALE_FACTOR);
//            view.setScaleY(MODEL_SCALE_FACTOR);
//            view.setScaleZ(MODEL_SCALE_FACTOR);
//        }

        root.setRotationAxis(Rotate.Y_AXIS);
        root.setRotate(0);

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
        if (trackingSpheres != null) {
            root.getChildren().addAll(trackingSpheres);
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

    private SubScene createScene3D(Group root, Group meshGroup) {
        SubScene scene3d = new SubScene(root, VIEWPORT_SIZE, VIEWPORT_SIZE, true, SceneAntialiasing.BALANCED);
        scene3d.widthProperty().bind(((AnchorPane) meshGroup.getParent()).widthProperty());
        scene3d.heightProperty().bind(((AnchorPane) meshGroup.getParent()).heightProperty());
        PerspectiveCamera perspectiveCamera = initCamera();

        scene3d.setFill(Color.DARKGREY);
        scene3d.setCamera(perspectiveCamera);
        scene3d.setPickOnBounds(true);
        return scene3d;
    }

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

    private void handleKeyboard(ScrollPane scrollPane, Camera perspectiveCamera) {

        scrollPane.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> {
                    perspectiveCamera.translateYProperty().set(perspectiveCamera.getTranslateY() - 100);
                    event.consume();
                }
                case A -> {
                    perspectiveCamera.translateYProperty().set(perspectiveCamera.getTranslateY() + 100);
                    event.consume();
                }
                case S -> {
                    perspectiveCamera.translateXProperty().set(perspectiveCamera.getTranslateX() + 100);
                    event.consume();
                }
                case D -> {
                    perspectiveCamera.translateXProperty().set(perspectiveCamera.getTranslateX() - 100);
                    event.consume();
                }
            }
        });
    }

}
