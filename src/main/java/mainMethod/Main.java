package mainMethod;

import userinterface.TrackingDataView;

public class Main {

    public static void main(String[] args) {

        new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(TrackingDataView.class);
            }
        }.start();

    }

}