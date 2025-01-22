package algorithm;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import org.json.JSONObject;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
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
import shapes.Target;
import tracking.Tool;
import tracking.TrackingService;
import util.HardwareStatus;
import util.Vector3D;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to manage the 3D-Visualization.
 */
public class VisualizationManager {
    private static final int VIEWPORT_SIZE = 800;
    /**
     * Determines how much the camera moves
     */
    public static final int CAM_MOVEMENT = 25;
    /**
     * A helper container who contains a camera
     */
    private CameraContainer cameraContainer;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final TrackingService trackingService = TrackingService.getInstance();
    /**
     * A rotate object around the x-axis, used for the rotation of the visualisation
     */
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    /**
     * A rotate object around the y-axis, used for the rotation of the visualisation
     */
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    /**
     * Defines the x,y and z coordinate of the center of all stl models
     * this point is the point around which is rotated
     */
    private double centerX, centerY, centerZ;
    /**
     * Two anchor points used when rotating with the mouse
     */
    private double anchorX, anchorY;
    /**
     * Anchor angle used when rotating with the mouse
     */
    private double anchorAngleX = 0;
    /**
     * Anchor angle used when rotating with the mouse
     */
    private double anchorAngleY = 0;
    /**
     * Anchor angle used when rotating with the mouse
     */
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    /**
     * Anchor angle used when rotating with the mouse
     */
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);
    /**
     * Boolean if the cone is visualized at the moment
     */
    private final BooleanProperty visualizeCone = new SimpleBooleanProperty(true);
    /**
     * The current status
     */
    private Label statusLabel;
    /**
     * ArrayList of all stl models
     */
    private ArrayList<STLModel> stlModels = new ArrayList<>();
    /**
     * LinkedList of all trackers
     */
    private LinkedList<Target> targets;
    private ScrollPane scrollPane;
    private Group meshGroup;

    private boolean flagReloadMatrix = false;

    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    /**
     * Get all currently loaded stl models
     *
     * @return ArrayList of stl models
     */
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
     * Loads the previously used stl files for visualisation
     */
    public void loadLastSTLModels() {
        StlMeshImporter importer = new StlMeshImporter();
        try {
            JSONObject jsonSTLModels = util.Persistence.readStlSaveFile();
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

    /**
     * Clears all stl models and paths from the visualisation
     */
    public void clearSTLModelsAndPaths() {
        stlModels.clear();
        targets.clear();
    }

    /**
     * Adds one or multiple stl files to the existing ones for the visualisation
     *
     * @return a string array of the loaded stl file names
     */
    public List<File> addSTLModels() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load STL File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("STL Files", "*.stl"));
        List<File> fileList = fc.showOpenMultipleDialog(new Stage());
        if (fileList != null) {
            loadNewSTLModel(fileList);
        }

        return fileList;
    }

    /**
     * Load one or more stl model from a file list and add them to the current ArrayList of stl models
     *
     * @param fileList the file list of models to be loaded
     */
    private void loadNewSTLModel(List<File> fileList) {
        for (int i = 0; i < fileList.size(); i++) {
            try {
                StlMeshImporter importer = new StlMeshImporter();

                importer.read(fileList.get(i));
                Mesh mesh = importer.getImport();
                String name = getSTLName(fileList.get(i));
                STLModel model = new STLModel(new MeshView(mesh), name, "ccccccff", true);
                model.getMeshView().getTransforms().addAll(
                        //Rotate the Model by 180 degrees for correct display
                        new Rotate(180, Rotate.X_AXIS)
                );
                stlModels.add(model);

                //logger.log(Level.INFO, "STL file read from: " + fileList.get(i).getAbsolutePath());
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error reading STL file");
            }
        }
    }

    /**
     * A helper method to get the name of a stl file
     * This requires that the files are named like "name.stl"
     *
     * @param file the stl file, where you want to extract the name from
     * @return the name of the stl
     */
    private String getSTLName(File file) {
        String name = file.toString();
        int index = name.lastIndexOf("\\");
        name = name.substring(index + 1, name.length() - 4);
        name = "STL " + name;
        return name;
    }

    /**
     * Select a .mps file created from MITK to add two targets to the visualisation
     * One is the entry point and the other represents the target point
     */
    public void addPathVisualisation() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Path Visualisation");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MPS Files", "*.mps"));
        File file = fc.showOpenDialog(new Stage());
        if (file != null) {
            // Read xml files:
            // https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // Refresh the target list, everytime you load new targets
            targets = new LinkedList<>();
            try {
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(file);
                doc.getDocumentElement().normalize();
                NodeList list = doc.getElementsByTagName("point");
                for (int temp = 0; temp < list.getLength(); temp++) {
                    org.w3c.dom.Node node = list.item(temp);
                    if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        double x = Double.parseDouble(element.getElementsByTagName("x").item(0).getTextContent());
                        double y = Double.parseDouble(element.getElementsByTagName("y").item(0).getTextContent());
                        double z = Double.parseDouble(element.getElementsByTagName("z").item(0).getTextContent());
                        targets.add(new Target(x, y, z));
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Visualizes the tracking tools by updating their position and rotation according to
     * the tracking data read from CSV or IGT connection
     */
    public void visualizeTracking() {
        if (trackingService.getStatus() == HardwareStatus.DISCONNECTED) {
            statusLabel.setText("No Tracking Data Source");
            return;
        }

        // timeline has not been started in trackingData view
        if (trackingService.getStatus() == HardwareStatus.CONNECTED_NO_STREAM) {
            statusLabel.setText("Start Tracking in Tracking Data View first");
            return;
        }
        statusLabel.setText("");

        // loads the next set of tracking data
        var toolMeasures = trackingService.updateSingle();
        for (var tool : trackingService.getToolsForRender()) {
            if (flagReloadMatrix) {
                tool.loadTransformationMatrix();
            }
            tool.updateDisplay();
//            tool.show();
            tool.checkBounds(stlModels);
        }
        flagReloadMatrix = false;
    }

    /**
     * This will set a flag that makes tools reload the transformation matrix from source on the next update
     */
    public void scheduleReloadMatrix(){
        flagReloadMatrix = true;
    }

    /**
     * Adds the Nodes and Controls to the Scene
     */
    public void showFigure() {
        if (trackingService.getStatus() == HardwareStatus.DISCONNECTED) {
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
        handleMouse(subScene, root);
    }

    /**
     * Adds Nodes and Rotation to Group and centres it
     *
     * @return Group with stl model, trackers and targets
     */
    private Group buildScene() {
        Group root = new Group();

        // If stlFiles are loaded
        if (stlModels != null) {
            Group stlGroup = new Group();
            for (STLModel model : stlModels) {
                stlGroup.getChildren().add(model.getMeshView());
            }
            centerX = stlGroup.getBoundsInParent().getCenterX();
            centerY = stlGroup.getBoundsInParent().getCenterY();
            centerZ = stlGroup.getBoundsInParent().getCenterZ();

            root.getChildren().add(stlGroup);
        }

        if (targets != null) {
            root.getChildren().addAll(targets);
        }

        if (trackingService.getStatus() == HardwareStatus.CONNECTED_AND_STREAMING) {
            trackingService.updateSingle();
            var tools = trackingService.getToolsForRender();
            for (var tool : tools) {
                tool.installInScene(root);
                if (targets != null) {
                    tool.setTargets(targets);
                }
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
    private void initCamera() {
        cameraContainer = new CameraContainer(true);
        Vector3D newPos = new Vector3D(centerX, centerY, centerZ);
        cameraContainer.setPos(newPos);
        cameraContainer.move(new Vector3D(0, 0, -500));
    }

    /**
     * Adds Mouse Controls to the Scene
     *
     * @param subScene Scene for MouseEvent
     * @param root     SmartGroup
     */
    private void handleMouse(SubScene subScene, Group root) {
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
     * @param scrollPane      Pane for KeyEvent
     * @param cameraContainer Camera to be controlled
     */
    private void handleKeyboard(ScrollPane scrollPane, CameraContainer cameraContainer) {

        scrollPane.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case S:
                case DOWN:
                    Vector3D down = new Vector3D(0, 1, 0);
                    down.setMag(CAM_MOVEMENT);
                    cameraContainer.move(down);
                    event.consume();
                    break;
                case W:
                case UP:
                    Vector3D up = new Vector3D(0, -1, 0);
                    up.setMag(CAM_MOVEMENT);
                    cameraContainer.move(up);
                    event.consume();
                    break;
                case D:
                case RIGHT:
                    Vector3D right = new Vector3D(1, 0, 0);
                    right.setMag(CAM_MOVEMENT);
                    cameraContainer.move(right);
                    break;
                case A:
                case LEFT:
                    Vector3D left = new Vector3D(-1, 0, 0);
                    left.setMag(CAM_MOVEMENT);
                    cameraContainer.move(left);
                    event.consume();
                    break;
            }
        });
    }

    /**
     * Helper Method used to recenter the view to its original position
     */
    public void resetView() {
        angleX.set(0);
        angleY.set(0);
    }
}
