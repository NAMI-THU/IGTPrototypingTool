package controller;

import algorithm.DataService;
import algorithm.TrackingTool;
import algorithm.TrackingService;
import algorithm.VisualizationManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import org.json.JSONObject;
import shapes.STLModel;
import util.Persistence;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class VisualizationController implements Controller {

    @FXML
    public Button loadPointSet;
    @FXML
    public Button clearPointSets;
    @FXML
    Group meshGroup;
    @FXML
    Button clearSTLFiles;
    @FXML
    Button addStlFile;
    @FXML
    ToggleButton cullBack;
    @FXML
    ToggleButton wireframe;
    @FXML
    ColorPicker trackerColorPicker;
    @FXML
    Slider trackerSlider;
    @FXML
    ScrollPane scrollPane;
    @FXML
    TreeView<String> stlTreeView;
    @FXML
    VBox trackerVBox;
    @FXML
    VBox stlVBox;
    @FXML
    TextField trackerTextField;
    @FXML
    TextField stlLabel;
    @FXML
    ColorPicker stlColorPicker;
    @FXML
    CheckBox stlVisibleCB;
    @FXML
    CheckBox trackingVisibleCB;
    @FXML
    CheckBox needleProjectionCB;
    TrackingController trackingController;
    VisualizationManager visualizationManager;
    TrackingService trackingService = TrackingService.getInstance();

    private Label statusLabel;
    private final BooleanProperty visualizationRunning = new SimpleBooleanProperty(false);
    private final BooleanProperty sourceConnected = new SimpleBooleanProperty(false);

    private final TreeItem<String> treeItemRoot = new TreeItem<>("Root");
    TreeItem<String> stlBranch = new TreeItem<>("Files");

    private String[] trackerNames;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();

        stlTreeView.setShowRoot(false);
        stlTreeView.setRoot(treeItemRoot);
        treeItemRoot.getChildren().add(stlBranch);
    }

    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
        this.statusLabel.setText("");
    }

    public void injectTrackingDataController(TrackingController trackingController) {
        this.trackingController = trackingController;
    }

    public void injectVisualizationManager(VisualizationManager visualizationManager) {
        this.visualizationManager = visualizationManager;
        visualizationManager.setPane(this.scrollPane);
        visualizationManager.setMeshGroup(this.meshGroup);
    }

    public void setVisualizationRunning(Boolean value) {
        this.visualizationRunning.set(value);
    }

    public void setSourceConnected(Boolean value) {
        this.sourceConnected.set(value);
    }

    /**
     * Method to add one or more STL Files to the existing ones
     */
    public void addSTLFile() {
        if (trackingService.getTrackingDataSource() == null) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Select Tracking Data Source first!");
            alert.showAndWait();
            //statusLabel.setText("Select Tracking Data Source first"); //improve status label first
            return;
        }

        List<File> fileNames = visualizationManager.addSTLModels();
        visualizationManager.showFigure();
        if (fileNames != null) {
            try {
                var jsonSTLModels = Persistence.readStlSaveFile();
                addSTLToJSON(fileNames, jsonSTLModels);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Helper method to add the STl Files to the JSON File
     *
     * @param fileNames the JSON file names to be stored
     * @param jsonSTLModels the JSON Object to which the STL Files should be added
     */
    private void addSTLToJSON(List<File> fileNames, JSONObject jsonSTLModels) {
        int offset = jsonSTLModels.length();
        for (int i = 0; i < fileNames.size(); i++) {
            String path = fileNames.get(i).toString();
            int index = path.lastIndexOf("\\");
            String name = path.substring(index+1, path.length() - 4);
            name = "STL " + name;

            JSONObject jsonSTLModel = new JSONObject();
            jsonSTLModel.put("Name", name);
            jsonSTLModel.put("Path", path);
            jsonSTLModel.put("Color", "0xccccccff");
            jsonSTLModel.put("Visible", "true");
            jsonSTLModels.put("STL " + (i + offset), jsonSTLModel);
            TreeItem<String> stlFile = new TreeItem<>(name);
            stlBranch.getChildren().add(stlFile);
        }
        try {
            Persistence.writeStlSaveFile(jsonSTLModels);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds the Tracker to the Tree View
     *
     * @param trackingTools the tools to get their names
     */
    public void addTrackerToTreeView(List<TrackingTool> trackingTools) {
        TreeItem<String> trackerBranch = new TreeItem<>("Tracker");
        treeItemRoot.getChildren().add(trackerBranch);
        if (trackingTools != null) {
            trackerNames = new String[trackingTools.size()];
            for (int i = 0; i < trackingTools.size(); i++) {
                String name = trackingTools.get(i).getName();
                trackerNames[i] = name;
                TreeItem<String> stlFile = new TreeItem<>(name);
                trackerBranch.getChildren().add(stlFile);
            }
        }
    }

    /**
     * Adds the name of the stl file to the tree view
     */
    public void addSTLToTreeView() {
        ArrayList<STLModel> stlModels = visualizationManager.getSTLModels();
        if (stlModels != null) {
            for (STLModel model : stlModels) {
                TreeItem<String> stlFile = new TreeItem<>(model.getName());
                stlBranch.getChildren().add(stlFile);
            }
        }
    }

    /**
     * Method to choose the tracker / stl files to modify it (change the color, size, visibility)
     */
    @FXML
    private void selectItem() {
        TreeItem<String> item = stlTreeView.getSelectionModel().getSelectedItem();
        if (item != null) {
            TreeItem<String> parent = item.getParent();
            if (parent.getValue().equals("Files")) {
                String name = item.getValue();
                activateSTLVBox(name);
            } else if (parent.getValue().equals("Tracker")) {
                String name = item.getValue();
                activateTrackerVBox(name);
            }
        }
    }

    /**
     * Method to show the box for customization of a given tracker
     *
     * @param name the name of the tracker
     */
    private void activateTrackerVBox(String name) {
        trackerVBox.setVisible(true);
        trackerVBox.setDisable(false);

        stlVBox.setVisible(false);
        stlVBox.setDisable(true);

        trackerTextField.setText(name);

        List<TrackingTool> trackingTools = trackingService.getDataService().loadNextData(1);

        int index = getSelectedTracker();
        trackingVisibleCB.setSelected(trackingTools.get(index).coneIsVisible());
        needleProjectionCB.setSelected(trackingTools.get(index).projectionIsVisible());
        needleProjectionCB.setDisable(!trackingTools.get(index).coneIsVisible());
    }

    /**
     * Method to show the box for customization of a given stl file
     *
     * @param name the name of the stl file
     */
    private void activateSTLVBox(String name) {
        trackerVBox.setVisible(false);
        trackerVBox.setDisable(true);

        stlVBox.setVisible(true);
        stlVBox.setDisable(false);

        stlLabel.setText(name);
        int pos = getSelectedSTL();
        if (pos != -1) {
            JSONObject jsonSTLModel = getSelectedJSON(pos);
            String hex = (String) jsonSTLModel.get("Color");
            hex = hex.substring(2);
            Color color = new Color(
                    Integer.valueOf(hex.substring(0, 2), 16) / 255.0,
                    Integer.valueOf(hex.substring(2, 4), 16)/ 255.0,
                    Integer.valueOf(hex.substring(4, 6), 16)/ 255.0,
                    Integer.valueOf(hex.substring(6, 8), 16)/ 255.0);
            stlColorPicker.setValue(color);
            stlVisibleCB.setSelected(Boolean.parseBoolean(jsonSTLModel.get("Visible").toString()));
        }
    }

    /**
     * Set a color for the selected tracker in the visualisation view
     */
    @FXML
    private void setTrackerColor() {
        if (visualizationManager.visualizeCone().get()) {
            List<TrackingTool> trackingTools = trackingService.getDataService().loadNextData(1);

            int index = getSelectedTracker();
            trackingTools.get(index).setConeColor(new PhongMaterial(trackerColorPicker.getValue()));
        }
    }

    /**
     * Set a size for the selected tracker in the visualisation view
     */
    @FXML
    private void setTrackerSize() {
        if (visualizationManager.visualizeCone().get()) {
            List<TrackingTool> trackingTools = trackingService.getDataService().loadNextData(1);

            int index = getSelectedTracker();
            trackingTools.get(index).setConeSize(trackerSlider.getValue());
        }
    }

    /**
     * Sets the tracker visibility
     */
    @FXML
    private void setTrackerVisibility() {
        if (visualizationManager.visualizeCone().get()) {
            List<TrackingTool> trackingTools = trackingService.getDataService().loadNextData(1);

            int index = getSelectedTracker();
            boolean visible = trackingTools.get(index).coneIsVisible();
            if (visible) {
                trackingTools.get(index).setProjectionVisibility(false);
                needleProjectionCB.setSelected(false);
                needleProjectionCB.setDisable(true);
            } else {
                needleProjectionCB.setDisable(false);
            }
            trackingTools.get(index).setConeVisibility(!visible);
        }
    }

    @FXML
    private void setProjectionVisibility() {
        if (visualizationManager.visualizeCone().get()) {
            List<TrackingTool> trackingTools = trackingService.getDataService().loadNextData(1);
            int index = getSelectedTracker();
            boolean visible = trackingTools.get(index).projectionIsVisible();
            trackingTools.get(index).setProjectionVisibility(!visible);
        }
    }

    /**
     * Sets the color of the stl model
     */
    @FXML
    private void setSTLColor()  {
        int pos = getSelectedSTL();
        if (pos != -1) {
            ArrayList<STLModel> stlModels = visualizationManager.getSTLModels();
            Color newColor = stlColorPicker.getValue();

            stlModels.get(pos).setColor(new PhongMaterial(newColor));
            changeAttributeOfSTL(pos, "Color", String.valueOf(newColor));

            DataService.getInstance().updateMeshColor(pos, newColor);
        }
    }

    /**
     * Sets the visibility of the stl model
     */
    @FXML
    private void setSTLVisibility() {
        int pos = getSelectedSTL();
        if (pos != -1) {
            ArrayList<STLModel> stlModels = visualizationManager.getSTLModels();
            boolean visible = stlModels.get(pos).isVisible();
            stlModels.get(pos).setVisible(!visible);
            changeAttributeOfSTL(pos, "Visible", String.valueOf(!visible));
        }
    }

    @FXML
    private void clearSTLFiles(){
        Alert alert = new Alert(Alert.AlertType.WARNING ,"", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Clear STl Files");
        alert.setHeaderText("Are you sure you want to clear all files?");
        alert.setContentText("This will remove all STL files from visualization and guidance.");

        if (alert.showAndWait().get() == ButtonType.YES) {
            try {
                visualizationManager.clearSTLModels();
                stlBranch.getChildren().clear();
                visualizationManager.showFigure();
                Persistence.writeStlSaveFile(new JSONObject());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void clearPointSets() {
        Alert alert = new Alert(Alert.AlertType.WARNING ,"", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Clear point sets");
        alert.setHeaderText("Are you sure you want to clear all point sets?");
        alert.setContentText("This will remove all point sets from visualization and guidance.");

        if (alert.showAndWait().get() == ButtonType.YES) {
            visualizationManager.clearTargetPoints();
            visualizationManager.showFigure();
        }
    }

    /**
     * Method to change the attributes if a stl file and save them to a json file
     *
     * @param index the number of the current stl file
     * @param attribute the attribute to change
     * @param value the new value of the attribute
     */
    private void changeAttributeOfSTL(int index, String attribute, String value) {
        try {
            var jsonSTLModels = Persistence.readStlSaveFile();
            JSONObject updatedModels = new JSONObject();

            for (int i = 0; i < jsonSTLModels.length(); i++) {
                JSONObject jsonSTLModel = jsonSTLModels.getJSONObject("STL " + i);
                if (i == index) {
                    jsonSTLModel.put(attribute, value);
                }
                updatedModels.put("STL " + i, jsonSTLModel);
            }
            try {
                Persistence.writeStlSaveFile(updatedModels);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper Method to get the json of a stl file for a given index
     *
     * @param index the index of the stl file
     * @return The desired json object
     */
    private JSONObject getSelectedJSON(int index) {
        try {
            var jsonSTLModels = Persistence.readStlSaveFile();
            return jsonSTLModels.getJSONObject("STL " + index);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method to get the index of the selected tracker
     *
     * @return the index of the selected tracker
     */
    private int getSelectedTracker() {
        TreeItem<String> item = stlTreeView.getSelectionModel().getSelectedItem();
        TreeItem<String> parent = item.getParent();
        return  parent.getChildren().indexOf(item);
    }

    /**
     * Helper method to get the index of the selected stl file
     *
     * @return the index of the selected stl file
     */
    private int getSelectedSTL() {
        TreeItem<String> item = stlTreeView.getSelectionModel().getSelectedItem();
        ArrayList<STLModel> stlModels = visualizationManager.getSTLModels();
        String name = item.getValue();
        int pos = -1;
        for (int i = 0; i < stlModels.size(); i++) {
            if (stlModels.get(i).getName().equals(name)) {
                pos = i;
            }
        }
        return pos;
    }

    /**
     * Set CullFace to none for every MeshView in the scene
     */
    @FXML
    private void toggleCullFace() {
        if (visualizationManager.getSTLModels() == null) {
            statusLabel.setText("No 3D-Model selected");
            return;
        }
        statusLabel.setText("");

        ArrayList<STLModel> stlModels = visualizationManager.getSTLModels();

        if (cullBack.isSelected()) {
            for (STLModel model: stlModels) {
                model.getMeshView().setCullFace(CullFace.NONE);
            }
        } else {
            for (STLModel model: stlModels) {
                model.getMeshView().setCullFace(CullFace.BACK);
            }
        }
    }

    /**
     * Set DrawMode to Line for every MeshView in the scene
     */
    @FXML
    private void toggleWireframe() {
        if (visualizationManager.getSTLModels() == null) {
            statusLabel.setText("No 3D-Model selected");
            return;
        }
        statusLabel.setText("");

        ArrayList<STLModel> stlModels = visualizationManager.getSTLModels();

        if (wireframe.isSelected()) {
            for (STLModel model: stlModels) {
                model.getMeshView().setDrawMode(DrawMode.LINE);
            }
        } else {
            for (STLModel model: stlModels) {
                model.getMeshView().setDrawMode(DrawMode.FILL);
            }
        }
    }

    /**
     * Sets the focus on the mesh group
     */
    @FXML
    private void focus() {
        meshGroup.requestFocus();
    }

    @Override
    public void close() {
        statusLabel.setText("");
        unregisterController();
    }

    public void addLoadPointSets() {
        if (trackingService.getTrackingDataSource() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Select Tracking Data Source first!");
            alert.showAndWait();
            return;
        }
        visualizationManager.loadPointSet();
        visualizationManager.showFigure();
    }
}
