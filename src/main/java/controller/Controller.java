package controller;

import javafx.fxml.Initializable;
import util.ControllerCollector;

public interface Controller extends Initializable {

    default void registerController(){
        ControllerCollector.getInstance().register(this);
    }

    default void unregisterController(){
        ControllerCollector.getInstance().unregister(this);
    }

    void close();

}
