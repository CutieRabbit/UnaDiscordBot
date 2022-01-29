package sigtuna.discord.codeforces;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sigtuna.discord.main.Main;

public class ConnectToDiscord extends TimerTask {

	public static HashMap<String, RegisterData> map = new HashMap<>();

	public void run() {
		for (Entry<String, RegisterData> e : map.entrySet()) {
			RegisterData data = e.getValue();
			Message message = data.message;
			try {
				long now = System.currentTimeMillis() / 1000;
				if (Math.abs(now - data.time) < 60) {
					continue;
				}
				boolean success = data.verificate_pass();
				if (!success) {
					message.getChannel().sendMessage(fail_embed(data.cfAccount, data));
				} else {
					Map<String, Object> userData = new HashMap<>();
					userData.put("CodeForcesAccount", data.cfAccount);
					Main.firestore.collection("user").document(data.user).set(userData);
					message.getChannel().sendMessage(pass_embed(data.cfAccount, data));
				}
				map.remove(e.getKey());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public EmbedBuilder pass_embed(String account, RegisterData d) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Registed! " + account);
		embed.setDescription("註冊成功!");
		return embed;
	}

	public EmbedBuilder fail_embed(String account, RegisterData d) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Wrong Answer :( " + account);
		return embed;
	}

}
