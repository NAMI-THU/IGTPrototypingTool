package controller;

import algorithm.Tool;
import algorithm.TrackingService;
import algorithm.TrackingServiceObserver;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import util.Vector3D;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class AdvancedTrackingController implements Controller {
    
    @FXML
    private ComboBox<String> toolSelector;
    @FXML
    private TextField targetX;
    @FXML
    private TextField targetY;
    @FXML
    private TextField targetZ;
    @FXML
    private Label toolX;
    @FXML
    private Label toolY;
    @FXML
    private Label toolZ;
    @FXML
    private Label diffX;
    @FXML
    private Label diffY;
    @FXML
    private Label diffZ;
    @FXML
    private Label distanceLabel;
    @FXML
    private Label hintLabel;
    @FXML
    private Canvas canvasXY;
    @FXML
    private Canvas canvasXZ;
    @FXML
    private Canvas canvasYZ;
    
    private Vector3D targetPosition = null;
    private Timeline updateTimeline;
    private final TrackingService trackingService = TrackingService.getInstance();
    private final DecimalFormat df = new DecimalFormat("0.00");
    
    // view bounds for scaling
    private double viewPadding = 50.0;
    private double viewMinX = -100.0, viewMaxX = 100.0;
    private double viewMinY = -100.0, viewMaxY = 100.0;
    private double viewMinZ = -100.0, viewMaxZ = 100.0;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        
        // resize canvas 
        canvasXY.widthProperty().addListener(evt -> {
            if (targetPosition != null || trackingService.getDataService() != null) {
                updateUI();
            }
        });
        canvasXY.heightProperty().addListener(evt -> {
            if (targetPosition != null || trackingService.getDataService() != null) {
                updateUI();
            }
        });
        canvasXZ.widthProperty().addListener(evt -> {
            if (targetPosition != null || trackingService.getDataService() != null) {
                updateUI();
            }
        });
        canvasXZ.heightProperty().addListener(evt -> {
            if (targetPosition != null || trackingService.getDataService() != null) {
                updateUI();
            }
        });
        canvasYZ.widthProperty().addListener(evt -> {
            if (targetPosition != null || trackingService.getDataService() != null) {
                updateUI();
            }
        });
        canvasYZ.heightProperty().addListener(evt -> {
            if (targetPosition != null || trackingService.getDataService() != null) {
                updateUI();
            }
        });
        
        // start loop if tracking is running 
        if (trackingService.getTimeline() != null && 
            trackingService.getTimeline().getStatus() == Animation.Status.RUNNING) {
            startUpdateLoop();
        }
        
        // observer to start loop 
        trackingService.registerObserver((sourceChanged, serviceChanged, timelineChanged) -> {
            if (timelineChanged && trackingService.getTimeline() != null &&
                trackingService.getTimeline().getStatus() == Animation.Status.RUNNING) {
                if (updateTimeline == null || updateTimeline.getStatus() != Animation.Status.RUNNING) {
                    startUpdateLoop();
                }
            }
        });
    }
    
    @FXML
    private void setTarget() {
        try {
            double x = Double.parseDouble(targetX.getText());
            double y = Double.parseDouble(targetY.getText());
            double z = Double.parseDouble(targetZ.getText());
            targetPosition = new Vector3D(x, y, z);
            
            // include target in bounds
            viewMinX = Math.min(viewMinX, x - 50);
            viewMaxX = Math.max(viewMaxX, x + 50);
            viewMinY = Math.min(viewMinY, y - 50);
            viewMaxY = Math.max(viewMaxY, y + 50);
            viewMinZ = Math.min(viewMinZ, z - 50);
            viewMaxZ = Math.max(viewMaxZ, z + 50);

            // start loop if not running
            if (updateTimeline == null || updateTimeline.getStatus() != Animation.Status.RUNNING) {
                startUpdateLoop();
            }
            updateUI();
        } catch (NumberFormatException e) {
            if (distanceLabel != null) {
                distanceLabel.setText("Invalid coordinates");
                distanceLabel.setTextFill(Color.RED);
            }
        }
    }
    
    private void startUpdateLoop() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
        updateTimeline = new Timeline(
            new KeyFrame(Duration.millis(100), e -> updateUI())
        );
        updateTimeline.setCycleCount(Animation.INDEFINITE);
        updateTimeline.play();
    }
    
    private void updateUI() {
        if (trackingService.getDataService() == null) {
            setNoDataState();
            return;
        }
        
        var tools = trackingService.getDataService().getDataManager().getToolMeasures();
        if (tools == null || tools.isEmpty()) {
            setNoDataState();
            return;
        }
        
        // get selected tool
        String selectedTool = toolSelector != null ? toolSelector.getSelectionModel().getSelectedItem() : null;
        Tool toolToUse = null;
        if (selectedTool != null) {
            for (var t : tools) {
                if (t.getName().equals(selectedTool)) {
                    toolToUse = t;
                    break;
                }
            }
        }
        if (toolToUse == null && !tools.isEmpty()) {
            toolToUse = tools.get(0);
        }
        
        // update tool selector
        if (toolSelector != null && !tools.isEmpty()) {
            var currentItems = toolSelector.getItems();
            java.util.List<String> toolNames = new java.util.ArrayList<>();
            for (Tool t : tools) {
                toolNames.add(t.getName());
            }
            if (!currentItems.equals(toolNames)) {
                toolSelector.getItems().clear();
                toolSelector.getItems().addAll(toolNames);
                if (toolSelector.getSelectionModel().getSelectedItem() == null || 
                    !toolNames.contains(toolSelector.getSelectionModel().getSelectedItem())) {
                    toolSelector.getSelectionModel().select(0);
                }
            }
        }
        
        if (toolToUse == null) {
            setNoDataState();
            return;
        }
        
        Vector3D toolPos = toolToUse.getCurrentPosition();
        if (toolPos == null) {
            setNoDataState();
            return;
        }
        
        // update tool position 
        toolX.setText(df.format(toolPos.getX()));
        toolY.setText(df.format(toolPos.getY()));
        toolZ.setText(df.format(toolPos.getZ()));
        
        // update bounds 
        viewMinX = Math.min(viewMinX, toolPos.getX() - 50);
        viewMaxX = Math.max(viewMaxX, toolPos.getX() + 50);
        viewMinY = Math.min(viewMinY, toolPos.getY() - 50);
        viewMaxY = Math.max(viewMaxY, toolPos.getY() + 50);
        viewMinZ = Math.min(viewMinZ, toolPos.getZ() - 50);
        viewMaxZ = Math.max(viewMaxZ, toolPos.getZ() + 50);
        
        if (targetPosition != null) {
            // ipdate difference
            double dx = targetPosition.getX() - toolPos.getX();
            double dy = targetPosition.getY() - toolPos.getY();
            double dz = targetPosition.getZ() - toolPos.getZ();
            
            diffX.setText(df.format(dx));
            diffY.setText(df.format(dy));
            diffZ.setText(df.format(dz));
            
            // update distance
            double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
            distanceLabel.setText(String.format(java.util.Locale.ENGLISH, "%.1f mm", dist));
            if (dist < 2.0) {
                distanceLabel.setTextFill(Color.GREEN);
            } else if (dist < 5.0) {
                distanceLabel.setTextFill(Color.ORANGE);
            } else {
                distanceLabel.setTextFill(Color.RED);
            }
            
            // update hint
            hintLabel.setText(directionHint(dx, dy, dz));
            
            drawView(canvasXY, "XY", toolPos, targetPosition);
            drawView(canvasXZ, "XZ", toolPos, targetPosition);
            drawView(canvasYZ, "YZ", toolPos, targetPosition);
        } else {
            // no target set
            diffX.setText("-");
            diffY.setText("-");
            diffZ.setText("-");
            distanceLabel.setText("-");
            hintLabel.setText("-");
            distanceLabel.setTextFill(Color.BLACK);
            
            drawView(canvasXY, "XY", toolPos, null);
            drawView(canvasXZ, "XZ", toolPos, null);
            drawView(canvasYZ, "YZ", toolPos, null);
        }
    }
    
    private void setNoDataState() {
        toolX.setText("-");
        toolY.setText("-");
        toolZ.setText("-");
        diffX.setText("-");
        diffY.setText("-");
        diffZ.setText("-");
        distanceLabel.setText("No tracking data");
        hintLabel.setText("-");
        distanceLabel.setTextFill(Color.GRAY);
        
        drawView(canvasXY, "XY");
        drawView(canvasXZ, "XZ");
        drawView(canvasYZ, "YZ");
    }
    
    private void drawView(Canvas canvas, String viewType) {
        drawView(canvas, viewType, null, null);
    }
    
    private void drawView(Canvas canvas, String viewType, Vector3D toolPos, Vector3D targetPos) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        
        // clear canvas
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);
        
        if (toolPos == null && targetPos == null) {
            drawAxes(gc, width, height, viewType);
            return;
        }
        
        // calculate scaling
        double scaleX, scaleY;
        double offsetX, offsetY;
        double viewX1, viewX2, viewY1, viewY2;
        
        switch (viewType) {
            // go through 3 views
            case "XY":
                viewX1 = viewMinX;
                viewX2 = viewMaxX;
                viewY1 = viewMinY;
                viewY2 = viewMaxY;
                scaleX = (width - 2 * viewPadding) / (viewX2 - viewX1);
                scaleY = (height - 2 * viewPadding) / (viewY2 - viewY1);
                offsetX = viewPadding - viewX1 * scaleX;
                offsetY = viewPadding - viewY1 * scaleY;
                
                // draw plots
                drawGrid(gc, width, height, viewX1, viewX2, viewY1, viewY2, scaleX, scaleY, offsetX, offsetY);
                drawAxes(gc, width, height, viewType);
                
                // draw target
                if (targetPos != null) {
                    double targetScreenX = targetPos.getX() * scaleX + offsetX;
                    double targetScreenY = height - (targetPos.getY() * scaleY + offsetY);
                    gc.setFill(Color.RED);
                    gc.fillOval(targetScreenX - 5, targetScreenY - 5, 10, 10);
                    gc.setStroke(Color.RED);
                    gc.setLineWidth(2);
                    gc.strokeOval(targetScreenX - 5, targetScreenY - 5, 10, 10);
                }
                
                // draw tool
                if (toolPos != null) {
                    double toolScreenX = toolPos.getX() * scaleX + offsetX;
                    double toolScreenY = height - (toolPos.getY() * scaleY + offsetY);
                    gc.setFill(Color.BLUE);
                    gc.fillOval(toolScreenX - 5, toolScreenY - 5, 10, 10);
                    gc.setStroke(Color.BLUE);
                    gc.setLineWidth(2);
                    gc.strokeOval(toolScreenX - 5, toolScreenY - 5, 10, 10);
                    
                    // vector arrow from tool to target
                    if (targetPos != null) {
                        drawArrow(gc, toolScreenX, toolScreenY, 
                                 targetPos.getX() * scaleX + offsetX, 
                                 height - (targetPos.getY() * scaleY + offsetY));
                    }
                }
                break;
                
            case "XZ":
                viewX1 = viewMinX;
                viewX2 = viewMaxX;
                viewY1 = viewMinZ;
                viewY2 = viewMaxZ;
                scaleX = (width - 2 * viewPadding) / (viewX2 - viewX1);
                scaleY = (height - 2 * viewPadding) / (viewY2 - viewY1);
                offsetX = viewPadding - viewX1 * scaleX;
                offsetY = viewPadding - viewY1 * scaleY;
                
                drawGrid(gc, width, height, viewX1, viewX2, viewY1, viewY2, scaleX, scaleY, offsetX, offsetY);
                drawAxes(gc, width, height, viewType);
                
                if (targetPos != null) {
                    double targetScreenX = targetPos.getX() * scaleX + offsetX;
                    double targetScreenY = height - (targetPos.getZ() * scaleY + offsetY);
                    gc.setFill(Color.RED);
                    gc.fillOval(targetScreenX - 5, targetScreenY - 5, 10, 10);
                    gc.setStroke(Color.RED);
                    gc.setLineWidth(2);
                    gc.strokeOval(targetScreenX - 5, targetScreenY - 5, 10, 10);
                }
                
                if (toolPos != null) {
                    double toolScreenX = toolPos.getX() * scaleX + offsetX;
                    double toolScreenY = height - (toolPos.getZ() * scaleY + offsetY);
                    gc.setFill(Color.BLUE);
                    gc.fillOval(toolScreenX - 5, toolScreenY - 5, 10, 10);
                    gc.setStroke(Color.BLUE);
                    gc.setLineWidth(2);
                    gc.strokeOval(toolScreenX - 5, toolScreenY - 5, 10, 10);
                    
                    if (targetPos != null) {
                        drawArrow(gc, toolScreenX, toolScreenY,
                                 targetPos.getX() * scaleX + offsetX,
                                 height - (targetPos.getZ() * scaleY + offsetY));
                    }
                }
                break;
                
            case "YZ":
                viewX1 = viewMinY;
                viewX2 = viewMaxY;
                viewY1 = viewMinZ;
                viewY2 = viewMaxZ;
                scaleX = (width - 2 * viewPadding) / (viewX2 - viewX1);
                scaleY = (height - 2 * viewPadding) / (viewY2 - viewY1);
                offsetX = viewPadding - viewX1 * scaleX;
                offsetY = viewPadding - viewY1 * scaleY;
                
                drawGrid(gc, width, height, viewX1, viewX2, viewY1, viewY2, scaleX, scaleY, offsetX, offsetY);
                drawAxes(gc, width, height, viewType);
                
                if (targetPos != null) {
                    double targetScreenX = targetPos.getY() * scaleX + offsetX;
                    double targetScreenY = height - (targetPos.getZ() * scaleY + offsetY);
                    gc.setFill(Color.RED);
                    gc.fillOval(targetScreenX - 5, targetScreenY - 5, 10, 10);
                    gc.setStroke(Color.RED);
                    gc.setLineWidth(2);
                    gc.strokeOval(targetScreenX - 5, targetScreenY - 5, 10, 10);
                }
                
                if (toolPos != null) {
                    double toolScreenX = toolPos.getY() * scaleX + offsetX;
                    double toolScreenY = height - (toolPos.getZ() * scaleY + offsetY);
                    gc.setFill(Color.BLUE);
                    gc.fillOval(toolScreenX - 5, toolScreenY - 5, 10, 10);
                    gc.setStroke(Color.BLUE);
                    gc.setLineWidth(2);
                    gc.strokeOval(toolScreenX - 5, toolScreenY - 5, 10, 10);
                    
                    if (targetPos != null) {
                        drawArrow(gc, toolScreenX, toolScreenY,
                                 targetPos.getY() * scaleX + offsetX,
                                 height - (targetPos.getZ() * scaleY + offsetY));
                    }
                }
                break;
        }
    }
    
    private void drawGrid(GraphicsContext gc, double width, double height, 
                          double minX, double maxX, double minY, double maxY,
                          double scaleX, double scaleY, double offsetX, double offsetY) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        
        // vertical lines
        double stepX = (maxX - minX) / 10;
        for (double x = minX; x <= maxX; x += stepX) {
            double screenX = x * scaleX + offsetX;
            gc.strokeLine(screenX, viewPadding, screenX, height - viewPadding);
        }
        
        // horizontal lines
        double stepY = (maxY - minY) / 10;
        for (double y = minY; y <= maxY; y += stepY) {
            double screenY = height - (y * scaleY + offsetY);
            gc.strokeLine(viewPadding, screenY, width - viewPadding, screenY);
        }
    }
    
    private void drawAxes(GraphicsContext gc, double width, double height, String viewType) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        
        switch (viewType) {
            case "XY":
                // X axis
                gc.strokeLine(viewPadding, height - viewPadding, width - viewPadding, height - viewPadding);
                gc.strokeLine(width - viewPadding, height - viewPadding, width - viewPadding - 10, height - viewPadding - 5);
                gc.strokeLine(width - viewPadding, height - viewPadding, width - viewPadding - 10, height - viewPadding + 5);
                gc.fillText("X", width - viewPadding - 15, height - viewPadding + 15);
                
                // Y axis
                gc.strokeLine(viewPadding, height - viewPadding, viewPadding, viewPadding);
                gc.strokeLine(viewPadding, viewPadding, viewPadding - 5, viewPadding + 10);
                gc.strokeLine(viewPadding, viewPadding, viewPadding + 5, viewPadding + 10);
                gc.fillText("Y", viewPadding - 20, viewPadding + 5);
                break;
                
            case "XZ":
                // X axis
                gc.strokeLine(viewPadding, height - viewPadding, width - viewPadding, height - viewPadding);
                gc.strokeLine(width - viewPadding, height - viewPadding, width - viewPadding - 10, height - viewPadding - 5);
                gc.strokeLine(width - viewPadding, height - viewPadding, width - viewPadding - 10, height - viewPadding + 5);
                gc.fillText("X", width - viewPadding - 15, height - viewPadding + 15);
                
                // Z axis
                gc.strokeLine(viewPadding, height - viewPadding, viewPadding, viewPadding);
                gc.strokeLine(viewPadding, viewPadding, viewPadding - 5, viewPadding + 10);
                gc.strokeLine(viewPadding, viewPadding, viewPadding + 5, viewPadding + 10);
                gc.fillText("Z", viewPadding - 20, viewPadding + 5);
                break;
                
            case "YZ":
                // Y axis
                gc.strokeLine(viewPadding, height - viewPadding, width - viewPadding, height - viewPadding);
                gc.strokeLine(width - viewPadding, height - viewPadding, width - viewPadding - 10, height - viewPadding - 5);
                gc.strokeLine(width - viewPadding, height - viewPadding, width - viewPadding - 10, height - viewPadding + 5);
                gc.fillText("Y", width - viewPadding - 15, height - viewPadding + 15);
                
                // Z axis
                gc.strokeLine(viewPadding, height - viewPadding, viewPadding, viewPadding);
                gc.strokeLine(viewPadding, viewPadding, viewPadding - 5, viewPadding + 10);
                gc.strokeLine(viewPadding, viewPadding, viewPadding + 5, viewPadding + 10);
                gc.fillText("Z", viewPadding - 20, viewPadding + 5);
                break;
        }
    }
    
    private void drawArrow(GraphicsContext gc, double fromX, double fromY, double toX, double toY) {
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(2);
        gc.strokeLine(fromX, fromY, toX, toY);
        
        double angle = Math.atan2(toY - fromY, toX - fromX);
        double arrowLength = 15;
        double arrowAngle = Math.PI / 6;
        
        double x1 = toX - arrowLength * Math.cos(angle - arrowAngle);
        double y1 = toY - arrowLength * Math.sin(angle - arrowAngle);
        double x2 = toX - arrowLength * Math.cos(angle + arrowAngle);
        double y2 = toY - arrowLength * Math.sin(angle + arrowAngle);
        
        gc.strokeLine(toX, toY, x1, y1);
        gc.strokeLine(toX, toY, x2, y2);
    }
    
    private String directionHint(double dx, double dy, double dz) {
        String xDir = dx > 1 ? "right" : (dx < -1 ? "left" : "");
        String yDir = dy > 1 ? "down" : (dy < -1 ? "up" : "");
        String zDir = dz > 1 ? "forward" : (dz < -1 ? "back" : "");
        StringBuilder sb = new StringBuilder();
        if (!yDir.isEmpty()) sb.append(yDir).append(" ");
        if (!xDir.isEmpty()) sb.append(xDir).append(" ");
        if (!zDir.isEmpty()) sb.append(zDir);
        String txt = sb.toString().trim();
        return txt.isEmpty() ? "on target" : txt;
    }
    
    @Override
    public void close() {
        if (updateTimeline != null) {
            updateTimeline.stop();
            updateTimeline = null;
        }
        unregisterController();
    }
    
    @Override
    public void injectStatusLabel(javafx.scene.control.Label statusLabel) {
        // not used
    }
}

