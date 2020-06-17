package sigtuna.discord.main;

import java.io.FileNotFoundException;
import java.sql.Statement;
import java.util.List;
import java.util.Timer;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;

import cfapi.main.CodeForcesProblemData;
import sigtuna.discord.codeforces.ConnectToDiscord;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.event.JoinEvent;
import sigtuna.discord.schedule.AutoCodeForcesDataBaseSave;
import sigtuna.discord.schedule.CodeForcesRank;
import sigtuna.discord.schedule.Contest;
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

			System.out.println("Login ok.");

			EventRegister.register();

			System.out.println("Event rigister ok.");

			Timer contest = new Timer();
			Timer connectToDiscord = new Timer();
			Timer autoCFDatabaseSave = new Timer();
			Timer rank = new Timer();
//			Timer timerPhoto = new Timer();
			
			DataBase.lode();
			initServerDataBase();
			
			contest.schedule(new Contest(), 0, 60000);
			autoCFDatabaseSave.schedule(new AutoCodeForcesDataBaseSave(), 0, 300000);
			connectToDiscord.schedule(new ConnectToDiscord(), 0, 30000);
			rank.schedule(new CodeForcesRank(), 0, 1000*600);
//			timerPhoto.schedule(new FixedTimeEvent(), 0, 1000);
			
			System.out.println("Timer ok.");
			System.out.println("working...");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
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
	
}
