package sigtuna.discord.event;

import java.awt.Color;
import java.io.IOException;

import cfapi.main.CodeForcesUser;
import cfapi.main.NoUserException;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import sigtuna.discord.codeforces.ConnectToDiscord;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.codeforces.RegisterData;
import sigtuna.discord.exception.CooldownException;
import sigtuna.discord.main.CodeForces;

public class CodeFocresRegisterEvent implements MessageCreateListener {

	@Override
	public void onMessageCreate(MessageCreateEvent e) {

		Message message = e.getMessage();
		String content = message.getContent();
		String[] content_array = content.split(" ");

		if (e.getMessage().getAuthor().isYourself())
			return;

		if (content_array[0].equals("<cf_reg")) {

			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("註冊");
			embed.setDescription("發生未知的錯誤，請通知作者。");

			try {

				String account = content_array[1];
				String user = message.getAuthor().getIdAsString();
				CodeForcesUser userData = new CodeForcesUser(account);

				if (ConnectToDiscord.map.containsKey(user)) {
					throw new CooldownException();
				}

				int rand = (int) (Math.random() * 1000 + 1);
				// int rand = 412;
				if (rand == 307)
					rand = (int) (Math.random() * 1000 + 1);
				long time = System.currentTimeMillis() / 1000;
				ConnectToDiscord.map.put(user, new RegisterData(user, account, rand, message, time));
				embed.setDescription("請在1分鐘內在此題目傳送一次CE，並且在這段時間內不要再次submit任何的程式碼。");
				embed.addField("題目", "https://codeforces.com/problemset/problem/" + rand + "/A");
				embed.addField("注意事項", "請注意，有低機率的機會會抽到無法submit的題目\n屆時請先<cf_regdrop後，再嘗試一次。");
				embed.setColor(Color.YELLOW);

			} catch (NoUserException | IOException exception){
				embed.setTitle("註冊");
				embed.setDescription("使用者不存在。");
				embed.setColor(Color.RED);

			} catch (CooldownException exception){
				embed.setTitle("註冊");
				embed.setDescription("你已經在註冊的序列中了，若你想要放棄註冊，請輸入<cf_regdrop。");
				embed.setColor(Color.RED);
			}

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
			if (DataBase.UIDToAccount.containsKey(account)) {
				String cfa = DataBase.UIDToAccount.get(account);
				CodeForces cf = new CodeForces();
				message.getChannel().sendMessage(cf.getUserEmbed(cfa));
			} else {
				message.getChannel().sendMessage("該使用者尚未註冊。");
			}
		}

		if (content_array[0].equals("<cfdrop")) {
			String user = message.getAuthor().getIdAsString();
			DataBase.UIDToAccount.put(user, "######");
			message.getChannel().sendMessage("drop完成，帳號將會在下一次讀取後解綁");
			DataBase.save();
		}


		
	}

}
