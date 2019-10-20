package online;

public class RecordStats {
	
	private int time;
	private String id;
	private double lowerBound;
	private double upperBound;
	private double currentEstimation;

	
	public RecordStats() {
		
	}
	
	public RecordStats(String id, int time) {
		super();
		this.id = id;
		this.time = time;
	}
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getCurrentEstimation() {
		return currentEstimation;
	}
	public void setCurrentEstimation(double currentEstimation) {
		this.currentEstimation = currentEstimation;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}
	
	
}
