package sigtuna.discord.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.ServerUpdater;
import org.javacord.api.entity.user.User;

import sigtuna.discord.classes.UserInfo;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.main.CodeForces;
import sigtuna.discord.main.Main;

public class CodeForcesRank extends TimerTask {

	static Map<String, String> user = DataBase.map;
	String[] roleNameArray = { "CF-Newbie", "CF-Pupil", "CF-Specialist", "CF-Expert", "CF-Candidate Master",
			"CF-Master", "CF-Grandmaster", "CF-International Master", "CF-International Grandmaster",
			"CF-Legendary Grandmaster" };
	Map<String, Role> roleName = new HashMap<String, Role>();

	public void setUpRole() {
		for (int i = 0; i < roleNameArray.length; i++) {
			Server server = Main.api.getServerById(534366668076613632L).get();
			Role role = server.getRolesByName(roleNameArray[i]).get(0);
			String name = roleNameArray[i].replaceAll("CF-", "").toLowerCase();
			System.out.println(name);
			roleName.put(name, role);
		}
	}

	public void clearDrop() {
		List<String> bin = new ArrayList<String>();
		for (Entry<String, String> entry : user.entrySet()) {
			if (entry.getValue().equals("######")) {
				bin.add(entry.getKey());
			}
		}
		for (String str : bin) {
			user.remove(str);
		}
	}

	public void run() {
		setUpRole();
		Server server = Main.api.getServerById(534366668076613632L).get();
		ServerUpdater serverUpdater = new ServerUpdater(server);
		try {
			for (Entry<String, String> entry : user.entrySet()) {
				CodeForces cf = new CodeForces();
				User user = Main.api.getUserById(entry.getKey()).get();				
				List<Role> roles = user.getRoles(server);
				for (String str : roleNameArray) {
					Role role = server.getRolesByName(str).get(0);
					if (roles.contains(role)) {
//						System.out.println(user.getName() + " , " + user.getIdAsString() + " , " + role.getName());
						serverUpdater.removeRoleFromUser(user, role);
					}
				}
				if (!entry.getValue().equals("######")) {
					UserInfo info = cf.getUserData(entry.getValue());
					if(info == null) continue;
					String rank = info.rank;
					if (rank.toLowerCase().equals("unrated")) {
						continue;
					}
					serverUpdater.addRoleToUser(user, roleName.get(rank));
//					System.out.println(user.getName() + " , " + user.getIdAsString() + " , " + roleName.get(rank).getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		clearDrop();
		DataBase.save();
		try {
			serverUpdater.update().get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
