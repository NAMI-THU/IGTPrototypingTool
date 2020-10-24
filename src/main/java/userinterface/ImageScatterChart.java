package userinterface;

import javafx.event.EventHandler;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/*
 * TODO check if change listeners on property are sensible 
 */

/*
 * this class displays an image that is loaded on top of a scatter chart. the image position and size can be 
 * adjusted so the tracking data can be displayed in relation to it.
 * do not override layoutplotchildren, it is needed to display tracking data
 */
@SuppressWarnings("rawtypes")
public class ImageScatterChart extends ScatterChart {
	
	private ImageView iv;
	private Double oldX;
	private Double oldY;
	private Double savedScale = 1.0;
	private Double savedX = 0.0;
	private Double savedY = 0.0;

	/**
	 * constructor with default axis size -500 to 500
	 * @param title is the displayed name of the chart 
	 */
	@SuppressWarnings("unchecked")
	public ImageScatterChart(String title) {
		super(new NumberAxis(-500, 500, 0), new NumberAxis(-500, 500, 0));
		this.setTitle(title);
		this.setPrefSize(400, 300);
		this.hideAxis();
	}
	
	/**
	 * @param title is the displayed name of the chart
	 * @param x is a the x axis of the chart as numberaxis
	 * @param y is a the y axis of the chart as numberaxis
	 */
	@SuppressWarnings("unchecked")
	public ImageScatterChart(String title, NumberAxis x, NumberAxis y) {
		super(x, y);
		setTitle(title);
		setPrefSize(400, 300);
		hideAxis();
	}
	
	public ImageView getIv() {
		return iv;
	}
	
	/**
	 * sets imageview and adds listeners so size and position can be changed
	 * @param iv imageview of image to be displayed in the chart
	 */
	@SuppressWarnings("unchecked")
	public void setIv(ImageView iv) {
		this.iv = iv;
		getPlotChildren().add(iv);
		if(iv.getImage() != null) {
			iv.setOnMousePressed(grabImage());
			iv.setOnMouseDragged(dragImage());
			this.setOnMouseEntered(hoverAction());
			iv.scaleXProperty().addListener((event, oldX, newX) -> {
				setSavedScale((Double) newX);
			});
		}
	}
		
	public Double getSavedScale() {
		return savedScale;
	}

	public void setSavedScale(Double savedScale) {
		this.savedScale = savedScale;
		iv.setScaleX(savedScale);
		iv.setScaleY(savedScale);
	}

	public Double getSavedX() {
		return savedX;
	}

	public void setSavedX(Double savedX) {
		this.savedX = savedX;
		iv.setX(savedX);
	}

	public Double getSavedY() {
		return savedY;
	}

	public void setSavedY(Double savedY) {
		this.savedY = savedY;
		iv.setY(savedY);
	}

	/**
	 * hides all visible parts of the axis
	 */
	private void hideAxis() {
		this.getXAxis().setTickLabelsVisible(false);
		this.getXAxis().setTickMarkVisible(false);
		this.getYAxis().setTickLabelsVisible(false);
		this.getYAxis().setTickMarkVisible(false);
		this.setHorizontalGridLinesVisible(false);
		this.setHorizontalZeroLineVisible(false);
		this.setVerticalGridLinesVisible(false);
		this.setVerticalZeroLineVisible(false);
	}
	
	/**
	 * sets old x and y values for reference
	 * @return mouseevent on mouse pressed
	 */
	private EventHandler<MouseEvent> grabImage() {
		return event -> {
			// maybe oldX = savedX ? 
			oldX = event.getScreenX();
			oldY = event.getScreenY();
		};
	}
	
	/**
	 * moves imageview according to mouse movement
	 * @return mouseevent on mouse dragged
	 */
	private EventHandler<MouseEvent> dragImage() {
		return event -> {
			double deltaX = event.getScreenX() - oldX;
			double deltaY = event.getScreenY() - oldY;
			iv.setX(iv.getX() + deltaX);
			iv.setY(iv.getY() + deltaY);
			oldX = event.getScreenX();
			oldY = event.getScreenY();
			setSavedX(iv.getX());
			setSavedY(iv.getY());
		};
	}
	
	/**
	 * adds zoom on scroll to object
	 * @return mouseevent on hover over object
	 */
	private EventHandler<MouseEvent> hoverAction() {
		return event -> {
			this.setOnScroll(zoom());
		};
	}
	
	/**
	 * increases scale of imageview on scroll up
	 * decreases scale of imageview on scroll down
	 * @return scrollevent 
	 */
	private EventHandler<ScrollEvent> zoom() {
		return event -> {
			double scaleX = iv.getScaleX();
			double scaleY = iv.getScaleY();
			// move mouse wheel up: zoom in
			if(event.getDeltaY() > 0) {
				iv.setScaleX(scaleX + 0.05);
				iv.setScaleY(scaleY + 0.05);
				
			// move mouse wheel down: zoom out
			} else if(event.getDeltaY() < 0) {
				iv.setScaleX(scaleX - 0.05);
				iv.setScaleY(scaleY - 0.05);
			}
			
			// implement that keys could be used if no mouse wheel available
//			switch(event.getCode()) {
//			case PLUS: 
//				chart1.getIv().setFitWidth(w*1.1);
//				chart1.getIv().setFitHeight(h*1.1);
//				System.out.println(chart1.getIv().getFitHeight());
//				break;
//			case MINUS: 
//				chart1.getIv().setFitWidth(w*0.9);
//				chart1.getIv().setFitHeight(h*0.9);
//				break;
//			default: break;
//			}
			
		};
	}
	
}
