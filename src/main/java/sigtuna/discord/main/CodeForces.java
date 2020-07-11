package sigtuna.discord.main;

import cfapi.main.CodeForcesUser;
import cfapi.main.NoUserException;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import sigtuna.discord.classes.UserInfo;
import sigtuna.discord.codeforces.UserSubmissionDatabase;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class CodeForces {

	public EmbedBuilder getUserEmbed(String user) {

		EmbedBuilder embed = new EmbedBuilder();

		try {

			CodeForcesUser userData = new CodeForcesUser(user);

			embed.setTitle(user);

			user = user.toLowerCase();
			Map<String, List<String>> solved = UserSubmissionDatabase.solved;
			String solvedCount = "";
			if (solved.containsKey(user)) {
				List<String> solvedTag = solved.get(user);
				solvedCount = String.valueOf(solvedTag.size());
			} else {
				solvedCount = "?";
			}

			URL photo = userData.getTitlePhotoURL();
			String photoURL = photo.toString();
			String rank = userData.getRank();

			embed.setThumbnail(photoURL);
			embed.setDescription(rank);

			embed.setUrl("https://codeforces.com/profile/" + user);
			embed.addInlineField("國家", userData.getCountry());
			embed.addInlineField("城市", userData.getCity());
			embed.addField("代表學校", userData.getOrganization());

			if (!rank.equals("Unrated")) {

				embed.addInlineField("最高分數", userData.getMaxRating() + "");
				embed.addInlineField("最高階級", userData.getMaxRank());
				embed.addField("--------------------------", "--------------------------");
				embed.addInlineField("目前分數", userData.getRating() + "");
				embed.addInlineField("目前階級", userData.getRank());
				embed.addField("--------------------------", "--------------------------");
				embed.addField("解題數量", solvedCount);
			}

			if(UserSubmissionDatabase.userColor.containsKey(user)){
				embed.setColor(UserSubmissionDatabase.userColor.get(user));
			}else{
				embed.setColor(Color.magenta);
			}

		} catch (NoUserException e) {

			embed.setTitle(user);
			embed.setDescription("找不到使用者。");
			embed.setColor(Color.RED);
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return embed;

	}

}
