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

import sigtuna.discord.classes.UserStatus;
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
			String solvedCount = "?";

			if (UserSubmissionDatabase.dataBaseContain(user)) {
				UserStatus userStatus = UserSubmissionDatabase.getUserStatus(user);
				solvedCount = String.valueOf(userStatus.getUserTotalSolved());
			}

			URL photo = userData.getTitlePhotoURL();
			String photoURL = photo.toString();
			String rank = userData.getRank();
			String city = userData.getCity();
			String organization = userData.getOrganization();
			long maxRating = userData.getMaxRating();
			String maxRank = userData.getMaxRank();
			long rating = userData.getRating();

			checkVaild(photoURL);
			checkVaild(rank);
			checkVaild(city);
			checkVaild(organization);
			checkVaild(maxRank);

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

			embed.setColor(UserSubmissionDatabase.userColor.getOrDefault(user, Color.magenta));

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

	public void checkVaild(String str) throws NullPointerException{
		if(str.equalsIgnoreCase("")){
			throw new NullPointerException();
		}
	}

}
