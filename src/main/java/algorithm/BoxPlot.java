package algorithm;

import java.util.ArrayList;
import java.util.List;

public class BoxPlot {

	private List<Double> errors = new ArrayList();
	private double min;
	private double max;
	private double q1;
	private double median;
	private double q3;

	public List<Double> getErrors() {
		return errors;
	}

	public void setErrors(List<Double> errors) {
		this.errors = errors;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getQ1() {
		return q1;
	}

	public void setQ1(double q1) {
		this.q1 = q1;
	}

	public double getMedian() {
		return median;
	}

	public void setMedian(double median) {
		this.median = median;
	}

	public double getQ3() {
		return q3;
	}

	public void setQ3(double q3) {
		this.q3 = q3;
	}

}
