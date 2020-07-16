package sigtuna.discord.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.NonThrowingAutoCloseable;
import org.joda.time.DateTime;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.codeforces.ProblemSelect;
import sigtuna.discord.codeforces.UserSubmissionDatabase;
import sigtuna.discord.exception.EmbedException;
import sigtuna.discord.function.CFChangeColor;
import sigtuna.discord.main.CodeForces;
import sigtuna.discord.util.ContestData;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class CodeForcesEvent implements MessageCreateListener {

	@Override
	public void onMessageCreate(MessageCreateEvent event) {

		Message message = event.getMessage();
		String command = message.getContent();
		TextChannel channel = message.getChannel();
		MessageAuthor messageAuthor = message.getAuthor();
		String[] array_command = command.split(" ");

		if (message.getAuthor().isYourself())
			return;

		CodeForces cf = new CodeForces();
		Optional<User> userOptional = messageAuthor.asUser();
		User user = null;

		if(userOptional.isPresent()){
			user = userOptional.get();
		}

		if (array_command[0].equals("<cf")) {
			try {
				EmbedBuilder embedBuilder =  null;

				if (array_command.length == 2) {
					String userID = user.getIdAsString();
					embedBuilder = cf.getUserEmbed(userID);
				} else if (array_command.length == 1) {
					String userID = message.getAuthor().getIdAsString();
					if (DataBase.UIDToAccount.containsKey(userID)) {
						String CFAccount = DataBase.UIDToAccount.get(userID);
						embedBuilder = cf.getUserEmbed(CFAccount);
					} else {
						throw new EmbedException(channel, "你目前無法使用此功能。", "你必須註冊帳號後，才能夠使用<cf指令速查你的帳號。\n如果你沒有註冊，你只能使用<cf <帳號>來查詢cf帳號。");
					}
				}

				send(message, embedBuilder);

			}catch (EmbedException e){
				e.print();
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
				int year = 0, month = 0, day = 0;
				boolean rating = false;
				if (array_command.length == 1) {
					if (!DataBase.UIDToAccount.containsKey(userID)) {
						throw new EmbedException(channel, "錯誤", "你必須要註冊帳號才能使用<ac來速查自己的月解題記錄\n如果你沒有註冊帳號，你只能使用<ac <帳號>來查詢「已註冊帳號」的月解題記錄。");
					}
					account = DataBase.UIDToAccount.get(userID).toLowerCase();
				}else if(array_command.length == 2){
					account = array_command[1];
					account = account.toLowerCase();
					if (!UserSubmissionDatabase.dataBaseContain(account)) {
						throw new EmbedException(channel, "錯誤", String.format("帳號 %s 沒有在註冊資料庫中。", account));
					}
				}else if(array_command.length == 4){
					account = array_command[1];
					account = account.toLowerCase();
					year = Integer.parseInt(array_command[2]);
					month = Integer.parseInt(array_command[3]);
					if (!UserSubmissionDatabase.dataBaseContain(account)) {
						throw new EmbedException(channel, "錯誤", String.format("帳號 %s 沒有在註冊資料庫中。", account));
					}
				}else if(array_command.length >= 5){
					if(array_command[4].equals("-day")) {
						account = array_command[1];
						account = account.toLowerCase();
						year = Integer.parseInt(array_command[2]);
						month = Integer.parseInt(array_command[3]);
						day = Integer.parseInt(array_command[5]);
						if (!UserSubmissionDatabase.dataBaseContain(account)) {
							throw new EmbedException(channel, "錯誤", String.format("帳號 %s 沒有在註冊資料庫中。", account));
						}
					}else if(array_command[4].equals("-rating")){
						account = array_command[1];
						account = account.toLowerCase();
						year = Integer.parseInt(array_command[2]);
						month = Integer.parseInt(array_command[3]);
						rating = true;
						if (!UserSubmissionDatabase.dataBaseContain(account)) {
							throw new EmbedException(channel, "錯誤", String.format("帳號 %s 沒有在註冊資料庫中。", account));
						}
					}else{
						throw new EmbedException(channel, "錯誤", String.format("參數錯誤: %s。", array_command[5]));
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
					if(!rating) {
						embedBuilder = UserSubmissionDatabase.getMonthAC(account, year, month);
					}else{
						embedBuilder = UserSubmissionDatabase.getMonthACProblem(account, year, month);
					}
				}else{
					embedBuilder = UserSubmissionDatabase.getDayAC(account, year, month, day);
				}

				send(message, embedBuilder);

			}catch (EmbedException e){
				e.print();
			}
		} else if (array_command[0].equalsIgnoreCase("<cf_changeColor") && array_command.length == 2){
			try {
				String userID = message.getAuthor().getIdAsString();
				if (!DataBase.UIDToAccount.containsKey(userID)) {
					throw new EmbedException(channel, "錯誤", "你必須要註冊帳號才能設定自己的embed顏色。");
				}
				String account = DataBase.UIDToAccount.get(userID).toLowerCase();
				String hex = array_command[1];
				CFChangeColor cfChangeColor = new CFChangeColor();
				cfChangeColor.run(channel, account, hex);
				EmbedBuilder embedBuilder = new EmbedBuilder();
				Color color = cfChangeColor.HEXToRGB(hex);
				embedBuilder.setTitle("你已成功更換你的Embed顏色!");
				embedBuilder.setDescription(String.format("user.embed.color = %s", hex));
				embedBuilder.setColor(color);

				send(message, embedBuilder);

			}catch (EmbedException e){
				e.print();
			}catch (IllegalArgumentException e){

			}
		}

		if(array_command[0].equalsIgnoreCase("<cf_makeProblemSelect")){
			if(!message.getAuthor().isBotOwner()) return;
			int min = Integer.parseInt(array_command[1]);
			int max = Integer.parseInt(array_command[2]);
			ProblemSelect problemSelect = new ProblemSelect();
			EmbedBuilder embedBuilder = problemSelect.getEmbed(min, max);
			send(message, embedBuilder);
		}
	}

	public void send(Message message, EmbedBuilder embed){
		try {
			TextChannel channel = message.getChannel();
			MessageAuthor author = message.getAuthor();
			Optional<User> user = author.asUser();
			if(user.isPresent()) {
				CompletableFuture<Message> messageable = channel.sendMessage(embed);
				Message sendMessage = messageable.get();
				EmbedDeleteReactionEvent.addRemoveEmoji(sendMessage, user.get());
			}
			message.delete();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
