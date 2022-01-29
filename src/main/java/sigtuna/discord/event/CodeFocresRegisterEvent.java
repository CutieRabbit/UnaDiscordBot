package sigtuna.discord.event;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;

import cfapi.main.CodeForcesContestData;
import cfapi.main.CodeForcesUser;
import cfapi.main.NoUserException;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import org.joda.time.DateTime;
import sigtuna.discord.classes.RandomStringGenerate;
import sigtuna.discord.codeforces.ConnectToDiscord;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.codeforces.RegisterData;
import sigtuna.discord.exception.CooldownException;
import sigtuna.discord.main.CodeForces;
import sigtuna.discord.main.Main;
import sigtuna.discord.schedule.Contest;
import sigtuna.discord.util.FuncEmbedBuilder;

public class CodeFocresRegisterEvent implements MessageCreateListener {

	@Override
	public void onMessageCreate(MessageCreateEvent e) {

		Message message = e.getMessage();
		String content = message.getContent();
		String[] content_array = content.split(" ");

		if (e.getMessage().getAuthor().isYourself())
			return;

		Optional<User> userOptional = message.getUserAuthor();
		if(!userOptional.isPresent()){
			return;
		}

		User user = userOptional.get();

		if (content_array[0].equals(Main.prefix + "reg")) {

			FuncEmbedBuilder embed = new FuncEmbedBuilder(user);
			embed.setTitle("註冊");
			embed.setDescription("發生未知的錯誤，請通知作者。");

			try {

				String account = content_array[1];
				String userID = user.getIdAsString();

				if (ConnectToDiscord.map.containsKey(userID)) {
					throw new CooldownException();
				}

				String generatedString = RandomStringGenerate.randomString(10);

				long time = System.currentTimeMillis() / 1000;
				ConnectToDiscord.map.put(userID, new RegisterData(userID, account, generatedString, message, time));

				embed.setDescription("請在1分鐘內，將自己的 CodeForces FirstName 改成以下的驗證碼");
				embed.addField("驗證碼", generatedString);
				embed.setColor(Color.orange);

			} catch (CooldownException exception){
				embed.setTitle("註冊");
				embed.setDescription("你已經在註冊的序列中了，若你想要放棄註冊，請等待註冊環節自然失敗。");
				embed.setColor(Color.RED);
			}

			message.getChannel().sendMessage(embed);

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

}
