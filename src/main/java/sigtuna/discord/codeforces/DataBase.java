package sigtuna.discord.codeforces;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;

public class DataBase {
	
	public static Map<String,String> map = new HashMap<String,String>();
	public static List<String> name = new ArrayList<>();

	public static void lode() {
		try {
			File file = new File("CodeForcesAccount.txt");
			if(!file.exists()) file.createNewFile();
			Scanner cin = new Scanner(file);
			while(cin.hasNext()) {
				String uid = cin.next();
				String cfa = cin.next();
				if(!name.contains(cfa)){
					name.add(cfa);
				}
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