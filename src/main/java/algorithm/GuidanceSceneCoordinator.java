package algorithm;

import controller.GuidanceHandler;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import shapes.CameraContainer;
import java.util.ArrayList;

/**
 * Class which handles the world, meshes and subScene for the scene overlay.
 * <p>
 * It loads the meshes from the {@link DataService}, transforms them and adds them to the scene.
 * Also creates the subScene and adds the camera to it.
 * </p>
 * */
public class GuidanceSceneCoordinator {

    /** Camera used for scene overlay. */
    private final CameraContainer cameraContainer = new CameraContainer(true);

    /** Group which contains the models. */
    private final Group world = new Group();

    /** Group that holds the models and its transform.  */
    private final Group modelRoot = new Group();

    /** The viewport hosting the background scene graph. */
    private SubScene subScene;

    /** A rectangle acting as a border around the sub scene. */
    private Rectangle border;

    /** Will be initalized, if a torso.stl file is being loaded. */
    private MeshView torso;

    private final GuidanceHandler guidanceHandler;

    public GuidanceSceneCoordinator(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
    }

    /** This method is used to set up the scene overlay. It's always called by guidance start.
     * <p>
     * This method rotates the modelRoot and therefore the meshes by a matrix. It creates
     * the subScene only once at runtime.
     * </p>
     *  */
    public void initialize() {
        modelRoot.getTransforms().setAll(new Affine(
                0, 1, 0, 0,
                0, 0, 1, 0,
                1, 0, 0, 0));

        if (!world.getChildren().contains(modelRoot)) {
            world.getChildren().add(modelRoot);
        }

        StackPane subScenePane = guidanceHandler.getSubScene();

        if (subScene == null) {
            subScene = new SubScene(world, 1500, 800, true, SceneAntialiasing.BALANCED);
            subScene.setRoot(world);
            subScene.setCamera(cameraContainer.getPerspectiveCamera());
            subScene.setFill(Color.rgb(90, 90, 90, 0.5));

            subScene.heightProperty().bind(subScenePane.heightProperty().multiply(0.82));
            subScene.widthProperty().bind(subScene.heightProperty().multiply(16.0 / 9.0));

            subScenePane.getChildren().add(subScene);
        }

        if (border == null) {
            border = new Rectangle();
            border.setFill(Color.TRANSPARENT);
            border.setStroke(Color.rgb(85, 85, 85));
            border.setStrokeWidth(3);
            border.widthProperty().bind(subScene.widthProperty());
            border.heightProperty().bind(subScene.heightProperty());
            border.setMouseTransparent(true);

            subScenePane.getChildren().add(border);
        }

        if (!subScenePane.getChildren().contains(subScene)) {
            subScenePane.getChildren().add(subScene);
        }

        if (!subScenePane.getChildren().contains(border)) {
            subScenePane.getChildren().add(border);
        }
    }

    /**
     * Creates a {@link MeshView} from the meshes which are contained in the {@link DataService}.
     * */
    public void addActiveModelsToRoot() {
        modelRoot.getChildren().clear();

        ArrayList<TriangleMesh> meshes = DataService.getInstance().getMeshes();
        ArrayList<String> meshNames = DataService.getInstance().getMeshNames();
        ArrayList<Color> meshColors = DataService.getInstance().getMeshColors();

        for (int i = 0; i < meshes.size(); i++) {
            MeshView meshView = new MeshView(meshes.get(i));
            meshView.setMaterial(new PhongMaterial(meshColors.get(i)));
            modelRoot.getChildren().add(meshView);
            // If a torso is loaded, initialize variable
            if (meshNames.get(i).contains("torso")) {
                torso = meshView;
            }
        }
    }

    /**
     * Manages the visibility of the torso in the background scene overlay.
     *
     * @param visible The visibility of the torso.
     * */
    public void torsoVisible(boolean visible) {
        if (torso != null) {
            if (!visible) {
                modelRoot.getChildren().remove(torso);
            } else {
                if (!modelRoot.getChildren().contains(torso)) {
                    modelRoot.getChildren().add(torso);
                }
            }
        }
    }

    /**
     * If torso has been removed from the mesh list, set the variable to null.
     * */
    public void setTorsoNull() {
        torso = null;
    }

    public CameraContainer getCamera() {
        return cameraContainer;
    }

    public Group getWorld() {
        return world;
    }

}
