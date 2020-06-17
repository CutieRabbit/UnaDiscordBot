package sigtuna.discord.bus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Bus {

	Map<String, Stop> StopIDMap = new HashMap<>();
	List<Route> routeArray = new ArrayList<Route>();

	public void refreshBusStop(int direction, String location, String routeID) throws IOException {
		String urlModule = "https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/";
		String url = String.format(urlModule + "%s/%s?&$format=JSON", location, routeID);
		// System.out.println(url);
		Connection connection = Jsoup.connect(url).followRedirects(false).ignoreContentType(true);
		connection = connection.validateTLSCertificates(false);
		String text = connection.get().text();
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(text);
		JsonArray rootObject = element.getAsJsonArray();
		StopIDMap.clear();
		for (int index = 0; index < rootObject.size(); index++) {
			JsonObject object = rootObject.get(index).getAsJsonObject();

			String stopID = "";
			String twStopName = "";
			String enStopName = "";
			String twSubStopName = "";
			String enSubStopName = "";
			int estimateTime = 0;
			int stopStatus = -1;
			int checkDirection = object.get("Direction").getAsInt();
			String plateNumb = object.get("PlateNumb").getAsString();

			if (!plateNumb.equals("-1")) {
				JsonObject stopName = object.get("StopName").getAsJsonObject();
				JsonObject subStopName = object.get("SubRouteName").getAsJsonObject();
				stopID = object.get("StopID").getAsString();
				twStopName = stopName.get("Zh_tw").getAsString();
				enStopName = stopName.get("En").getAsString();
				estimateTime = object.get("EstimateTime").getAsInt();
				stopStatus = object.get("StopStatus").getAsInt();
				twSubStopName = subStopName.get("Zh_tw").getAsString();
				enSubStopName = subStopName.get("En").getAsString();
			}

			Stop stop = new Stop(stopID, estimateTime, twStopName, enStopName, stopStatus, checkDirection,
					twSubStopName, enSubStopName);
			StopIDMap.put(stopID, stop);
		}
	}
	
	public void refreshBusRoute(String location, String routeID, boolean strict) throws IOException {
		String urlModule = "https://ptx.transportdata.tw/MOTC/v2/Bus/StopOfRoute/City/";
		String url = "";
		if(strict == true) {
			url = String.format(urlModule + "%s/%s?$filter=RouteName%%2FZh_tw%%20eq%%20'%s'&$format=JSON", location, routeID, routeID);
		}else {
			url = String.format(urlModule + "%s/%s?&$format=JSON", location, routeID, routeID);
		}
		Connection connection = Jsoup.connect(url).followRedirects(false).ignoreContentType(true);
		connection = connection.validateTLSCertificates(false);
		String text = connection.get().text();
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(text);
		JsonArray rootObject = element.getAsJsonArray();
		for (int index = 0; index < rootObject.size(); index++) {

			JsonObject object = rootObject.get(index).getAsJsonObject();
			JsonObject subRouteName = object.get("SubRouteName").getAsJsonObject();

			int direction = object.get("Direction").getAsInt();
			String twRouteName = subRouteName.get("Zh_tw").getAsString();
			String enRouteName = subRouteName.get("En").getAsString();
			Route route = new Route(twRouteName, enRouteName);

			JsonArray stopArray = object.get("Stops").getAsJsonArray();

			for (int stopIndex = 0; stopIndex < stopArray.size(); stopIndex++) {
				JsonObject stopObject = stopArray.get(stopIndex).getAsJsonObject();
				JsonObject stopName = stopObject.get("StopName").getAsJsonObject();
				String twStopName = stopName.get("Zh_tw").getAsString();
				String enStopName = stopName.get("En") == null ? "" : stopName.get("En").getAsString();
				String stopID = stopObject.get("StopID").getAsString();
				Stop stop = new Stop(stopID, twStopName, enStopName, direction, twRouteName, enRouteName);
				route.addStop(stop);
			}
			routeArray.add(route);
		}
	}
	
	public List<EmbedBuilder> getRouteStopListEmbed(String location,String routeID,int routeNumber) {
		Route route = routeArray.get(routeNumber);
		List<EmbedBuilder> embedList = new ArrayList<EmbedBuilder>();	
		List<Stop> routeList = new ArrayList<Stop>();
		routeList.addAll(route.stopList);
		for(int i = 0; i <= routeList.size()/25; i++) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle(location + " " + routeID + " 路線");
			embed.setDescription(route.getTWRouteName());
			for(int j = 0; j < Math.min(routeList.size()-25*i,25); j++) {
				Stop stop = routeList.get(i*25+j);
				embed.addInlineField(stop.getTWStopName(),stop.getStopID());
			}
			embedList.add(embed);
		}
		return embedList;
	}
	
	public EmbedBuilder getRouteListEmbed(String location,String routeID) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(location + " " + routeID + " 路線列表");
		System.out.println(routeArray.size());
		for(int i = 0; i < routeArray.size(); i++) {
			Route route = routeArray.get(i);
			Stop firstStop = route.stopList.get(0);
			Stop lastStop = route.stopList.get(route.stopList.size()-1);
			embed.addField(route.twRouteName + " " ,"編號"+i + "  " + firstStop.twStopName + "→" + lastStop.twStopName);
		}
		return embed;
	}
}
