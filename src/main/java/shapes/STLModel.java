package shapes;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import util.Matrix3D;
import util.Vector3D;

/**
 * This class represent a stl model. It has the MehsView, the name, the color and the position of the model
 */
public class STLModel {
    private MeshView meshView;
    private String name;
    private Color color;
    private Vector3D pos;

    public STLModel(MeshView mv) {
        setMeshView(mv);
    }

    public STLModel(MeshView mv, String name, String hex, boolean visible) {
        setMeshView(mv);
        setName(name);
        setColor(hex);
        setVisible(visible);
        pos = new Vector3D(mv.getTranslateX(), mv.getTranslateY(), mv.getTranslateZ());
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    /**
     * A method to set the color of the model.
     * @param hex a hex value as a string
     */
    public void setColor(String hex) {
        color = new Color(
                Integer.valueOf(hex.substring(0, 2), 16) / 255.0,
                Integer.valueOf(hex.substring(2, 4), 16)/ 255.0,
                Integer.valueOf(hex.substring(4, 6), 16)/ 255.0,
                Integer.valueOf(hex.substring(6, 8), 16)/ 255.0);
        meshView.setMaterial(new PhongMaterial((color)));
    }

    public void setColor(PhongMaterial material) {
        meshView.setMaterial(material);
    }

    public void setMeshView(MeshView mv) {
        meshView = mv;
    }

    public MeshView getMeshView() {
        return meshView;
    }

    public void setVisible(boolean val) {
        meshView.setVisible(val);
    }

    public boolean isVisible() {
        return meshView.isVisible();
    }
}
