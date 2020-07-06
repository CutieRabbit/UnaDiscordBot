package sigtuna.discord.event;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import cfapi.main.CodeForcesProblemData;
import cfapi.main.CodeForcesProblemSet;
import cfapi.main.CodeForcesUser;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.NonThrowingAutoCloseable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.main.CodeForces;
import sigtuna.discord.util.ContestData;

public class CodeForcesEvent implements MessageCreateListener {

	@Override
	public void onMessageCreate(MessageCreateEvent event) {

		Message message = event.getMessage();
		String command = message.getContent();
		String[] array_command = command.split(" ");

		if (message.getAuthor().isYourself())
			return;
		CodeForces cf = new CodeForces();

		if (array_command[0].equals("<cf")) {

			if (array_command.length == 2) {
				String user = array_command[1];
				message.getChannel().sendMessage(cf.getUserEmbed(user));
			} else if (array_command.length == 1) {
				String userID = message.getAuthor().getIdAsString();
				if (DataBase.map.containsKey(userID)) {
					String CFAccount = DataBase.map.get(userID);
					message.getChannel().sendMessage(cf.getUserEmbed(CFAccount));
				} else {
					EmbedBuilder embed = new EmbedBuilder();
					embed.setTitle("你目前無法使用此功能。");
					embed.setDescription("你必須註冊帳號後，才能夠使用<cf指令速查你的帳號。\n如果你沒有註冊，你只能使用<cf <帳號>來查詢cf帳號。");
					embed.setColor(Color.red);
					message.getChannel().sendMessage(embed);
				}
			}

		} else if (array_command[0].equals("<cfcontest") && array_command.length == 1) {

			NonThrowingAutoCloseable ntac = message.getChannel().typeContinuously();
			ContestData.refresh();
			message.getChannel().sendMessage(ContestData.getEmbed());
			ntac.close();

		} else if (array_command[0].equals("<contestplay") && array_command.length == 1) {

			List<Role> roleList = message.getServer().get().getRolesByName("CodeForces");

			if (roleList.size() == 0) {
				System.out.println("你需要一個名為codeforces的身分組");
				return;
			}

			String ID = message.getServer().get().getIdAsString();
			String CFMention = roleList.get(0).getMentionTag();
			String channelID = message.getChannel().getIdAsString();

			try {

				File file = new File("./ServerBase/" + ID + "/ServerConfig.json");
				Scanner cin = new Scanner(file);
				String text = cin.nextLine();
				JsonObject json = new JsonParser().parse(text).getAsJsonObject();
				PrintWriter pw = new PrintWriter(file);

				json.addProperty("CFMention", CFMention);
				json.addProperty("CFContestChannel", channelID);
				pw.println(json.toString());
				pw.close();
				cin.close();

				message.getChannel().sendMessage("ok. 在這推播競賽資訊");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
