package sigtuna.discord.event;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.NonThrowingAutoCloseable;
import org.joda.time.DateTime;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.codeforces.UserSubmissionDatabase;
import sigtuna.discord.exception.EmbedException;
import sigtuna.discord.main.CodeForces;
import sigtuna.discord.main.Main;
import sigtuna.discord.util.ContestData;
import sigtuna.discord.util.FuncEmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

				FuncEmbedBuilder embedBuilder =  null;

				if (array_command.length == 2) {

					String cfAccount = array_command[1];

					Pattern nameVerification = Pattern.compile("[0-9A-Za-z\\_\\-\\.]+");
					Matcher matcher = nameVerification.matcher(cfAccount);

					if (!matcher.matches()) {
						FuncEmbedBuilder embed = new FuncEmbedBuilder(user);
						embed.setTitle("註冊");
						embed.setDescription("請輸入正確的 Handle");
						embed.setColor(Color.RED);
						message.getChannel().sendMessage(embed);
						return;
					}

					embedBuilder = cf.getUserEmbed(user, cfAccount);

				} else if (array_command.length == 1) {
					String userID = message.getAuthor().getIdAsString();
					if (DataBase.UIDToAccount.containsKey(userID)) {
						String CFAccount = DataBase.UIDToAccount.get(userID);
						embedBuilder = cf.getUserEmbed(user, CFAccount);
					} else {
						throw new EmbedException(user, channel, "你目前無法使用此功能。", "你必須註冊帳號後，才能夠使用<cf指令速查你的帳號。\n如果你沒有註冊，你只能使用<cf <帳號>來查詢cf帳號。");
					}
				}

				channel.sendMessage(embedBuilder);

			}catch (EmbedException e){
				e.print();
			}

		} else if (array_command[0].equals("<cfcontest") && array_command.length == 1) {

			NonThrowingAutoCloseable ntac = message.getChannel().typeContinuously();
			ContestData.refresh();
			message.getChannel().sendMessage(ContestData.getEmbed(user));
			ntac.close();

		} else if (array_command[0].equalsIgnoreCase("<ac")){
			try {

				String account = "";

				String userID = message.getAuthor().getIdAsString();
				int year = 0, month = 0, day = 0;
				boolean rating = false;
				if (array_command.length == 1) {
					if (!DataBase.UIDToAccount.containsKey(userID)) {
						throw new EmbedException(user, channel, "錯誤", "你必須要註冊帳號才能使用<ac來速查自己的月解題記錄\n如果你沒有註冊帳號，你只能使用<ac <帳號>來查詢「已註冊帳號」的月解題記錄。");
					}
					account = DataBase.UIDToAccount.get(userID).toLowerCase();
				}else if(array_command.length == 2){
					account = array_command[1];
					account = account.toLowerCase();
				}else if(array_command.length == 4){
					account = array_command[1];
					account = account.toLowerCase();
					year = Integer.parseInt(array_command[2]);
					month = Integer.parseInt(array_command[3]);
				}else if(array_command.length >= 5){
					if(array_command[4].equals("-day")) {
						account = array_command[1];
						account = account.toLowerCase();
						year = Integer.parseInt(array_command[2]);
						month = Integer.parseInt(array_command[3]);
						day = Integer.parseInt(array_command[5]);
					}else if(array_command[4].equals("-rating")){
						account = array_command[1];
						account = account.toLowerCase();
						year = Integer.parseInt(array_command[2]);
						month = Integer.parseInt(array_command[3]);
						rating = true;
					}else{
						throw new EmbedException(user, channel, "錯誤", String.format("參數錯誤: %s。", array_command[5]));
					}
				}
				DateTime dateTime = new DateTime();
				if(year == 0){
					year = dateTime.getYear();
				}
				if(month == 0){
					month = dateTime.getMonthOfYear();
				}

				Pattern nameVerification = Pattern.compile("[0-9A-Za-z\\_\\-\\.]+");
				Matcher matcher = nameVerification.matcher(account);

				if (!matcher.matches()) {
					FuncEmbedBuilder embed = new FuncEmbedBuilder(user);
					embed.setTitle("註冊");
					embed.setDescription("請輸入正確的 Handle");
					embed.setColor(Color.RED);
					message.getChannel().sendMessage(embed);
					return;
				}

				FuncEmbedBuilder embedBuilder;
				UserSubmissionDatabase.load(account);

				if(day == 0){
					if(!rating) {
						embedBuilder = UserSubmissionDatabase.getMonthAC(user, account, year, month);
					}else{
						embedBuilder = UserSubmissionDatabase.getMonthACProblem(user, account, year, month);
					}
				}else{
					embedBuilder = UserSubmissionDatabase.getDayAC(user, account, year, month, day);
				}

				channel.sendMessage(embedBuilder);

			}catch (EmbedException e){
				e.print();
			}
		}else if(array_command[0].equals("<acrank")) {

			FuncEmbedBuilder funcEmbedBuilder = UserSubmissionDatabase.getACRank(user);
			channel.sendMessage(funcEmbedBuilder);

		}else if(array_command[0].equals("<rr")){

			FuncEmbedBuilder funcEmbedBuilder = new FuncEmbedBuilder(user);
			List<Map.Entry<String, Integer>> list = new ArrayList<>(DataBase.UIDtoRating.entrySet());
			list.sort(Map.Entry.comparingByValue());
			Collections.reverse(list);

			funcEmbedBuilder.setColor(Color.pink);
			funcEmbedBuilder.setTitle("Rating 排名");
			funcEmbedBuilder.setDescription("如果沒有看到自己的，單純只是還沒更新 \n 請稍待片刻");

			for(int i = 0; i < Math.min(list.size(), 25); i++){
				try {
					User target = Main.api.getUserById(list.get(i).getKey()).get();
					String handle = DataBase.UIDToAccount.get(list.get(i).getKey());
					String title = String.format("第 %d 名 - %s(%s)", (i+1), handle, target.getDiscriminatedName());
					funcEmbedBuilder.addField(title, list.get(i).getValue().toString());
				}catch (InterruptedException | ExecutionException e){
					e.printStackTrace();
				}
			}

			channel.sendMessage(funcEmbedBuilder);

		} else {
			return;
		}
		message.delete();
	}

}
