package mainMethod;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.CustomLogger;

public class Main extends Application {

    private final static Logger LOGGER = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);

    @Override
    public void start(Stage primaryStage) throws IOException {
        // setup logging
        try {
            CustomLogger.setup();
        } catch (IOException e) {
            LOGGER.warning("Log file was not set up");
            e.printStackTrace();
        }

        String path = "/view/MainView.fxml";
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        Parent root = loader.load();
        primaryStage.setTitle("Tracking Data Analysis Tool");
        Scene scene = new Scene(root, 1050, 700);
        scene.getStylesheets().add("css/customstyle.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
//        new Thread() {
//            @Override
//            public void run() {
         launch();
//            }
//        }.start();
    }
}
