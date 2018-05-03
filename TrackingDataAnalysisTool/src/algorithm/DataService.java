package algorithm;

public class DataService {
	private DataManager dataManager;
	private DataProcessor dataProcessor;
	
	//Constructor 
	public DataService() {
		String DataManager = new String();
		
		String DataProcessor = new String(); 
	}
	
	public void loadNextData() {
		
		this.dataManager.getNextData();
		
		
	}
	
	public void getAverageMeasurement() {
		int average;
		average = ... / countToGetNext; 
	}
	
	public void getJitter() {}
	
	public void getAccuracy() {}
	
}
