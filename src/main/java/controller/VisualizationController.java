package controller;

import algorithm.ToolMeasure;
import algorithm.TrackingService;
import algorithm.VisualizationManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import shapes.TrackingCone;
import shapes.TrackingSphere;

import java.io.*;
import java.net.URL;
import java.util.*;
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
    Label trackerLabel;
    @FXML
    Label stlLabel;
    @FXML
    ColorPicker stlColorPicker;
    @FXML
    CheckBox stlVisibleCB;
    @FXML
    CheckBox trackingVisibleCB;
    TrackingDataController trackingDataController;
    VisualizationManager visualizationManager;
    TrackingService trackingService = TrackingService.getInstance();

    private Label statusLabel;
    private final BooleanProperty visualizationRunning = new SimpleBooleanProperty(false);
    private final BooleanProperty sourceConnected = new SimpleBooleanProperty(false);

    private final TreeItem<String> root = new TreeItem<>("Root");

    private String[] stlNames;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        start.disableProperty().bind(visualizationRunning.or(sourceConnected.not()));

        stlTreeView.setShowRoot(false);
        stlTreeView.setRoot(root);
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

        TreeItem<String> stlBranch = new TreeItem<>("Files");
        if (root.getChildren().size() > 1) {
            root.getChildren().remove(1, root.getChildren().size());
        }

        root.getChildren().add(stlBranch);

        JSONObject jsonSTLModels = new JSONObject();

        if (fileNames != null) {
            stlNames = new String[fileNames.size()];
            for (int i = 0; i < fileNames.size(); i++) {
                String path = fileNames.get(i).toString();
                int index = path.lastIndexOf("\\");
                String name = path.substring(index+1, path.length() - 4);
                name = "STL " + name;
                stlNames[i] = name;

                JSONObject jsonSTLModel = new JSONObject();
                jsonSTLModel.put("Name", name);
                jsonSTLModel.put("Path", path);
                jsonSTLModel.put("Color", "0xccccccff");
                jsonSTLModel.put("Visible", "true");
                jsonSTLModels.put("STL " + i, jsonSTLModel);
                TreeItem<String> stlFile = new TreeItem<>(name);
                stlBranch.getChildren().add(stlFile);
            }
            try {
                FileWriter file = new FileWriter("src/main/resources/stlFiles.json");
                file.write(jsonSTLModels.toJSONString());
                file.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Adds the Tracker to the Tree View
     *
     * @param tools the tools to get their names
     */
    public void addTrackerToTreeView(List<ToolMeasure> tools) {
        TreeItem<String> trackerBranch = new TreeItem<>("Tracker");
        root.getChildren().add(trackerBranch);
        if (tools != null) {
            for (ToolMeasure tool: tools) {
                String name = tool.getName();
                TreeItem<String> stlFile = new TreeItem<>(name);
                trackerBranch.getChildren().add(stlFile);
            }
        }
    }

    public void addSTLToTreeView(String[] stlFileNames) {
        stlNames = stlFileNames;
        TreeItem<String> stlBranch = new TreeItem<>("Files");
        root.getChildren().add(stlBranch);
        for (String name : stlNames) {
            TreeItem<String> stlFile = new TreeItem<>(name);
            stlBranch.getChildren().add(stlFile);
        }
    }

    /**
     * Method to choose the tracker / stl files to modify it (change the color, size, visibility)
     */
    @FXML
    private void selectItem() {
        TreeItem<String> item = stlTreeView.getSelectionModel().getSelectedItem();
        if (item != null) {
            String name = item.getValue();
            Pattern pattern = Pattern.compile("Tool\\d");
            Matcher matcher = pattern.matcher(name);
            boolean matchFound = matcher.find();
            if(matchFound) {
                activateTrackerVBox(name);
            }
            pattern = Pattern.compile("^STL");
            matcher = pattern.matcher(name);
            matchFound = matcher.find();
            if(matchFound) {
                activateSTLVBox(name);
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

        trackerLabel.setText(name);
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
            if (visualizationManager.getTrackingCones() == null) {
                statusLabel.setText("No Tracking Data Source");
                return;
            }
            statusLabel.setText("");

            int index = getSelectedTracker();
            TrackingCone[] trackingCones = visualizationManager.getTrackingCones();
            trackingCones[index].setMaterial(new PhongMaterial(trackerColorPicker.getValue()));
        }
    }

    /**
     * Set a size for the selected tracker in the visualisation view
     */
    @FXML
    private void setTrackerSize() {
        if (visualizationManager.visualizeCone().get()) {
            if (visualizationManager.getTrackingCones() == null) {
                statusLabel.setText("No Tracking Data Source");
                return;
            }
            statusLabel.setText("");

            int index = getSelectedTracker();

            TrackingCone[] trackingCones = visualizationManager.getTrackingCones();
            trackingCones[index].setHeight(trackerSlider.getValue());
            trackingCones[index].setRadius(trackerSlider.getValue() * 0.4);
        }
    }

    /**
     * Sets the tracker visibility
     */
    @FXML
    private void setTrackerVisibility() {
        if (visualizationManager.visualizeCone().get()) {
            if (visualizationManager.getTrackingCones() == null) {
                statusLabel.setText("No Tracking Data Source");
                return;
            }
            statusLabel.setText("");

            int index = getSelectedTracker();

            TrackingCone[] trackingCones = visualizationManager.getTrackingCones();
            TrackingSphere[] trackingSpheres = visualizationManager.getTrackingSpheres();

            boolean visible = trackingCones[index].isVisible();
            trackingCones[index].setVisible(!visible);
            trackingSpheres[index].setVisible(!visible);
        }
    }

    @FXML
    private void setSTLColor()  {
        int pos = getSelectedSTL();
        if (pos != -1) {
            MeshView[] stlFiles = visualizationManager.getMeshView();
            System.out.println(stlColorPicker.getValue());
            stlFiles[pos].setMaterial(new PhongMaterial(stlColorPicker.getValue()));
            changeAttributeOfSTL(pos, "Color", String.valueOf(stlColorPicker.getValue()));
        }
    }

    @FXML
    private void setSTLVisibility() {
        int pos = getSelectedSTL();
        if (pos != -1) {
            MeshView[] stlFiles = visualizationManager.getMeshView();
            boolean visible = stlFiles[pos].isVisible();
            stlFiles[pos].setVisible(!visible);
            changeAttributeOfSTL(pos, "Visible", String.valueOf(!visible));
        }
    }

    private void changeAttributeOfSTL(int index, String attribute, String value) {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonSTLModels = (JSONObject) jsonParser.parse(new FileReader("src/main/resources/stlFiles.json"));
            JSONObject updatedModels = new JSONObject();

            for (int i = 0; i < jsonSTLModels.size(); i++) {
                JSONObject jsonSTLModel = (JSONObject) jsonSTLModels.get("STL " + i);
                if (i == index) {
                    jsonSTLModel.put(attribute, value);
                }
                updatedModels.put("STL " + i, jsonSTLModel);
            }
            try {
                FileWriter file = new FileWriter("src/main/resources/stlFiles.json");
                file.write(jsonSTLModels.toJSONString());
                file.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject getSelectedJSON(int index) {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonSTLModels = (JSONObject) jsonParser.parse(new FileReader("src/main/resources/stlFiles.json"));
            return  (JSONObject) jsonSTLModels.get("STL " + index);
        } catch (IOException | ParseException e) {
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
        String name = item.getValue();
        int pos = -1;
        for (int i = 0; i < stlNames.length; i++) {
            if (stlNames[i].equals(name)) {
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
        if (visualizationManager.getMeshView() == null) {
            statusLabel.setText("No 3D-Model selected");
            return;
        }
        statusLabel.setText("");

        MeshView[] meshView = visualizationManager.getMeshView();

        if (cullBack.isSelected()) {
            for (MeshView mesh : meshView) {
                mesh.setCullFace(CullFace.NONE);
            }
        } else {
            for (MeshView mesh : meshView) {
                mesh.setCullFace(CullFace.BACK);
            }
        }
    }

    /**
     * Set DrawMode to Line for every MeshView in the scene
     */
    @FXML
    private void toggleWireframe() {
        if (visualizationManager.getMeshView() == null) {
            statusLabel.setText("No 3D-Model selected");
            return;
        }
        statusLabel.setText("");

        MeshView[] meshView = visualizationManager.getMeshView();

        if (wireframe.isSelected()) {
            for (MeshView mesh : meshView) {
                mesh.setDrawMode(DrawMode.LINE);
            }
        } else {
            for (MeshView mesh : meshView) {
                mesh.setDrawMode(DrawMode.FILL);
            }
        }
    }

    @FXML
    private void focus() {
        meshGroup.requestFocus();
    }

    /**
     * Resets the camera view back to the starting point
     */
    @FXML
    private void resetView() {
        visualizationManager.showFigure();
    }

    @Override
    public void close() {
        statusLabel.setText("");
        unregisterController();
    }
}
