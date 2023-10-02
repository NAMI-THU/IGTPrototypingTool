package algorithm;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
//import com.jme3.math.Quaternion;
import javafx.scene.transform.Transform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import shapes.CameraContainer;
import shapes.STLModel;
import util.Quaternion;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import shapes.TrackingCone;
import shapes.TrackingSphere;
import util.Matrix3D;
import util.Vector3D;

import java.io.*;
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
    public static final int CAM_MOVEMENT = 25;
    public Vector3D cam_dir = new Vector3D(0,0,1);
    private CameraContainer cameraContainer;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final TrackingService trackingService = TrackingService.getInstance();
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    private final BooleanProperty visualizeCone = new SimpleBooleanProperty(true);
    private Label statusLabel;
    private TrackingCone[] trackingCones;
    private TrackingSphere[] trackingSpheres;
    private STLModel[] stlModels;
    private ScrollPane scrollPane;
    private Group meshGroup;
    private double mouseOldX, mouseOldY = 0;

    // For orientation
    TrackingSphere s = new TrackingSphere(2, 5, Color.RED);

    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    public STLModel[] getSTLModels() {
        return stlModels;
    }

    public TrackingCone[] getTrackingCones() {
        return trackingCones;
    }
    public TrackingSphere[] getTrackingSpheres() {
        return trackingSpheres;
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
     * Loads one or multiple stl files for the visualisation
     *
     * @return a string array of the loaded stl file names
     */

    public List<File> loadStlModel() {
        StlMeshImporter importer = new StlMeshImporter();

        FileChooser fc = new FileChooser();
        fc.setTitle("Load STL File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("STL Files", "*.stl"));
        List<File> fileList = fc.showOpenMultipleDialog(new Stage());

        if (fileList != null) {
            stlModels = new STLModel[fileList.size()];
            for (int i = 0; i < fileList.size(); i++) {
                try {
                    importer.read(fileList.get(i));
                    Mesh mesh = importer.getImport();
                    String name = getSTLName(fileList.get(i));
                    stlModels[i] = new STLModel(new MeshView(mesh), name, "ccccccff", true);
                    stlModels[i].getMeshView().getTransforms().addAll(
                            //Rotate the Model by 180 degrees for correct display
                            new Rotate(180, Rotate.X_AXIS)
                    );
                    logger.log(Level.INFO, "STL file read from: " + fileList.get(i).getAbsolutePath());
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error reading STL file");
                }
            }
        }
        return fileList;
    }

    public void loadLastSTLModels() {
        JSONParser jsonParser = new JSONParser();
        StlMeshImporter importer = new StlMeshImporter();
        String[] stlNames;
        try {
            JSONObject jsonSTLModels = (JSONObject) jsonParser.parse(new FileReader("src/main/resources/json/stlFiles.json"));
            stlModels = new STLModel[jsonSTLModels.size()];
            for (int i = 0; i < jsonSTLModels.size(); i++) {
                JSONObject jsonSTLModel = (JSONObject) jsonSTLModels.get("STL " + i);
                try {
                    File file = new File(String.valueOf(jsonSTLModel.get("Path")));
                    importer.read(file);
                    Mesh mesh = importer.getImport();

                    boolean visible = Boolean.parseBoolean(jsonSTLModel.get("Visible").toString());
                    String name = (String) jsonSTLModel.get("Name");
                    String hex = (String) jsonSTLModel.get("Color");
                    hex = hex.substring(2);

                    stlModels[i] = new STLModel(new MeshView(mesh), name, hex, visible);
                    stlModels[i].getMeshView().getTransforms().addAll(
                            //Rotate the Model by 180 degrees for correct display
                            new Rotate(180, Rotate.X_AXIS)
                    );
                    double[] arr = new double[9];
                    try {
                        JSONObject jsonTransformationMatrix = (JSONObject) jsonParser.parse(new FileReader("src/main/resources/json/transformationMatrix.json"));
                        JSONArray transformationArray = (JSONArray) jsonTransformationMatrix.get("transformTracker");

                        for (int j = 0; j < 3; j++) {
                            for (int k = 0; k < 3; k++) {
                                JSONArray row = (JSONArray) transformationArray.get(j);
                                arr[k + j * 3] = (double) row.get(k);
                            }
                        }
                    } catch (IOException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                    stlModels[i].transformPosition(new Matrix3D(arr));
                    logger.log(Level.INFO, "STL file read from: " + jsonSTLModel.get("Path"));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Last STL File could not be loaded");
                    stlModels = null;
                }
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String getSTLName(File file) {
        String name = file.toString();
        int index = name.lastIndexOf("\\");
        name = name.substring(index+1, name.length() - 4);
        name = "STL " + name;
        return name;
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

            Quaternion rotationMovement = measurements.get(measurements.size() - 1).getRotation();
            double[] eulerAngles = rotationMovement.toRadiansAngles(null);

            double x = measurements.get(measurements.size() - 1).getPoint().getX();
            double y = measurements.get(measurements.size() - 1).getPoint().getY();
            double z = measurements.get(measurements.size() - 1).getPoint().getZ();
            // convert Quaternion to Euler
            double yaw = eulerAngles[0];
            double roll = eulerAngles[1];
            double pitch = eulerAngles[2];
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
                JSONParser jsonParser = new JSONParser();
                try {
                    JSONObject jsonTransformationMatrix = (JSONObject) jsonParser.parse(new FileReader("src/main/resources/json/transformationMatrix.json"));
                    JSONArray offset = (JSONArray) jsonTransformationMatrix.get("trackerOffset");
                    trackingCones[i].setTranslateX(x + (double) offset.get(0));
                    trackingCones[i].setTranslateY(y + (double) offset.get(1));
                    trackingCones[i].setTranslateZ(z + (double) offset.get(2));
                    matrixRotateNode(trackingCones[i], -pitch, -yaw, -roll);

                    trackingSpheres[i].setTranslateX(x + (double) offset.get(0));
                    trackingSpheres[i].setTranslateY(y + (double) offset.get(1));
                    trackingSpheres[i].setTranslateZ(z + (double) offset.get(2));
                    matrixRotateNode(trackingSpheres[i], -pitch, -yaw, -roll);
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }

            }

            if (stlModels != null) {
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
                for (STLModel stlModel : stlModels) {
                    if (trackingCone.getBoundsInParent().intersects(stlModel.getMeshView().getBoundsInParent())) {
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

        handleKeyboard(scrollPane, cameraContainer);
        handleMouse(subScene, cameraContainer);
    }

    /**
     * Adds Nodes and Rotation to Group and centres it
     *
     * @return Group with Model and Tracker
     */
    private Group buildScene() {
        Group root = new Group();

        // If no stlFiles are loaded
        if(stlModels != null) {
            for (STLModel model : stlModels) {
                root.getChildren().add(model.getMeshView());
            }
        }

        if (trackingCones == null && trackingSpheres == null) {
            createTrackerShape();
        }

        // If you want to have the tracking cone and the sphere
        if (trackingCones != null && trackingSpheres != null) {
            root.getChildren().addAll(trackingCones);
            root.getChildren().addAll(trackingSpheres);
        }
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
        initCamera();
        PerspectiveCamera perspectiveCamera = cameraContainer.getPerspectiveCamera();

        scene3d.setFill(Color.DARKGREY);

        scene3d.setCamera(perspectiveCamera);
        scene3d.setPickOnBounds(false);
        return scene3d;
    }

    /**
     * Initiates a new PerspectiveCamera
     *
     * @return PerspectiveCamera
     */
    private CameraContainer initCamera() {
        cameraContainer = new CameraContainer(true, rotateX, rotateY);
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonTransformationMatrix = (JSONObject) jsonParser.parse(new FileReader("src/main/resources/json/transformationMatrix.json"));
            JSONArray jsonArr = (JSONArray) jsonTransformationMatrix.get("trackerOffset");
            double[] offset = new double[jsonArr.size()];
            for (int i = 0; i < jsonArr.size(); i++) {
                offset[i] = (double) jsonArr.get(i);
            }
            Vector3D newPos = new Vector3D(offset);
            cameraContainer.setPos(newPos);
            cameraContainer.move(new Vector3D(0,0,-500));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        rotateX.setAngle(0);
        rotateY.setAngle(0);
        cam_dir = new Vector3D(0,0,1);

        return cameraContainer;
    }

    /**
     * Adds Mouse Controls to the Scene
     *
     * @param subScene Scene for MouseEvent
     * @param cameraContainer Camera
     */
    private void handleMouse(SubScene subScene, CameraContainer cameraContainer) {
        subScene.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            Vector3D movement = new Vector3D(cam_dir.get(0), cam_dir.get(1), cam_dir.get(2));
            movement.mult(deltaY);
            cameraContainer.move(movement);
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

        subScene.setOnMouseReleased(event -> {
            double[] angles = {rotateX.getAngle(), rotateY.getAngle(), 0};
            System.out.println("RotateX: " + rotateX.getAngle() + " RotateY: " + rotateY.getAngle());
            System.out.println("Camera: " + cameraContainer.getPerspectiveCamera().getRotate());
            util.Quaternion q = new util.Quaternion(angles);
            Matrix3D rotMat = q.toRotationMatrix();
            cam_dir = rotMat.mult(new Vector3D(0,0,1));
        });
    }

    /**
     * Adds Keyboard Controls to the Scene
     *
     * @param scrollPane        Pane for KeyEvent
     * @param cameraContainer Camera to be controlled
     */
    private void handleKeyboard(ScrollPane scrollPane, CameraContainer cameraContainer) {

        scrollPane.setOnKeyPressed(event -> {
            double[] angles = {rotateX.getAngle(), rotateY.getAngle(), 0};
            util.Quaternion q = new util.Quaternion(angles);
            Matrix3D rotMat = q.toRotationMatrix();
            switch (event.getCode()) {
                case S:
                case DOWN:
                    Matrix3D rotXDown = new Matrix3D(new double[] {1,0,0,0,0,1,0,-1,0});
                    Vector3D down = rotXDown.mult(cam_dir);
                    down.setMag(CAM_MOVEMENT);
                    System.out.println("Down");
                    down.print();
                    cameraContainer.move(down);
                    event.consume();
                    break;
                case W:
                case UP:
                    Matrix3D rotXUp = new Matrix3D(new double[] {1,0,0,0,0,-1,0,1,0});
                    Vector3D up = rotXUp.mult(cam_dir);
                    up.setMag(CAM_MOVEMENT);
                    System.out.println("Up");
                    up.print();
                    cameraContainer.move(up);
                    event.consume();
                    break;
                case D:
                case RIGHT:
                    Matrix3D rotYRight = new Matrix3D(new double[] {0,0,1,0,1,0,-1,0,0});
                    Vector3D right = rotYRight.mult(new Vector3D(0,0,1));
                    right = rotMat.mult(right);
                    right.setMag(CAM_MOVEMENT);
                    System.out.println("Right");
                    right.print();
                    cameraContainer.move(right);
                    break;
                case A:
                case LEFT:
                    Matrix3D rotYLeft = new Matrix3D(new double[] {0,0,-1,0,1,0,1,0,0});
                    Vector3D left = rotYLeft.mult(new Vector3D(0,0,1));
                    left = rotMat.mult(left);
                    left.setMag(CAM_MOVEMENT);
                    System.out.println("Left");
                    left.print();
                    cameraContainer.move(left);
                    event.consume();
                    break;
                case G:
                    System.out.println("CAM DIR:");
                    cam_dir.print();
                    break;
            }
        });
    }

}
