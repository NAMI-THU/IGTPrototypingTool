package algorithm;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import shapes.CameraContainer;
import shapes.STLModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.SmartGroup;
import util.Vector3D;

import java.io.*;
import java.nio.file.Files;
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
    private CameraContainer cameraContainer;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final TrackingService trackingService = TrackingService.getInstance();
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    private double centerX, centerY, centerZ;
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);
    private final BooleanProperty visualizeCone = new SimpleBooleanProperty(true);
    private Label statusLabel;
    private ArrayList<STLModel> stlModels;
    private ScrollPane scrollPane;
    private Group meshGroup;

    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    public ArrayList<STLModel> getSTLModels() {
        return stlModels;
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
     * Loads one or multiple stl files for the visualisation
     *
     * @return a string array of the loaded stl file names
     */

    public List<File> loadStlModel() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load STL File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("STL Files", "*.stl"));
        List<File> fileList = fc.showOpenMultipleDialog(new Stage());

        if (fileList != null) {
            stlModels = new ArrayList<>();
            loadNewSTLModel(fileList, 0);
        }
        return fileList;
    }

    public void loadLastSTLModels() {
        StlMeshImporter importer = new StlMeshImporter();
        try {
            var jsonString = Files.readString(new File("src/main/resources/json/stlFiles.json").toPath());
            JSONObject jsonSTLModels = new JSONObject(jsonString);
            stlModels = new ArrayList<>();
            for (int i = 0; i < jsonSTLModels.length(); i++) {
                JSONObject jsonSTLModel = jsonSTLModels.getJSONObject("STL " + i);
                try {
                    File file = new File(jsonSTLModel.getString("Path"));
                    importer.read(file);
                    Mesh mesh = importer.getImport();

                    boolean visible = jsonSTLModel.getBoolean("Visible");
                    String name = jsonSTLModel.getString("Name");
                    String hex = jsonSTLModel.getString("Color");
                    hex = hex.substring(2);

                    stlModels.add(new STLModel(new MeshView(mesh), name, hex, visible));
                    stlModels.get(i).getMeshView().getTransforms().addAll(
                            //Rotate the Model by 180 degrees for correct display
                            new Rotate(180, Rotate.X_AXIS)
                    );
                    logger.log(Level.INFO, "STL file read from: " + jsonSTLModel.get("Path"));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Last STL File could not be loaded");
                    stlModels = null;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<File> addSTLModels() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load STL File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("STL Files", "*.stl"));
        List<File> fileList = fc.showOpenMultipleDialog(new Stage());
        if (fileList != null) {
            loadNewSTLModel(fileList, stlModels.size());
        }

        return fileList;
    }

    private void loadNewSTLModel(List<File> fileList, int indexOffset) {
        for (int i = 0; i < fileList.size(); i++) {
            try {
                StlMeshImporter importer = new StlMeshImporter();

                importer.read(fileList.get(i));
                Mesh mesh = importer.getImport();
                String name = getSTLName(fileList.get(i));
                stlModels.add(new STLModel(new MeshView(mesh), name, "ccccccff", true));
                stlModels.get(i + indexOffset).getMeshView().getTransforms().addAll(
                        //Rotate the Model by 180 degrees for correct display
                        new Rotate(180, Rotate.X_AXIS)
                );
                //logger.log(Level.INFO, "STL file read from: " + fileList.get(i).getAbsolutePath());
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error reading STL file");
            }
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
        List<Tool> tools = trackingService.getDataService().loadNextData(1);

        for (Tool tool : tools) {
            tool.show();
            tool.checkBounds(stlModels);
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
        SmartGroup root = buildScene();
        // Create subScene
        SubScene subScene = createScene3D(root);
        // Add subScene to meshGroup
        meshGroup.getChildren().add(subScene);
        // size will not impact the size of parent and resizing will also work when reducing the AnchorPane object's size
        subScene.setManaged(false);

        handleKeyboard(scrollPane, cameraContainer);
        handleMouse(subScene, root);
    }

    /**
     * Adds Nodes and Rotation to Group and centres it
     *
     * @return Group with Model and Tracker
     */
    private SmartGroup buildScene() {
        SmartGroup root = new SmartGroup();

        // If no stlFiles are loaded
        if(stlModels != null) {
            Group stlGroup = new Group();
            for (STLModel model : stlModels) {
                stlGroup.getChildren().add(model.getMeshView());
            }
            centerX = stlGroup.getBoundsInParent().getCenterX();
            centerY = stlGroup.getBoundsInParent().getCenterY();
            centerZ = stlGroup.getBoundsInParent().getCenterZ();

            root.getChildren().add(stlGroup);
        }
        if (trackingService.getDataService() != null) {
            List<Tool> tools = trackingService.getDataService().loadNextData(1);
            for (Tool tool : tools) {
                tool.addVisualizationToRoot(root);
            }
        }

        rotateX = new Rotate(0, centerX, centerY, centerZ, Rotate.X_AXIS);
        rotateY = new Rotate(0, centerX, centerY, centerZ, Rotate.Y_AXIS);
        root.getTransforms().addAll(rotateX, rotateY);

        return root;
    }

    /**
     * Creates a new 3d SubScene
     *
     * @param root Group containing the nodes Model and Tracker
     * @return 3d SubScene
     */
    private SubScene createScene3D(SmartGroup root) {
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
    private void initCamera() {
        cameraContainer = new CameraContainer(true);
        Vector3D newPos = new Vector3D(centerX, centerY, centerZ);
        cameraContainer.setPos(newPos);
        cameraContainer.move(new Vector3D(0,0,-500));
    }

    /**
     * Adds Mouse Controls to the Scene
     *
     * @param subScene Scene for MouseEvent
     * @param root SmartGroup
     */
    private void handleMouse(SubScene subScene, SmartGroup root) {
        subScene.setOnScroll(event -> {
            double delta = event.getDeltaY();
            root.setTranslateZ(root.getTranslateZ() - delta);
        });

        rotateX.angleProperty().bind(angleX);
        rotateY.angleProperty().bind(angleY);

        subScene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = angleX.get();
            anchorAngleY = angleY.get();
        });

        subScene.setOnMouseDragged(event -> {
            angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
            angleY.set(anchorAngleY + (anchorX - event.getSceneX()));
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
            switch (event.getCode()) {
                case S, DOWN -> {
                    Vector3D down = new Vector3D(0, 1, 0);
                    down.setMag(CAM_MOVEMENT);
                    cameraContainer.move(down);
                    event.consume();
                }
                case W, UP -> {
                    Vector3D up = new Vector3D(0, -1, 0);
                    up.setMag(CAM_MOVEMENT);
                    cameraContainer.move(up);
                    event.consume();
                }
                case D, RIGHT -> {
                    Vector3D right = new Vector3D(1, 0, 0);
                    right.setMag(CAM_MOVEMENT);
                    cameraContainer.move(right);
                }
                case A, LEFT -> {
                    Vector3D left = new Vector3D(-1, 0, 0);
                    left.setMag(CAM_MOVEMENT);
                    cameraContainer.move(left);
                    event.consume();
                }
            }
        });
    }

    public void resetView() {
        angleX.set(0);
        angleY.set(0);
    }
}
