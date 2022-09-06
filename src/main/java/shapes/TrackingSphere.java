package shapes;

import algorithm.ToolMeasure;
import algorithm.TrackingService;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

import javax.sound.midi.Track;
import java.util.List;


public class TrackingSphere extends Sphere {
    /*
    Field variables
     */
    private final TrackingService trackingService = TrackingService.getInstance();
    private Sphere[] trackingSpheres;

    final Rotate rx = new Rotate(0, Rotate.X_AXIS);
    final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
    final Rotate rz = new Rotate(0, Rotate.Z_AXIS);


    /*
    Constructors
     */
    public TrackingSphere(double radius, int divisions, Color color) {
        super(radius, divisions);
        setMaterial(new PhongMaterial(color));
        getTransforms().addAll(rz, ry, rx);
    }

    /*
    Methods
     */
    public Sphere[] loadTrackingSpheres() {
        List<ToolMeasure> tools = trackingService.getDataService().loadNextData(1);



        if (tools != null) {
            trackingSpheres = new Sphere[tools.size()];
            int x = 0;

            for (ToolMeasure tool : tools) {
                trackingSpheres[x] = new Sphere();
                x++;
            }
        }
        return trackingSpheres;
    }
}
