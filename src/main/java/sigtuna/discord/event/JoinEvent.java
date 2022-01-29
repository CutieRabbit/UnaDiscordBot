package sigtuna.discord.event;

import java.io.File;
import java.io.PrintWriter;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;

import com.google.gson.JsonObject;

public class JoinEvent implements ServerJoinListener {

	@Override
	public void onServerJoin(ServerJoinEvent event) {
		Server server = event.getServer();
		TextChannel textChannel = server.getTextChannels().get(0);
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("謝謝你邀請我進你的伺服器，我的名子叫做Una，請多指教:)");
		embed.setDescription("請輸入<help來取得我的說明書喔！");
		JoinServer(server.getIdAsString(), textChannel.getIdAsString(), server.getName());
		textChannel.sendMessage(embed);
	}

	public void JoinServer(String serverID, String channelID, String serverName) {
		try {
			File baseDir = new File("./ServerBase");
			if (!baseDir.exists()) {
				baseDir.mkdir();
			}
			String basePath = "./ServerBase/" + serverID + "/";
			File dir = new File(basePath);
			if (dir.exists()) {
				dir.delete();
			}
			dir.mkdir();
			File serverConfig = new File(basePath + "/ServerConfig.json");
			if (!serverConfig.exists()) {
				JsonObject object = new JsonObject();
				object.addProperty("CFMention", "unset");
				object.addProperty("CFContestChannel", "unset");
				object.addProperty("ServerName", serverName.replaceAll(" ", "_"));
				object.addProperty("ServerID", serverID);
				object.addProperty("InitTextChannel", channelID);
				serverConfig.createNewFile();
				PrintWriter writer = new PrintWriter(serverConfig);
				writer.println(object.toString());
				writer.close();
			}
			File PhotoBanner = new File(basePath + "/PhotoBan.txt");
			if (!PhotoBanner.exists()) {
				PhotoBanner.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
