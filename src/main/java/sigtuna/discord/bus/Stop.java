package sigtuna.discord.bus;

public class Stop {
	
	String stopID;
	int estimateTime;
	String twStopName;
	String enStopName;
	String twSubStopName;
	String enSubStopName;
	String subStopName;
	int stopStatus;
	int direction;

	public Stop(String stopID, int estimateTime, String twStopName, String enStopName, int stopStatus, int direction,
			String twSubStopName, String enSubStopName) {
		this.stopID = stopID;
		this.estimateTime = estimateTime;
		this.twStopName = twStopName;
		this.enStopName = enStopName;
		this.stopStatus = stopStatus;
		this.direction = direction;
		this.enSubStopName = enSubStopName;
		this.twSubStopName = twSubStopName;
	}
	public Stop(String stopID, String twStopName, String enStopName, int direction,	String twSubStopName, String enSubStopName) {
		this.stopID = stopID;
		this.twStopName = twStopName;
		this.enStopName = enStopName;
		this.direction = direction;
		this.enSubStopName = enSubStopName;
		this.twSubStopName = twSubStopName;
	}

	public String getStopID() {
		return stopID;
	}

	public int getestimateTime() {
		return estimateTime;
	}

	public String getTWStopName() {
		return twStopName;
	}

	public String getENStopName() {
		return enStopName;
	}

	public int getStopStatus() {
		return stopStatus;
	}

	public int getDirection() {
		return direction;
	}

	public String getTWSubStopName() {
		return twSubStopName;
	}

	public String getENSubStopName() {
		return enSubStopName;
	}
}