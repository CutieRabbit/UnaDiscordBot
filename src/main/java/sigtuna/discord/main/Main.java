package sigtuna.discord.main;

import cfapi.main.CodeForcesProblemData;
import cfapi.main.CodeForcesProblemSet;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.jsoup.nodes.Document;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.event.CodeForcesSlashCommandListener;
import sigtuna.discord.event.JoinEvent;
import sigtuna.discord.schedule.CodeForcesRank;
import sigtuna.discord.schedule.Contest;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

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

			api = new DiscordApiBuilder().setToken(token).login().join();

			EventRegister.register();

			System.out.println("UnaBot Start.");

			Timer contest = new Timer();
			Timer rank = new Timer();

			initFirebaseDatabase();
			DataBase.load();
			makeProblemSet();

			contest.schedule(new Contest(), 0, 60*1000);
			rank.schedule(new CodeForcesRank(), 0, 1000*5);

			if(api.getServerById("534366668076613632").isPresent()) {
				Server codeCommunity = api.getServerById("534366668076613632").get();
				SlashCommandRegister.register(codeCommunity);
			}

			api.addSlashCommandCreateListener(new CodeForcesSlashCommandListener());

		} catch (Exception e) {
			e.printStackTrace();
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
