package tracking;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import shapes.NeedleProjection;
import shapes.STLModel;
import shapes.Target;
import shapes.TrackingCone;
import util.Matrix3D;
import util.Quaternion;
import util.Transformation;
import util.Vector3D;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Wrapper to display tools.
 * Adds attributes such as color and transformation
 */
public class RenderedTool {
    private final Tool tool;
    private Transformation transformation;
    private final TrackingCone cone;
    private final NeedleProjection needleProjection;
    private PhongMaterial material;
    private final List<Target> targets = new ArrayList<>();

    public RenderedTool(Tool tool){
        this(tool, new TrackingCone(36, 4, 10), new NeedleProjection(), new PhongMaterial(Color.GRAY));
    }

    public RenderedTool(Tool tool, TrackingCone cone, NeedleProjection needleProjection, PhongMaterial material) {
        this.tool = tool;
        this.cone = cone;
        this.needleProjection = needleProjection;
        this.material = material;
        this.transformation = Transformation.identity();

        updateMaterial(material);
    }

    public void setTransformation(Transformation t){
        this.transformation = t;
    }

    public Vector3D transformPosition(Vector3D point){
        point = transformation.getAffineTransformationMatrix().mult(point).add(transformation.getTranslationVector());
        return point;
    }

    public void updateDisplay(){
        var latestMeasurement = tool.getLatestMeasurement();
        var transformedPosition = transformPosition(latestMeasurement.getPosition());

        this.cone.rotateMatrix(transformation.getRotationMatrix());
        this.cone.setTranslateX(transformedPosition.getX());
        this.cone.setTranslateY(transformedPosition.getY());
        this.cone.setTranslateZ(transformedPosition.getZ());

        this.needleProjection.rotateMatrix(transformation.getRotationMatrix());
        this.needleProjection.setTranslateX(transformedPosition.getX());
        this.needleProjection.setTranslateY(transformedPosition.getY());
        this.needleProjection.setTranslateZ(transformedPosition.getZ());
    }

    public void installInScene(Group root){
        root.getChildren().add(needleProjection);
        root.getChildren().add(cone);
    }

    public void updateMaterial(PhongMaterial material){
        this.material = material;
        this.cone.setMaterial(material);
    }

    public void setTargets(List<Target> targets) {
        this.targets.clear();
        this.targets.addAll(targets);
    }

    public void checkTargets() {
        for (var target: targets) {
            if (needleProjection.isVisible() && needleProjection.intersectsTarget(target, cone.getPos())) {
                target.setSphereColor(Color.GREEN);
            } else {
                target.setSphereColor(Color.RED);
            }
        }
    }

    public void checkBounds(ArrayList<STLModel> stlModels) {
        if (stlModels != null) {
            for (var stlModel : stlModels) {
                if (cone.getBoundsInParent().intersects(stlModel.getMeshView().getBoundsInParent())) {
                    cone.setMaterial(new PhongMaterial(Color.RED));
                } else {
                    cone.setMaterial(this.material);
                }
            }
        }
    }

    public void loadTransformationMatrix(){
        var userPreferences = Preferences.userRoot().node("IGT_Settings");
        var path = userPreferences.get("visualisationTransformMatrix","");
        if(path.isEmpty() || Files.notExists(Path.of(path))) {
            return;
        }

        List<Double> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] valuesStr = line.split(";");
                for (int i = 0; i < 4; i++) {
                    records.add(Double.parseDouble(valuesStr[i]));
                }
            }

            double[] matrixArr = new double[9];
            double[] vectorArr = new double[3];
            byte count = 0;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    if (j == 3) {
                        vectorArr[i] = records.get(4*i + j);
                    } else {
                        matrixArr[count] = records.get(4*i + j);
                        count++;
                    }
                }
            }
            transformation = new Transformation(new Matrix3D(matrixArr),new Vector3D(vectorArr), Quaternion.identity().toRotationMatrix());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to set the size of the cone
     * @param size the new cone size
     */
    public void setConeSize(double size) {
        cone.setHeight(size);
        cone.setRadius(size * 0.4);
    }

    /**
     * Method to set the visibility of the cone
     * @param visibility boolean, true if visible
     */
    public void setConeVisibility(boolean visibility) {
        cone.setVisible(visibility);
    }

    /**
     * Return if cone is visible
     * @return true if cone is visible
     */
    public boolean coneIsVisible() {
        return cone.isVisible();
    }

    /**
     * Method to set the visibility of the projection
     * @param visibility boolean, true if visible
     */
    public void setProjectionVisibility(boolean visibility) {
        needleProjection.setVisible(visibility);
    }

    /**
     * Return if projection is visible
     * @return true if projection is visible
     */
    public boolean projectionIsVisible() {
        return needleProjection.isVisible();
    }
}
