package sigtuna.discord.event;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cfapi.main.CodeForcesUser;
import cfapi.main.NoUserException;
import com.google.cloud.firestore.DocumentSnapshot;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import sigtuna.discord.classes.RandomStringGenerate;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.codeforces.RegisterData;
import sigtuna.discord.main.CodeForces;
import sigtuna.discord.main.Main;
import sigtuna.discord.util.FuncEmbedBuilder;

public class CodeFocresRegisterEvent implements MessageCreateListener {

	public static HashMap<String, RegisterData> map = new HashMap<>();

	@Override
	public void onMessageCreate(MessageCreateEvent e) {

		Message message = e.getMessage();
		String content = message.getContent();
		String[] content_array = content.split(" ");

		if (e.getMessage().getAuthor().isYourself())
			return;

		Optional<User> userOptional = message.getUserAuthor();
		if (!userOptional.isPresent()) {
			return;
		}

		User user = userOptional.get();

		if (content_array[0].equals(Main.prefix + "reg")) {

			try {

				if (content_array.length == 1) {
					throw new ArithmeticException("用法： <reg <handle>");
				}

				String account = content_array[1];
				String userID = user.getIdAsString();

				Pattern nameVerification = Pattern.compile("[0-9A-Za-z\\_\\-\\.]+");
				Matcher matcher = nameVerification.matcher(account);

				if (!matcher.matches()) {
					throw new ArithmeticException("請輸入正確的 Handle");
				}

				new CodeForcesUser(content_array[1]);

				String generatedString = RandomStringGenerate.randomString(10);

				long time = System.currentTimeMillis() / 1000;
				map.put(userID, new RegisterData(userID, account, generatedString, message, time));

				FuncEmbedBuilder embed = new FuncEmbedBuilder(user);
				embed.setTitle("註冊");
				embed.setDescription("Hello " + content_array[1] + "! \n\n 請在1分鐘內，將自己的 CodeForces FirstName 改成以下的驗證碼 \n 輸入完成後，請輸入 <reg_veri 來進行驗證程序。");
				embed.addField("傳送門", "https://codeforces.com/settings/social");
				embed.addField("驗證碼", generatedString);
				embed.setColor(Color.orange);

				message.getChannel().sendMessage(embed);

			} catch (ArithmeticException ex){

				FuncEmbedBuilder embed = new FuncEmbedBuilder(user);
				embed.setTitle("註冊");
				embed.setDescription(ex.getMessage());
				embed.setColor(Color.RED);
				message.getChannel().sendMessage(embed);

			} catch (NoUserException nue){

				FuncEmbedBuilder embed = new FuncEmbedBuilder(user);
				embed.setTitle("註冊");
				embed.setDescription("沒有這個使用者。");
				embed.setColor(Color.RED);
				message.getChannel().sendMessage(embed);

			} catch (IOException ex) {

				FuncEmbedBuilder embed = new FuncEmbedBuilder(user);
				embed.setTitle("註冊");
				embed.setDescription("出現了系統問題，請通知開發者。");
				embed.setColor(Color.RED);
				message.getChannel().sendMessage(embed);

				ex.printStackTrace();
			}

		}else if (content_array[0].equals("<reg_veri")) {

			String userID = user.getIdAsString();

			if(!map.containsKey(userID)){
				FuncEmbedBuilder embed = new FuncEmbedBuilder(user);
				embed.setTitle("註冊");
				embed.setDescription("你沒有任何的註冊請求。");
				embed.setColor(Color.RED);
				message.getChannel().sendMessage(embed);
				map.remove(userID);
			}

			RegisterData data = map.get(userID);

			try {

				long now = System.currentTimeMillis() / 1000;

				if (Math.abs(now - data.time) > 60) {
					FuncEmbedBuilder embed = new FuncEmbedBuilder(user);
					embed.setTitle("註冊");
					embed.setDescription("逾期的驗證。");
					embed.setColor(Color.RED);
					message.getChannel().sendMessage(embed);
					map.remove(userID);
					return;
				}

				boolean success = data.verificate_pass();

				if (!success) {

					message.getChannel().sendMessage(fail_embed(data.cfAccount, user, data));

				} else {

					Map<String, Object> userData = new HashMap<>();
					userData.put("CodeForcesAccount", data.cfAccount);
					Main.firestore.collection("user").document(data.user).set(userData);
					DataBase.UIDToAccount.put(data.user, data.cfAccount);
					message.getChannel().sendMessage(pass_embed(data.cfAccount, user, data));

				}

				map.remove(userID);

			} catch (Exception e1) {
				message.getChannel().sendMessage(fail_embed(data.cfAccount, user, data));
				map.remove(userID);
				e1.printStackTrace();
			}

		}else if (content_array[0].equals("<cf_handle")) {

			String userID = content_array[1];
			userID = userID.replaceAll("[<@!>]", "");

			try{

				DocumentSnapshot documentSnapshot = Main.firestore.collection("user").document(userID).get().get();

				if (documentSnapshot.exists()) {
					String cfa = documentSnapshot.getString("CodeForcesAccount");
					CodeForces cf = new CodeForces();
					message.getChannel().sendMessage(cf.getUserEmbed(user, cfa));
				} else {
					message.getChannel().sendMessage("該使用者尚未註冊。");
				}

			}catch (Exception exception){

				exception.printStackTrace();

			}

		} else {
			return;
		}

		message.delete();
	}

	public EmbedBuilder pass_embed(String account, User user, RegisterData d) {
		FuncEmbedBuilder embed = new FuncEmbedBuilder(user);
		embed.setTitle("Registed! " + account);
		embed.setDescription("註冊成功!");
		embed.setColor(Color.GREEN);
		return embed;
	}

	public EmbedBuilder fail_embed(String account, User user, RegisterData d) {
		FuncEmbedBuilder embed = new FuncEmbedBuilder(user);
		embed.setTitle("註冊失敗 - " + account);
		embed.setDescription("註冊失敗，請確認 handle 是否正確以及有沒有正確更改 FirstName");
		embed.setColor(Color.RED);
		return embed;
	}

}
