package mainMethod;

import userinterface.*;

public class main {

	public static void main(String[] args) {

		new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(Diagramm.class);
            }
        }.start();
                  
	}

}