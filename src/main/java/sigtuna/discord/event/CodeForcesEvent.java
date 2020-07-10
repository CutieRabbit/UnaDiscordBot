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
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.NonThrowingAutoCloseable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.joda.time.DateTime;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.codeforces.UserSubmissionDatabase;
import sigtuna.discord.exception.EmbedException;
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
				if (DataBase.UIDToAccount.containsKey(userID)) {
					String CFAccount = DataBase.UIDToAccount.get(userID);
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
		} else if (array_command[0].toLowerCase().equals("<ac")){
			try {
				String account = "";
				String userID = message.getAuthor().getIdAsString();
				TextChannel channel = message.getChannel();
				int year = 0, month = 0, day = 0;
				if (array_command.length == 1) {
					if (!DataBase.UIDToAccount.containsKey(userID)) {
						throw new EmbedException(channel, "錯誤", "你必須要註冊帳號才能使用<ac來速查自己的月解題記錄\n如果你沒有註冊帳號，你只能使用<ac <帳號>來查詢「已註冊帳號」的月解題記錄。");
					}
					account = DataBase.UIDToAccount.get(userID).toLowerCase();
				}else if(array_command.length == 2){
					account = array_command[1];
					account = account.toLowerCase();
					if (!UserSubmissionDatabase.acTime.containsKey(account)) {
						throw new EmbedException(channel, "錯誤", String.format("帳號 %s 沒有在註冊資料庫中。", account));
					}
				}else if(array_command.length == 4){
					account = array_command[1];
					account = account.toLowerCase();
					year = Integer.parseInt(array_command[2]);
					month = Integer.parseInt(array_command[3]);
					if (!UserSubmissionDatabase.acTime.containsKey(account)) {
						throw new EmbedException(channel, "錯誤", String.format("帳號 %s 沒有在註冊資料庫中。", account));
					}
				}else if(array_command.length == 5){
					account = array_command[1];
					account = account.toLowerCase();
					year = Integer.parseInt(array_command[2]);
					month = Integer.parseInt(array_command[3]);
					day = Integer.parseInt(array_command[4]);
					if (!UserSubmissionDatabase.acTime.containsKey(account)) {
						throw new EmbedException(channel, "錯誤", String.format("帳號 %s 沒有在註冊資料庫中。", account));
					}
				}
				DateTime dateTime = new DateTime();
				if(year == 0){
					year = dateTime.getYear();
				}
				if(month == 0){
					month = dateTime.getMonthOfYear();
				}
				EmbedBuilder embedBuilder;
				if(day == 0){
					embedBuilder = UserSubmissionDatabase.makeACData(account, year, month);
				}else{
					embedBuilder = UserSubmissionDatabase.getUserSolved(account, year, month, day);
				}
				channel.sendMessage(embedBuilder);
			}catch (EmbedException e){
				e.print();
			}
		}
	}
}
