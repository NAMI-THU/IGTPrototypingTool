package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainController implements Controller {
	
	@FXML TabPane tabPane;
	@FXML TrackingDataController trackingDataController;
	FXMLLoader loader;

	MeasurementController measurementController;
	ThrombectomyController thrombectomyController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	/**
	 * Opens a view defined in a fxml file.
	 * @param name of the view that is loaded
	 * @throws IOException
	 */
	private void loadView(String name) throws IOException {
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/" + name 
				+ ".fxml"));
		Tab t = new Tab(name, loader.load());
		tabPane.getTabs().add(t);
		tabPane.getSelectionModel().select(t);
	}	

	@FXML
	private void openMeasurementView() throws IOException{
		if (measurementController != null) return;
		
		loadView("MeasurementView");
		measurementController = loader.getController();
		measurementController.setTrackingDataController(
				trackingDataController);
	}

	@FXML
	private void openThrombectomyView() throws IOException {
		if (thrombectomyController != null) return;
		
		loadView("ThrombectomyView");
		thrombectomyController = loader.getController();
		thrombectomyController.setTrackingDataController(
				trackingDataController);
	};

	public void close() {
	}
}
