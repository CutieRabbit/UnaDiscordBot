package sigtuna.discord.bus;

import java.util.ArrayList;
import java.util.List;

public class Route {
	
	String twRouteName;
	String enRouteName;
	List<Stop> stopList = new ArrayList<>();
	
	public Route(String twRouteName, String enRouteName) {
		this.twRouteName = twRouteName;
		this.enRouteName = enRouteName;
	}
	public void addStop(Stop stop) {
		stopList.add(stop);
	}
	public String getTWRouteName() {
		return twRouteName;
	}
	public String getENRouteName() {
		return enRouteName;
	}
}
