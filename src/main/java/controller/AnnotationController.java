package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class AnnotationController implements Controller {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization code goes here
  }

    @FXML
    @Override
    public void close() {
        unregisterController();
    }
}
