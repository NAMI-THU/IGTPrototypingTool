package algorithm;

import java.util.ArrayList;
import java.util.List;

public class ToolMeasure {

	private String name;
	private List<Measurement> measurements;
	private Measurement averageMeasurement;

	public ToolMeasure(String name) {
		this.name = name;
		this.measurements = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Measurement> getMeasurement() {
		return measurements;
	}

	// public void setMeasurements(List<Measurement> measurements) {
	// this.measurements=measurements;
	// }

	public void addMeasurement(Measurement measurement) {
		measurements.add(measurement);
	}

	public Measurement getAverageMeasurement() {
		return averageMeasurement;
	}

	public void setAverageMeasurement(Measurement averageMeasurement) {
		this.averageMeasurement = averageMeasurement;
	}

}
