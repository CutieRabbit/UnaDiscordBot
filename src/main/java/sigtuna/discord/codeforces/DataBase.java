package sigtuna.discord.codeforces;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import sigtuna.discord.main.Main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DataBase {
	
	public static Map<String,String> UIDToAccount = new HashMap<>();
	public static Map<String, Integer> UIDtoRating = new HashMap<>();
	public static Map<String, Integer> monthSolveRecord = new HashMap<>();

	public static void load() {
		try {
			UIDToAccount.clear();
			ApiFuture<QuerySnapshot> querySnapshot = Main.firestore.collection("user").get();
			List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
			for(QueryDocumentSnapshot document : documents){
				String ID = document.getId();
				Map<String, Object> data = document.getData();
				String codeforcesAccount = data.get("CodeForcesAccount").toString();
				UIDToAccount.put(ID, codeforcesAccount);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static String findAccount(String targetAccount){
		for(Entry<String, String> entry : UIDToAccount.entrySet()){
			String account = entry.getValue();
			if(account.equals(targetAccount)){
				return entry.getKey();
			}
		}
		return null;
	}
}