package controller;

import algorithm.Tool;
import algorithm.TrackingService;
import algorithm.VisualizationManager;
import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import org.json.JSONObject;
import shapes.STLModel;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VisualizationController implements Controller {

    @FXML
    Button resetView;
    @FXML
    Group meshGroup;
    @FXML
    Button loadStlFile;
    @FXML
    Button addStlFile;
    @FXML
    Button start;
    @FXML
    ToggleButton pause;
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
    Label stlLabel;
    @FXML
    ColorPicker stlColorPicker;
    @FXML
    CheckBox stlVisibleCB;
    @FXML
    CheckBox trackingVisibleCB;
    @FXML
    CheckBox needleProjectionCB;
    TrackingDataController trackingDataController;
    VisualizationManager visualizationManager;
    TrackingService trackingService = TrackingService.getInstance();

    private Label statusLabel;
    private final BooleanProperty visualizationRunning = new SimpleBooleanProperty(false);
    private final BooleanProperty sourceConnected = new SimpleBooleanProperty(false);

    private final TreeItem<String> treeItemRoot = new TreeItem<>("Root");
    TreeItem<String> stlBranch = new TreeItem<>("Files");


    //private String[] stlNames;

    private String[] trackerNames;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        start.disableProperty().bind(visualizationRunning.or(sourceConnected.not()));

        stlTreeView.setShowRoot(false);
        stlTreeView.setRoot(treeItemRoot);
        treeItemRoot.getChildren().add(stlBranch);
    }

    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
        this.statusLabel.setText("");
    }

    public void injectTrackingDataController(TrackingDataController trackingDataController) {
        this.trackingDataController = trackingDataController;
    }

    public void injectVisualizationManager(VisualizationManager visualizationManager) {
        this.visualizationManager = visualizationManager;
    }

    public void setVisualizationRunning(Boolean value) {
        this.visualizationRunning.set(value);
    }

    public void setSourceConnected(Boolean value) {
        this.sourceConnected.set(value);
    }

    /**
     * Method to load one or more STL Files and overwrite the existing ones
     */
    @FXML
    private void loadSTLFile() {
        if (trackingService.getTrackingDataSource() == null) {
            statusLabel.setText("Select Tracking Data Source first");
            return;
        }
        visualizationManager.setPane(this.scrollPane);
        visualizationManager.setMeshGroup(this.meshGroup);
        List<File> fileNames = visualizationManager.loadStlModel();
        visualizationManager.showFigure();

        JSONObject jsonSTLModels = new JSONObject();

        stlBranch.getChildren().removeAll(stlBranch.getChildren());

        if (fileNames != null) {
            addSTLToJSON(fileNames, jsonSTLModels);
        }
    }

    /**
     * Method to add one or more STL Files to the existing ones
     */
    public void addSTLFile() {
        if (trackingService.getTrackingDataSource() == null) {
            statusLabel.setText("Select Tracking Data Source first");
            return;
        }
        List<File> fileNames = visualizationManager.addSTLModels();
        visualizationManager.showFigure();
        if (fileNames != null) {
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject jsonSTLModels = (JSONObject) jsonParser.parse(new FileReader("src/main/resources/json/stlFiles.json"));
                addSTLToJSON(fileNames, jsonSTLModels);
            } catch (IOException | ParseException e) {
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
        System.out.println("Model Size " + jsonSTLModels.size());
        int offset = jsonSTLModels.size();
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
            FileWriter file = new FileWriter("src/main/resources/json/stlFiles.json");
            file.write(jsonSTLModels.toJSONString());
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds the Tracker to the Tree View
     *
     * @param tools the tools to get their names
     */
    public void addTrackerToTreeView(List<Tool> tools) {
        TreeItem<String> trackerBranch = new TreeItem<>("Tracker");
        treeItemRoot.getChildren().add(trackerBranch);
        if (tools != null) {
            trackerNames = new String[tools.size()];
            for (int i = 0; i < tools.size(); i++) {
                String name = tools.get(i).getName();
                trackerNames[i] = name;
                TreeItem<String> stlFile = new TreeItem<>(name);
                trackerBranch.getChildren().add(stlFile);
            }
        }
    }

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

        List<Tool> tools = trackingService.getDataService().loadNextData(1);

        int index = getSelectedTracker();
        trackingVisibleCB.setSelected(tools.get(index).coneIsVisible());
        needleProjectionCB.setSelected(tools.get(index).projectionIsVisible());
        needleProjectionCB.setDisable(!tools.get(index).coneIsVisible());
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

    @FXML
    private void startTracking() {
        trackingDataController.visualizeTracking();
    }

    /**
     * Set a color for the selected tracker in the visualisation view
     */
    @FXML
    private void setTrackerColor() {
        if (visualizationManager.visualizeCone().get()) {
            List<Tool> tools = trackingService.getDataService().loadNextData(1);

            int index = getSelectedTracker();
            tools.get(index).setConeColor(new PhongMaterial(trackerColorPicker.getValue()));
        }
    }

    /**
     * Set a size for the selected tracker in the visualisation view
     */
    @FXML
    private void setTrackerSize() {
        if (visualizationManager.visualizeCone().get()) {
            List<Tool> tools = trackingService.getDataService().loadNextData(1);

            int index = getSelectedTracker();
            tools.get(index).setConeSize(trackerSlider.getValue());
        }
    }

    /**
     * Sets the tracker visibility
     */
    @FXML
    private void setTrackerVisibility() {
        if (visualizationManager.visualizeCone().get()) {
            List<Tool> tools = trackingService.getDataService().loadNextData(1);

            int index = getSelectedTracker();
            boolean visible = tools.get(index).coneIsVisible();
            if (visible) {
                tools.get(index).setProjectionVisibility(false);
                needleProjectionCB.setSelected(false);
                needleProjectionCB.setDisable(true);
            } else {
                needleProjectionCB.setDisable(false);
            }
            tools.get(index).setConeVisibility(!visible);
        }
    }

    @FXML
    private void setProjecitonVisibility() {
        if (visualizationManager.visualizeCone().get()) {
            List<Tool> tools = trackingService.getDataService().loadNextData(1);

            int index = getSelectedTracker();
            boolean visible = tools.get(index).projectionIsVisible();
            tools.get(index).setProjectionVisibility(!visible);
        }
    }

    @FXML
    private void changeTrackerName() {

    }

    /**
     * Sets the color of the stl model
     */
    @FXML
    private void setSTLColor()  {
        int pos = getSelectedSTL();
        if (pos != -1) {
            ArrayList<STLModel> stlModels = visualizationManager.getSTLModels();
            stlModels.get(pos).setColor(new PhongMaterial(stlColorPicker.getValue()));
            changeAttributeOfSTL(pos, "Color", String.valueOf(stlColorPicker.getValue()));
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

    /**
     * Method to change the attributes if a stl file and save them to a json file
     *
     * @param index the number of the current stl file
     * @param attribute the attribute to change
     * @param value the new value of the attribute
     */
    private void changeAttributeOfSTL(int index, String attribute, String value) {
        try {
            var jsonString = Files.readString(new File("src/main/resources/json/stlFiles.json").toPath());
            JSONObject jsonSTLModels = new JSONObject(jsonString);
            JSONObject updatedModels = new JSONObject();

            for (int i = 0; i < jsonSTLModels.length(); i++) {
                JSONObject jsonSTLModel = jsonSTLModels.getJSONObject("STL " + i);
                if (i == index) {
                    jsonSTLModel.put(attribute, value);
                }
                updatedModels.put("STL " + i, jsonSTLModel);
            }
            try {
                FileWriter file = new FileWriter("src/main/resources/json/stlFiles.json");
                file.write(jsonSTLModels.toString());
                file.close();
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
            var jsonString = Files.readString(new File("src/main/resources/json/stlFiles.json").toPath());
            JSONObject jsonSTLModels = new JSONObject(jsonString);
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
        String name = item.getValue();
        return Character.getNumericValue(name.charAt(name.length()-1));
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
     * Pauses the visualization
     */
    @FXML
    private void pauseVisualization() {
        trackingDataController.freezeVisualization();
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

    /**
     * Resets the camera view back to the starting point
     */
    @FXML
    private void resetView() {
        visualizationManager.resetView();
    }

    @Override
    public void close() {
        statusLabel.setText("");
        unregisterController();
    }
}
