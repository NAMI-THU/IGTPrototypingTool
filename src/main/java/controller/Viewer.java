package controller;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class Viewer implements Initializable {

    @FXML
    Group meshGroup;

    private PerspectiveCamera camera;
    private MeshView meshView;

    private static final int VIEWPORT_SIZE = 500;
//    private static final double MODEL_SCALE_FACTOR = 40;
    private static final double MODEL_SCALE_FACTOR = 6;
    private static final double MODEL_X_OFFSET = 0;
    private static final double MODEL_Y_OFFSET = 0;
    private static final double MODEL_Z_OFFSET = VIEWPORT_SIZE * 21;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initCamera();
        Platform.runLater(() -> showFigure());
    }

//    private void initCamera() {
//        this.camera = new PerspectiveCamera(true);
//        this.camera.setNearClip(0.1);
//        this.camera.setFarClip(10000.0);
//        this.camera.setTranslateZ(-1000);
//    }
//
//    private Group buildScene() {
//        Group group = new Group();
//
//        meshView.setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
//        meshView.setTranslateY(VIEWPORT_SIZE / 2 * 9.0 / 16 + MODEL_Y_OFFSET);
//        meshView.setTranslateZ(VIEWPORT_SIZE / 2 + MODEL_Z_OFFSET);
//        meshView.setScaleX(MODEL_SCALE_FACTOR);
//        meshView.setScaleY(MODEL_SCALE_FACTOR);
//        meshView.setScaleZ(MODEL_SCALE_FACTOR);
//
//        PointLight pointLight = new PointLight(Color.WHITE);
//        pointLight.setTranslateZ(VIEWPORT_SIZE / 2);
//        pointLight.setTranslateY(VIEWPORT_SIZE / 2);
//
//        group.getChildren().addAll(meshView, pointLight);
//        return group;
//    }

//    private SubScene createScene3D(Group group) {
//        SubScene scene3d = new SubScene(group, VIEWPORT_SIZE, VIEWPORT_SIZE * 9.0 / 16);
//        scene3d.setFill(Color.WHITE);
//        scene3d.setCamera(this.camera);
//        scene3d.setPickOnBounds(true);
//        return scene3d;
//    }

    private void initCamera() {
        this.camera = new PerspectiveCamera(true);
        this.camera.setNearClip(0.1);
        this.camera.setFarClip(10000.0);
        this.camera.setTranslateZ(-2000);
    }
    private Group buildScene() {
        Group group = new Group();

        meshView.setScaleX(MODEL_SCALE_FACTOR);
        meshView.setScaleY(MODEL_SCALE_FACTOR);
        meshView.setScaleZ(MODEL_SCALE_FACTOR);

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateZ(-2*VIEWPORT_SIZE );
        pointLight.setTranslateY(-2*VIEWPORT_SIZE );

        group.getChildren().addAll(meshView, pointLight);
        return group;
    }

    private SubScene createScene3D(Group group) {
        SubScene scene3d = new SubScene(group, VIEWPORT_SIZE, VIEWPORT_SIZE, true, SceneAntialiasing.BALANCED);
        scene3d.widthProperty().bind(((AnchorPane)meshGroup.getParent()).widthProperty());
        scene3d.heightProperty().bind(((AnchorPane)meshGroup.getParent()).heightProperty());

        scene3d.setFill(Color.WHITE);
        scene3d.setCamera(this.camera);
        scene3d.setPickOnBounds(true);
        return scene3d;
    }

    private void showFigure() {
        meshView = buildMesh();
        // Add MeshView to Group
        Group meshInGroup = buildScene();
        // Create SubScene
        SubScene subScene = createScene3D(meshInGroup);
        // Add subScene to meshGroup
        this.meshGroup.getChildren().add(subScene);

        RotateTransition rotate = rotate3dGroup(meshInGroup);
        this.meshGroup.getChildren().add(createControls(rotate));
    }

    private HBox createControls(RotateTransition rotateTransition) {
        CheckBox cull = new CheckBox("Cull Back");
        meshView.cullFaceProperty().bind(
                Bindings.when(
                                cull.selectedProperty())
                        .then(CullFace.BACK)
                        .otherwise(CullFace.NONE)
        );
        CheckBox wireframe = new CheckBox("Wireframe");
        meshView.drawModeProperty().bind(
                Bindings.when(
                                wireframe.selectedProperty())
                        .then(DrawMode.LINE)
                        .otherwise(DrawMode.FILL)
        );

        CheckBox rotate = new CheckBox("Rotate");
        rotate.selectedProperty().addListener(observable -> {
            if (rotate.isSelected()) {
                rotateTransition.play();
            } else {
                rotateTransition.pause();
            }
        });


        HBox controls = new HBox(10, rotate, cull, wireframe);
        controls.setPadding(new Insets(10));
        return controls;
    }


    private RotateTransition rotate3dGroup(Group group) {
        RotateTransition rotate = new RotateTransition(Duration.seconds(10), group);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setCycleCount(RotateTransition.INDEFINITE);

        return rotate;
    }


    private MeshView buildMesh() {
        TriangleMesh mesh = new TriangleMesh();
        float hw = 100 / 2f;
        float hh = 100 / 2f;
        float hd = 100 / 2f;

        mesh.getPoints().addAll(
                hw, hh, hd,
                hw, hh, -hd,
                hw, -hh, hd,
                hw, -hh, -hd,
                -hw, hh, hd,
                -hw, hh, -hd,
                -hw, -hh, hd,
                -hw, -hh, -hd
        );

        mesh.getTexCoords().addAll(
                100, 0,
                200, 0,
                0, 100,
                100, 100,
                200, 100,
                300, 100,
                400, 100,
                0, 200,
                100, 200,
                200, 200,
                300, 200,
                400, 200,
                100, 300,
                200, 300
        );

        mesh.getFaces().addAll(
                0, 10, 2, 5, 1, 9,
                2, 5, 3, 4, 1, 9,
                4, 7, 5, 8, 6, 2,
                6, 2, 5, 8, 7, 3,
                0, 13, 1, 9, 4, 12,
                4, 12, 1, 9, 5, 8,
                2, 1, 6, 0, 3, 4,
                3, 4, 6, 0, 7, 3,
                0, 10, 4, 11, 2, 5,
                2, 5, 4, 11, 6, 6,
                1, 9, 3, 4, 5, 8,
                5, 8, 3, 4, 7, 3
        );

        return new MeshView(mesh);
    }
}
