package sigtuna.discord.schedule;

import cfapi.main.CodeForcesContest;
import cfapi.main.CodeForcesContestData;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import sigtuna.discord.main.Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Contest extends TimerTask {

	Map<String, Boolean> openRegisterMentioned = new HashMap<>();
	Map<String, Boolean> tenMinutesLeftMentioned = new HashMap<>();
	List<CodeForcesContestData> list = new ArrayList<>();
	public static List<CodeForcesContestData> debugContestList = new ArrayList<>();
	CodeForcesContest contest;

	@Override
	public void run() {
		try {
			contest = new CodeForcesContest();
			list = contest.getBeforeContest(false);
			list.addAll(debugContestList);
			checkOpenRegister();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public void checkOpenRegister() throws FileNotFoundException {
		List<String> openRegisterList = new ArrayList<String>();
		List<String> tenMinutesList = new ArrayList<String>();
		for (CodeForcesContestData cfcd : list) {
			String ID = cfcd.getID();
			long relative = Math.abs(cfcd.getRelativeTimeSeconds());
			if (relative <= 86400 && relative >= 86400-600 && (!openRegisterMentioned.containsKey(ID) || !openRegisterMentioned.get(ID))) {
				openRegisterMentioned.put(cfcd.getID(), true);
				openRegisterList.add(ID);
			}
			if (relative <= 600 && relative >= 0 && (!tenMinutesLeftMentioned.containsKey(ID) || !tenMinutesLeftMentioned.get(ID))) {
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
		Optional<Server> serverOptional = api.getServerById("534366668076613632");
		Server server = null;
		if (serverOptional.isPresent()) {
			server = serverOptional.get();
		} else {
			throw new NullPointerException("在CF Alert中，找不到Server。");
		}
		List<Role> roles = server.getRolesByName("CodeForces");
		if (roles.size() == 0) {
			throw new NullPointerException("在CF Contest Alert中，找不到名為CodeForces的Role。");
		}
		Role role = roles.get(0);
		Optional<ServerChannel> serverChannelOptional = server.getChannelById("534366668076613635");
		ServerChannel serverChannel = null;
		if (serverChannelOptional.isPresent()) {
			serverChannel = serverChannelOptional.get();
		} else {
			throw new NullPointerException("在CF Contest Alert中，找不到播報頻道。");
		}
		TextChannel channel = null;
		String mentionTag = role.getMentionTag();
		Optional<TextChannel> textChannelOptional = serverChannel.asTextChannel();
		if (textChannelOptional.isPresent()) {
			channel = textChannelOptional.get();
		} else {
			throw new NullPointerException("在CF Contest Alert中，找不到播報頻道。");
		}
		channel.sendMessage(mentionTag);
		if (alertType.equals("OR")) {
			channel.sendMessage(OpenRegisterEmbed(ID));
		} else if (alertType.equals("TEN")) {
			channel.sendMessage(TenMinutesLeftEmbed(ID));
		}
	}

	public EmbedBuilder OpenRegisterEmbed(List<String> IDList) {
		EmbedBuilder embed = new EmbedBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat();
		DateTime dateTime = null;
		String contestInfo = "";
		for(int i = 0; i < IDList.size(); i++) {
			int ID = Integer.parseInt(IDList.get(i));
			CodeForcesContestData contestData = contest.getContestData(ID);
			String contestName = contestData.getName();
			String fieldKey = String.format("競賽註冊連結(%d)", i+1);
			String descriptionLine = String.format("%d. %s\n", i+1, contestName);
			contestInfo += descriptionLine;
			embed.addField(fieldKey, "https://codeforces.com/contestRegistration/" + ID);
			dateTime = new DateTime(contestData.getStartTimeSeconds() * 1000);
			dateTime = dateTime.withZone(DateTimeZone.forID("Asia/Taipei"));
		}
		embed.setTitle("以下競賽只剩下24小時");
		embed.setDescription(contestInfo);
		embed.addField("競賽時間", dateTime.toString("yyyy/MM/dd HH:mm:ss"));
		return embed;
	}

	public EmbedBuilder TenMinutesLeftEmbed(List<String> IDList) {
		EmbedBuilder embed = new EmbedBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat();
		String contestInfo = "";
		String contestIDList = String.join(",", IDList);
		DateTime dateTime = null;
		for(int i = 0; i < IDList.size(); i++) {
			int ID = Integer.parseInt(IDList.get(i));
			CodeForcesContestData contestData = contest.getContestData(ID);
			String contestName = contestData.getName();
			String descriptionLine = String.format("%d. %s\n", i+1, contestName);
			contestInfo += descriptionLine;
			dateTime = new DateTime(contestData.getStartTimeSeconds() * 1000);
			dateTime = dateTime.withZone(DateTimeZone.forID("Asia/Taipei"));
		}
		embed.setTitle("以下競賽將於10分鐘後開始");
		embed.setDescription(contestInfo);
		embed.addField("競賽連結", "https://codeforces.com/contests/" + contestIDList);
		embed.addField("競賽時間", dateTime.toString("yyyy/MM/dd HH:mm:ss"));
		return embed;
	}

}