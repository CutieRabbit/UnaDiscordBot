package sigtuna.discord.message;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class Messager {

	public static void sendMessage(Message message, String str) {

		message.getChannel().sendMessage(str);

	}

	public static void sendMessage(Message message, EmbedBuilder str) {

		message.getChannel().sendMessage(str);

	}

}
