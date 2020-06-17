package sigtuna.discord.codeforces;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class DataBase {
	
	public static Map<String,String> map = new HashMap<String,String>();
	
	public static void lode() {
		try {
			File file = new File("CodeForcesAccount.txt");
			if(!file.exists()) file.createNewFile();
			Scanner cin = new Scanner(file);
			while(cin.hasNext()) {
				String uid = cin.next();
				String cfa = cin.next();
				map.put(uid, cfa);
			}
			cin.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void save() {
		try {
			File file = new File("CodeForcesAccount.txt");
			PrintWriter pw = new PrintWriter(file);
			for(Entry<String,String> entry : map.entrySet()) {
				pw.println(entry.getKey() + " " + entry.getValue());
			}
			pw.flush();
			pw.close();
			lode();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}