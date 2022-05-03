package mainMethod;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.ControllerCollector;
import util.CustomLogger;

public class App extends Application {


    @Override
    public void start(Stage primaryStage) throws IOException {
        // setup logging
        try {
            CustomLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String path = "/view/MainView.fxml";
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        Parent root = loader.load();
        primaryStage.setTitle("IGT Prototyping Tool");
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(500);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/customstyle.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        CustomLogger.closeLogger();
        var controllers = new ArrayList<>(ControllerCollector.getInstance().getControllers());
        for(var controller : controllers){
            controller.close();
        }
    }
}
