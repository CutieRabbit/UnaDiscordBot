package sigtuna.discord.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Statement;
import java.util.*;

import cfapi.main.CodeForcesProblemSet;
import cfapi.main.CodeForcesStatus;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.server.Server;

import cfapi.main.CodeForcesProblemData;
import org.javacord.api.entity.user.User;
import org.jsoup.nodes.Document;
import sigtuna.discord.codeforces.ConnectToDiscord;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.codeforces.ProblemSelect;
import sigtuna.discord.codeforces.UserSubmissionDatabase;
import sigtuna.discord.event.JoinEvent;
import sigtuna.discord.schedule.*;
import sigtuna.discord.util.FileIO;

public class Main {

	public static String prefix = "<";
	public static DiscordApi api;
	public static Statement state;
	public static List<CodeForcesProblemData> problemList;

	public static void main(String[] args) {

		try {
			
			String token = new FileIO("token.txt").getScanner().next();

			api = new DiscordApiBuilder().setToken(token).login().join();

			EventRegister.register();

			Timer contest = new Timer();
			Timer connectToDiscord = new Timer();
			Timer autoCFDatabaseSave = new Timer();
			Timer rank = new Timer();
			Timer status = new Timer();
			Timer updateDataBase = new Timer();

			DataBase.lode();
			initServerDataBase();
			makeProblemSet();
			initUserStatus();

			contest.schedule(new Contest(), 0, 60000);
			autoCFDatabaseSave.schedule(new AutoCodeForcesDataBaseSave(), 0, 300000);
			connectToDiscord.schedule(new ConnectToDiscord(), 0, 30000);
			rank.schedule(new CodeForcesRank(), 0, 1000*10);
			status.schedule(new UpdateStatus(), 0, 1000*3);
			updateDataBase.schedule(new UpdateHandleDatabase(), 0, 1000*600);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void initServerDataBase() {
		for(Server server : api.getServers()) {
			JoinEvent join = new JoinEvent();
			String serverID = server.getIdAsString();
			String channelID = server.getTextChannels().get(0).getIdAsString();
			String serverName = server.getName();
			join.JoinServer(serverID, channelID, serverName);
		}
	}

	public static void initUserStatus() {
		for(String name : DataBase.name) {
			UserSubmissionDatabase.load(name);
			UpdateStatus.updateHandle.add(name);
		}
		new ProblemSelect();
	}

	public static void makeProblemSet(){
		try {
			CodeForcesProblemSet problemSet = new CodeForcesProblemSet();
			Document doc = problemSet.getDoc();
			String text = doc.text();
			problemList = CodeForcesProblemSet.make(text);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
