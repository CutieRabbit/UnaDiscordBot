package sigtuna.discord.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Statement;
import java.util.*;

import cfapi.main.CodeForcesProblemSet;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
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
	public static FirebaseApp firebaseApp;
	public static Firestore firestore;
	public static Statement state;
	public static List<CodeForcesProblemData> problemList;

	public static void main(String[] args) {

		try {
			
			String token = args[0];

			api = new DiscordApiBuilder().setToken(token).setAllIntents().login().join();

			EventRegister.register();

			System.out.println("UnaBot Start.");

			Timer contest = new Timer();
			Timer connectToDiscord = new Timer();
			Timer rank = new Timer();

			initServerDataBase();
			initFirebaseDatabase();
			DataBase.load();
			makeProblemSet();

			contest.schedule(new Contest(), 0, 60*1000);
			connectToDiscord.schedule(new ConnectToDiscord(), 0, 30000);
			rank.schedule(new CodeForcesRank(), 0, 1000*15);

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

	public static void initFirebaseDatabase() throws IOException {
		FileInputStream serviceAccount = new FileInputStream("./serviceAccountKey.json");
		FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
		firebaseApp = FirebaseApp.initializeApp(options);
		firestore = FirestoreClient.getFirestore();
		System.out.println("Firebase and firestore init.");
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
