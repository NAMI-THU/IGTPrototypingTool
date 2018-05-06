package algorithm;

public class DataService {
	private DataManager dataManager;
	private DataProcessor dataProcessor;
	
	//Constructor 
	public DataService() {
	
	}
	
	public void loadNextData() {
		
		this.dataManager.getNextData();
		
		
	}
	
	public void getAverageMeasurement() {}
	
	public void getJitter() {}
	
	public void getAccuracy() {}
	
}
