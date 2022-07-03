package sigtuna.discord.schedule;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import cfapi.main.CodeForcesSubmissionData;
import cfapi.main.CodeForcesUser;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.ServerUpdater;
import org.javacord.api.entity.user.User;

import org.joda.time.DateTime;
import sigtuna.discord.classes.UserStatus;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.main.CodeForces;
import sigtuna.discord.main.Main;

import javax.xml.crypto.Data;

public class CodeForcesRank extends TimerTask {

	List<String> accountList = new ArrayList<>();
	String[] roleNameArray = { "CF-Newbie", "CF-Pupil", "CF-Specialist", "CF-Expert", "CF-Candidate Master",
			"CF-Master", "CF-Grandmaster", "CF-International Master", "CF-International Grandmaster",
			"CF-Legendary Grandmaster" };

	Map<String, Role> roleName = new HashMap<String, Role>();
	int index = 0;

	public void setUpRole() {
		for (int i = 0; i < roleNameArray.length; i++) {
			Server server = Main.api.getServerById(534366668076613632L).get();
			Role role = server.getRolesByName(roleNameArray[i]).get(0);
			String name = roleNameArray[i].replaceAll("CF-", "").toLowerCase();
			roleName.put(name, role);
		}
	}

	public void make(String account){

		setUpRole();

		try {
			Optional<Server> serverOptional = Main.api.getServerById(534366668076613632L);
			Server server;

			if(serverOptional.isPresent()){
				server = serverOptional.get();
			}else{
				throw new NullPointerException("Cannot Find Such Server");
			}

			ServerUpdater serverUpdater = new ServerUpdater(server);

			CodeForcesUser accountData = new CodeForcesUser(account);
			String uid = DataBase.findAccount(account);
			User user = Main.api.getUserById(uid).get();
			List<Role> roles = user.getRoles(server);
			String rank = accountData.getRank().toLowerCase();
			if(rank.equals("unrated") || rank.equalsIgnoreCase("Headquarters")){
				return;
			}

			/* Fetch Rating */
			int rating = (int) accountData.getRating();
			DataBase.UIDtoRating.put(uid, rating);

			Role rankRole = roleName.get(rank);

			if (roles.contains(rankRole)) {
				return;
			}

			System.out.println("Check user role " + user + " " + roleName.get(rank));

			for (String str : roleNameArray) {
				Role role = server.getRolesByName(str).get(0);
				if (roles.contains(role)) {
					serverUpdater.removeRoleFromUser(user, role);
				}
			}

			serverUpdater.addRoleToUser(user, roleName.get(rank));
			serverUpdater.update().get();

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		accountList = new ArrayList<>();
		for(Entry<String, String> entry : DataBase.UIDToAccount.entrySet()){
			String account = entry.getValue();
			if(account.equals("######")){
				continue;
			}
			accountList.add(account);
		}
		String account = accountList.get(index);
		UserStatus userStatus = new UserStatus(account);
		DateTime dateTime = DateTime.now();
		int year = dateTime.getYear();
		int month = dateTime.getMonthOfYear();
		List<CodeForcesSubmissionData> list = userStatus.getMonthSolvedList(year, month);
		DataBase.monthSolveRecord.put(account, list.size());
		System.out.println("Catch " + account + "'s solve count in this month: " + list.size());
		make(account);
		index += 1;
		index %= accountList.size();
	}
}
