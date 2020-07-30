package sigtuna.discord.main;

import java.sql.Statement;
import java.util.*;

import cfapi.main.CodeForcesProblemSet;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;

import cfapi.main.CodeForcesProblemData;
import org.jsoup.nodes.Document;
import sigtuna.discord.codeforces.ConnectToDiscord;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.event.JoinEvent;
import sigtuna.discord.schedule.*;

public class Main {

	public static String prefix = "<";
	public static DiscordApi api;
	public static Statement state;
	public static List<CodeForcesProblemData> problemList;

	public static void main(String[] args) {

		try {
			
			String token = args[0];

			api = new DiscordApiBuilder().setToken(token).login().join();

			EventRegister.register();

			Timer contest = new Timer();
			Timer connectToDiscord = new Timer();
			Timer autoCFDatabaseSave = new Timer();
			Timer rank = new Timer();
			Timer status = new Timer();
			Timer updateDataBase = new Timer();

			DataBase.load();
			initServerDataBase();
			makeProblemSet();

			contest.schedule(new Contest(), 0, 60000);
			autoCFDatabaseSave.schedule(new AutoCodeForcesDataBaseSave(), 0, 300000);
			connectToDiscord.schedule(new ConnectToDiscord(), 0, 30000);
			rank.schedule(new CodeForcesRank(), 0, 1000*10);

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
