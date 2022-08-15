package controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import util.ControllerCollector;

public interface Controller extends Initializable {

    default void registerController(){
        ControllerCollector.getInstance().register(this);
    }

    default void unregisterController(){
        ControllerCollector.getInstance().unregister(this);
    }

    default void injectStatusLabel(Label statusLabel){};

    void close();

}
