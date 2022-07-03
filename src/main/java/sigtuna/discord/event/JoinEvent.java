package sigtuna.discord.event;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;

public class JoinEvent implements ServerJoinListener {

	@Override
	public void onServerJoin(ServerJoinEvent event) {
		Server server = event.getServer();
		TextChannel textChannel = server.getTextChannels().get(0);
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("謝謝你邀請我進你的伺服器，我的名子叫做Una，請多指教:)");
		embed.setDescription("請輸入<help來取得我的說明書喔！");
		textChannel.sendMessage(embed);
	}

}
