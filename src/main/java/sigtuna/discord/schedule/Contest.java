package sigtuna.discord.schedule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TimerTask;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cfapi.main.CodeForcesContest;
import cfapi.main.CodeForcesContestData;
import sigtuna.discord.main.Main;

public class Contest extends TimerTask {

	Map<String, Boolean> openRegisterMentioned = new HashMap<>();
	Map<String, Boolean> tenMinutesLeftMentioned = new HashMap<>();
	Map<String, CodeForcesContestData> debug_map = new HashMap<>();
	List<CodeForcesContestData> list = new ArrayList<>();
	CodeForcesContest contest;

	@Override
	public void run() {	
		try {
			contest = new CodeForcesContest();
			list = contest.getBeforeContest(false);
			initMap();
			initDataBase();
			//debug_vituralContest();
			checkOpenRegister();
			saveDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void debug_vituralContest() {
		list.add(contest.getContestData(1310));
		list.add(contest.getContestData(1314));
		list.add(contest.getContestData(1315));
		tenMinutesLeftMentioned.put("1310", false);
		tenMinutesLeftMentioned.put("1314", false);
		tenMinutesLeftMentioned.put("1315", false);
	}

	public void checkOpenRegister() throws FileNotFoundException {
		List<String> openRegisterList = new ArrayList<String>();
		List<String> tenMinutesList = new ArrayList<String>();
		for (CodeForcesContestData cfcd : list) {
			String ID = cfcd.getID();
			long relative = Math.abs(cfcd.getRelativeTimeSeconds());
			if (relative <= 86400 && (!openRegisterMentioned.containsKey(ID) || openRegisterMentioned.get(ID) == false)) {
				openRegisterMentioned.put(cfcd.getID(), true);
				openRegisterList.add(ID);
			}
			if (relative <= 600 && (!tenMinutesLeftMentioned.containsKey(ID) || tenMinutesLeftMentioned.get(ID) == false)) {
				tenMinutesLeftMentioned.put(cfcd.getID(), true);
				tenMinutesList.add(ID);
			}
		}
		if(openRegisterList.size() > 0) {
			alert("OR", openRegisterList);
		}
		if(tenMinutesList.size() > 0) {
			alert("TEN", tenMinutesList);
		}
	}

	public void alert(String alertType, List<String> ID) throws FileNotFoundException {
		DiscordApi api = Main.api;
		for (Server server : api.getServers()) {
			String ServerID = server.getIdAsString();
			// Query MentionTag and ChannelID
			File file = new File("./ServerBase/" + ServerID + "/ServerConfig.json");
			Scanner cin = new Scanner(file);
			String text = cin.next();
			JsonObject object = new JsonParser().parse(text).getAsJsonObject();
			if (object.get("CFMention").getAsString().equals("unset")) {
				continue;
			}
			String mentionTag = object.get("CFMention").getAsString();
			String channelID = object.get("CFContestChannel").getAsString();
			TextChannel channel = api.getTextChannelById(channelID).get();
			channel.sendMessage(mentionTag);
			if (alertType.equals("OR")) {
				channel.sendMessage(OpenRegisterEmbed(ID));
			} else if (alertType.equals("TEN")) {
				channel.sendMessage(TenMinutesLeftEmbed(ID));
			}
			cin.close();
		}
	}
	
	public EmbedBuilder OpenRegisterEmbed(List<String> IDList) {
		EmbedBuilder embed = new EmbedBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat();
		Date date = null;
		String contestInfo = "";
		for(int i = 0; i < IDList.size(); i++) {
			int ID = Integer.parseInt(IDList.get(i));
			CodeForcesContestData contestData = contest.getContestData(ID);
			if(debug_map.containsKey(IDList.get(i))) contestData = debug_map.get(IDList.get(i));
			String contestName = contestData.getName();
			String fieldKey = String.format("競賽註冊連結(%d)", i+1);
			String descriptionLine = String.format("%d. %s\n", i+1, contestName);
			contestInfo += descriptionLine;
			embed.addField(fieldKey, "https://codeforces.com/contestRegistration/" + ID);
			date = new Date(contestData.getStartTimeSeconds()*1000);
		}
		embed.setTitle("以下競賽只剩下24小時"); 
		embed.setDescription(contestInfo);
		embed.addField("競賽時間", sdf.format(date));
		return embed;
	}

	public EmbedBuilder TenMinutesLeftEmbed(List<String> IDList) {
		EmbedBuilder embed = new EmbedBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat();
		String contestInfo = "";
		String contestIDList = String.join(",", IDList);
		Date date = null;
		for(int i = 0; i < IDList.size(); i++) {
			int ID = Integer.parseInt(IDList.get(i));
			CodeForcesContestData contestData = contest.getContestData(ID);
			if(debug_map.containsKey(IDList.get(i))) contestData = debug_map.get(IDList.get(i));
			String contestName = contestData.getName();
			String descriptionLine = String.format("%d. %s\n", i+1, contestName);
			contestInfo += descriptionLine;
			date = new Date(contestData.getStartTimeSeconds()*1000);
		}
		embed.setTitle("以下競賽將於10分鐘後開始"); 
		embed.setDescription(contestInfo);
		embed.addField("競賽連結", "https://codeforces.com/contests/" + contestIDList);
		embed.addField("競賽時間", sdf.format(date));
		return embed;
	}

	public void saveDataBase() throws IOException {
		JsonObject mainObject = new JsonObject();
		JsonArray array = new JsonArray();
		for (Entry<Integer, CodeForcesContestData> set : contest.getContestEntrySet()) {
			int contestID = set.getKey();
			String strID = String.valueOf(contestID);
			CodeForcesContestData cfcd = set.getValue();
			JsonObject object = new JsonObject();
			object.addProperty("id", contestID);
			object.addProperty("name", cfcd.getName());
			object.addProperty("openRegisterMentioned", openRegisterMentioned.get(strID));
			object.addProperty("tenMinutesLeftMentioned", tenMinutesLeftMentioned.get(strID));
			array.add(object);
		}
		mainObject.add("contest", array);
		File file = new File("./ContestPlay.json");
		if (!file.exists()) {
			file.createNewFile();
		}
		PrintWriter writer = new PrintWriter(file);
		writer.println(mainObject.toString());
		writer.close();
	}

	public void initDataBase() throws IOException {
		File file = new File("./ContestPlay.json");
		if (!file.exists()) {
			saveDataBase();
			return;
		}
		Scanner cin = new Scanner(file);
		String text = cin.nextLine();
		JsonObject mainObject = new JsonParser().parse(text).getAsJsonObject();
		JsonArray array = mainObject.get("contest").getAsJsonArray();
		for(int i = 0; i < array.size(); i++) {
			JsonObject object = array.get(i).getAsJsonObject();
			String ID = object.get("id").getAsString();
			boolean openRegister = object.get("openRegisterMentioned").getAsBoolean();
			boolean tenMinutesLeft = object.get("tenMinutesLeftMentioned").getAsBoolean();
			openRegisterMentioned.put(ID, openRegister);
			tenMinutesLeftMentioned.put(ID, tenMinutesLeft);
		}
		cin.close();
	}
	
	public void initMap() {
		for (Entry<Integer, CodeForcesContestData> set : contest.getContestEntrySet()) {
			CodeForcesContestData cfcd = set.getValue();
			openRegisterMentioned.put(cfcd.getID(),false);
			tenMinutesLeftMentioned.put(cfcd.getID(),false);
		}
	}

}
