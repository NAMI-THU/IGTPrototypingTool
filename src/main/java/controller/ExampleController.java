package controller;
import algorithm.ImageDataManager;
import algorithm.ImageDataProcessor;
import algorithm.Measurement;
import algorithm.TrackingService;
import inputOutput.VideoSource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExampleController implements Controller {

    @FXML
    public Label trackingDataLabel;
    @FXML
    public Button exampleButton;
    @FXML
    public ImageView imageView;

    private boolean videoConnected = false;
    private ImageDataManager imageDataManager = new ImageDataManager();
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
    }

    @Override
    public void close() {
        unregisterController();
    }

    public void on_exampleButton(){
        //Example for getting tracking data
        String s = "";
        TrackingService t = TrackingService.getInstance();
        if (t.getTrackingDataSource()!=null) {
            t.getTrackingDataSource().update();
            List<Measurement> li = t.getDataService().loadNextData(1).get(0).getMeasurement();
            s = "Tracking Coordinates: " + li.get(li.size() - 1).getPos();
        }
        else{
            s = "No tracker connected";
        }
        trackingDataLabel.setText(s);
        System.out.println(s);


    }
    public void  on_exampleVideoButton(){
        //Example for grabbing a video stream from device id 0
        if(!videoConnected) {
            videoConnected = imageDataManager.openConnection(VideoSource.LIVESTREAM, 0);
        }
        if(videoConnected){
            imageView.setImage(imageDataManager.readImg());
            System.out.println("Updated video");
        }
        else{
            System.out.println("Could not connect to video source id 0");
        }

    }

}
