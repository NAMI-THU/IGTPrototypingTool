package controller;
import algorithm.Measurement;
import algorithm.TrackingService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExampleController implements Controller {

    
    @FXML
    public Button exampleButton;
    @FXML
    public Label trackingDataLabel;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
    }

    @Override
    public void close() {
        unregisterController();
    }

    public void on_exampleButton(){
        String s = "";
        TrackingService t = TrackingService.getInstance();
        if (t.getTrackingDataSource()!=null) {
            t.getTrackingDataSource().update();
            List<Measurement> li = t.getDataService().loadNextData(1).get(0).getMeasurement();
            s = "Tracking Coordinates: " + li.get(li.size() - 1).getPoint();
        }
        else{
            s = "No tracker connected";
        }
        trackingDataLabel.setText(s);
        System.out.println(s);
    }


}
