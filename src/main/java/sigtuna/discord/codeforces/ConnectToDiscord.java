package sigtuna.discord.codeforces;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TimerTask;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sigtuna.discord.schedule.UpdateStatus;

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
				boolean succ = data.verificate_pass();
				if (succ == false) {
					message.getChannel().sendMessage(fail_embed(data.cfAccount, data));
					
				} else {
					DataBase.UIDToAccount.put(e.getKey(), data.cfAccount);
					DataBase.save();
					message.getChannel().sendMessage(pass_embed(data.cfAccount, data));
					UpdateStatus.make(data.cfAccount, map.size(),true);
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
		embed.addField("CE檢查", d.ce == true ? "true" : "false");
		embed.addField("ContestID檢查", d.same_contestID == true ? "true" : "false");
		embed.addField("題號檢查", d.same_index == true ? "true" : "false");
		return embed;
	}

	public EmbedBuilder fail_embed(String account, RegisterData d) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Wrong Answer :( " + account);
		embed.setDescription("註冊失敗 :(");
		embed.addField("CE檢查", d.ce == true ? "true" : "false");
		embed.addField("ContestID檢查", d.same_contestID == true ? "true" : "false");
		embed.addField("題號檢查", d.same_index == true ? "true" : "false");
		return embed;
	}

}
