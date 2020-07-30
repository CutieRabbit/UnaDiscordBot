package sigtuna.discord.codeforces;

import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.permission.RoleBuilder;
import org.javacord.api.entity.permission.RoleUpdater;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.ServerUpdater;
import org.javacord.api.entity.user.User;
import sigtuna.discord.main.Main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.rmi.server.UID;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

public class DataBase {
	
	public static Map<String,String> UIDToAccount = new HashMap<String,String>();

	public static List<String> name = new ArrayList<>();

	public static void load() {
		try {
			Optional<Server> serverOptional = Main.api.getServerById(534366668076613632L);
			Server server = null;
			if(serverOptional.isPresent()){
				server = serverOptional.get();
			}else{
				throw new Exception("Cannot find server!!");
			}
			Collection<User> members = server.getMembers();
			for(User user : members){
				List<Role> roles = user.getRoles(server);
				if(roles.size() == 0) continue;
				for(Role role : roles){
					String name = role.getName();
					if(!name.contains("H=")){
						continue;
					}
					String handle = name.substring(2);
					String userID = user.getIdAsString();
					System.out.println(handle);
					UIDToAccount.put(userID, handle);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void save() {
		try {
			Optional<Server> serverOptional = Main.api.getServerById(534366668076613632L);
			Server server = null;
			if(serverOptional.isPresent()){
				server = serverOptional.get();
			}else{
				throw new Exception("Cannot find server!!");
			}
			ServerUpdater serverUpdater = new ServerUpdater(server);
			for(Entry<String, String> entry : UIDToAccount.entrySet()) {
				String UserID = entry.getKey();
				String handle = entry.getValue();
				List<Role> roles = server.getRolesByName("H=" + handle);
				User user = null;
				Optional<User> userOptional = server.getMemberById(UserID);
				if (userOptional.isPresent()) {
					user = userOptional.get();
				} else {
					continue;
				}
				Role role = null;
				if (roles.size() == 0) {
					RoleBuilder roleBuilder = server.createRoleBuilder();
					roleBuilder.setName("H=" + entry.getValue());
					roleBuilder.setMentionable(false);
					role = roleBuilder.create().get();
				} else {
					role = server.getRolesByName("H=" + handle).get(0);
				}
				serverUpdater.addRoleToUser(user, role);
			}
			serverUpdater.update();
			load();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void drop(String uid){
		try {
			Optional<Server> serverOptional = Main.api.getServerById(534366668076613632L);
			Server server = null;
			if (serverOptional.isPresent()) {
				server = serverOptional.get();
			} else {
				throw new Exception("Cannot find server!!");
			}
			String handle = UIDToAccount.get(uid);
			List<Role> roles = server.getRolesByName("H=" + handle);
			User user = null;
			Optional<User> userOptional = server.getMemberById(uid);
			if (userOptional.isPresent()) {
				user = userOptional.get();
			} else {
				return;
			}
			Role role = null;
			if (roles.size() == 0) {
				RoleBuilder roleBuilder = server.createRoleBuilder();
				roleBuilder.setName("H=" + handle);
				roleBuilder.setMentionable(false);
				role = roleBuilder.create().get();
			} else {
				role = server.getRolesByName("H=" + handle).get(0);
			}
			role.delete();
		}catch (Exception e){
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