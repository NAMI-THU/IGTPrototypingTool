package mainMethod;

import userinterface.Diagramm;

public class Main {

    public static void main(String[] args) {

        new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(Diagramm.class);
            }
        }.start();

    }

}