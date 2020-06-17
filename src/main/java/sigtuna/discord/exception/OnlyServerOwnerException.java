package sigtuna.discord.exception;

import java.awt.Color;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class OnlyServerOwnerException extends Exception {
private static final long serialVersionUID = 1L;
	
	TextChannel channel;
	String message;
	
	public OnlyServerOwnerException(TextChannel channel, String message) {
		this.channel = channel;
		this.message = message;
	}
	
	public void throwNotice() {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("您無法使用此指令");
		embed.setDescription(message);
		embed.setColor(Color.RED);
		channel.sendMessage(embed);
	}
}
