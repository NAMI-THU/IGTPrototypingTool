package util;

import controller.Controller;

import java.util.ArrayList;
import java.util.List;

public class ControllerCollector {
    private static final ControllerCollector instance = new ControllerCollector();
    private ControllerCollector(){}
    private final List<Controller> controllers = new ArrayList<>();
    public static ControllerCollector getInstance(){return instance;}

    public List<Controller> getControllers(){
        return controllers;
    }

    public void register(Controller controller){
        synchronized (controllers) {
            controllers.add(controller);
        }
    }

    public void unregister(Controller controller){
        synchronized (controllers) {
            controllers.remove(controller);
        }
    }
}
