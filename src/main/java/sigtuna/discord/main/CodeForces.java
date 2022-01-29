package sigtuna.discord.main;

import cfapi.main.CodeForcesUser;
import cfapi.main.NoUserException;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import sigtuna.discord.classes.UserStatus;
import sigtuna.discord.codeforces.UserSubmissionDatabase;
import sigtuna.discord.util.FuncEmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class CodeForces {

	public FuncEmbedBuilder getUserEmbed(User user, String cfAccount) {

		FuncEmbedBuilder embed = new FuncEmbedBuilder(user);

		try {

			CodeForcesUser userData = new CodeForcesUser(cfAccount);

			cfAccount = cfAccount.toLowerCase();
			String solvedCount = "?";

			if (UserSubmissionDatabase.dataBaseContain(cfAccount)) {
				UserStatus userStatus = UserSubmissionDatabase.getUserStatus(cfAccount);
				solvedCount = String.valueOf(userStatus.getUserTotalSolved());
			}

			URL photo = userData.getTitlePhotoURL();
			String photoURL = photo.toString().substring(5);
			String rank = userData.getRank();
			String city = userData.getCity();
			String country = userData.getCountry();
			String organization = userData.getOrganization();
			long maxRating = userData.getMaxRating();
			String maxRank = userData.getMaxRank();
			long rating = userData.getRating();

			checkVaild(photoURL);
			checkVaild(rank);
			checkVaild(country);
			checkVaild(city);
			checkVaild(organization);
			checkVaild(maxRank);
			checkVaild(solvedCount);
			checkVaild(cfAccount);

			embed.setTitle(cfAccount);
			embed.setThumbnail(photoURL);
			embed.setDescription(rank);

			embed.setUrl("https://codeforces.com/profile/" + cfAccount);
			embed.addInlineField("國家", "unknown");
			embed.addInlineField("城市", city);
			embed.addField("代表學校", organization);

			if (!rank.equalsIgnoreCase("Unrated")) {

				embed.addInlineField("最高分數", userData.getMaxRating() + "");
				embed.addInlineField("最高階級", userData.getMaxRank());
				embed.addField("--------------------------", "--------------------------");
				embed.addInlineField("目前分數", userData.getRating() + "");
				embed.addInlineField("目前階級", userData.getRank());
				embed.addField("--------------------------", "--------------------------");
				embed.addField("解題數量", solvedCount);
			}

			embed.setColor(Color.magenta);

		} catch (NoUserException e) {

			embed.setTitle(cfAccount);
			embed.setDescription("找不到使用者。");
			embed.setColor(Color.RED);
			e.printStackTrace();

		} catch (Exception e) {
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
