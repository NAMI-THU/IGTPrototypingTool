package mainMethod;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.ControllerCollector;
import util.CustomLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class App extends Application {
    static Logger logger = Logger.getLogger(Application.class.getName());

    @Override
    public void start(Stage primaryStage) throws IOException {
        // setup logging
        try {
            CustomLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.log(Level.SEVERE, "Uncaught exception in thread " + thread.getName(), throwable);
        });

        var userPreferencesGlobal = java.util.prefs.Preferences.userRoot().node("IGT_Settings");
        var fontSize = userPreferencesGlobal.getInt("font-size",12);

        String path = "/view/MainView.fxml";
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        Parent root = loader.load();
        primaryStage.setTitle("IGT Prototyping Tool");
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(800);
        primaryStage.getIcons().add(new Image("icon/icon.png"));
        root.setStyle("-fx-font-size: "+fontSize+"pt;");
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
