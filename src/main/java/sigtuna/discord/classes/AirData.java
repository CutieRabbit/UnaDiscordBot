package sigtuna.discord.classes;

public class AirData {
	
	String county;
	String station;
	String AQI;
	String status;
	
	public AirData(String county, String station, String AQI, String status) {
		this.county = county;
		this.station = station;
		this.AQI = AQI;
		this.status = status;
	}
	
	public String getCounty() {
		return county;
	}
	
	public String getStation() {
		return station;
	}
	
	public String getAQI() {
		return AQI;
	}
	
	public String getStatus() {
		return status;
	}
	
}
