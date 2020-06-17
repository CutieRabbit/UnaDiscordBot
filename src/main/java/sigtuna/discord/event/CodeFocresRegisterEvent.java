package sigtuna.discord.event;

import java.awt.Color;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import sigtuna.discord.classes.UserIDParser;
import sigtuna.discord.codeforces.ConnectToDiscord;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.codeforces.RegisterData;
import sigtuna.discord.main.CodeForces;
import sigtuna.discord.main.Main;
import sigtuna.discord.schedule.CodeForcesRank;

public class CodeFocresRegisterEvent implements MessageCreateListener {

	@Override
	public void onMessageCreate(MessageCreateEvent e) {

		Message message = e.getMessage();
		String content = message.getContent();
		String[] content_array = content.split(" ");

		if (e.getMessage().getAuthor().isYourself())
			return;

		if (content_array[0].equals("<cf_reg")) {

			String account = content_array[1];
			String user = message.getAuthor().getIdAsString();
			
			CodeForces cf = new CodeForces();
			
			if(cf.getUserData(account) == null) {
				message.getChannel().sendMessage("使用者不存在。");
				return;
			}
			
			if (ConnectToDiscord.map.containsKey(user)) {
				message.getChannel().sendMessage("你已經在註冊的序列中了。");
				return;
			}
			

			int rand = (int) (Math.random() * 1000 + 1);
			// int rand = 412;
			if (rand == 307)
				rand = (int) (Math.random() * 1000 + 1);
			long time = System.currentTimeMillis() / 1000;
			ConnectToDiscord.map.put(user, new RegisterData(user, account, rand, message, time));
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("註冊");
			embed.setDescription("請在1分鐘內在此題目傳送一次CE，並且在這段時間內不要再次submit任何的程式碼。");
			embed.addField("題目", "https://codeforces.com/problemset/problem/" + rand + "/A");
			embed.addField("注意事項", "請注意，有低機率的機會會抽到無法submit的題目\n屆時請先<cf_regdrop後，再嘗試一次。");
			embed.setColor(Color.YELLOW);
			message.getChannel().sendMessage(embed);
		}
		
		if(content_array[0].equals("<cf_regdrop")) {
			
			String user = message.getAuthor().getIdAsString();
			EmbedBuilder embed = new EmbedBuilder();
			
			if(ConnectToDiscord.map.containsKey(user)) {
				ConnectToDiscord.map.remove(user);
				embed.setTitle("註冊");
				embed.setDescription("已取消註冊動作。");
				embed.setColor(Color.GREEN);
			} else {
				embed.setTitle("註冊");
				embed.setDescription("你尚未進行註冊動作，無法取消。");
				embed.setColor(Color.RED);
			}
			
			message.getChannel().sendMessage(embed);
		}

		if (content_array[0].equals("<cf_handle")) {
			String account = content_array[1];
			account = account.replaceAll("[<@!>]", "");
			System.out.println(account);
			if (DataBase.map.containsKey(account)) {
				String cfa = DataBase.map.get(account);
				CodeForces cf = new CodeForces();
				message.getChannel().sendMessage(cf.getUserEmbed(cfa));
			} else {
				message.getChannel().sendMessage("該使用者尚未註冊。");
			}
		}

		if (content_array[0].equals("<cfdrop")) {
			String user = message.getAuthor().getIdAsString();
			DataBase.map.put(user, "######");
			message.getChannel().sendMessage("drop完成，帳號將會在下一次讀取後解綁");
			DataBase.save();
		}

		if (content_array[0].equals("<cf_FRGU")) {
			try {
				String owner = Main.api.getOwner().get().getDiscriminatedName();
				if (message.getAuthor().isBotOwner()) {
					CodeForcesRank cfr = new CodeForcesRank();
					cfr.run();
					message.getChannel().sendMessage("force rating group ok :)");
				} else {
					message.getChannel().sendMessage("僅限於原作者" + owner + "使用。");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		if (content_array[0].equals("<cf_ForceReg")) {
			try {
				String owner = Main.api.getOwner().get().getDiscriminatedName();
				if (message.getAuthor().isBotOwner()) {
					User user = Main.api.getOwner().get();
					String account = "unknown";
					if (content_array.length == 3) {
						user = Main.api.getUserById(content_array[1]).get();
						account = content_array[2];
						DataBase.map.put(UserIDParser.parser(content_array[1]), account);
					} else if (content_array.length == 2) {
						account = content_array[1];
						DataBase.map.put(message.getAuthor().getIdAsString(), account);
					} else {
						message.getChannel().sendMessage("指令輸入錯誤，格式應為<cf_ForceReg [指定使用者] <帳號>，且必須為bot擁有者才能使用");
						return;
					}
					message.getChannel().sendMessage("已設定" + user.getMentionTag() + "的CF帳號為" + account);
					DataBase.save();
				} else {
					message.getChannel().sendMessage("僅限於原作者" + owner + "使用使。");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}

}
