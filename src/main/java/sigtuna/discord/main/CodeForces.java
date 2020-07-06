package sigtuna.discord.main;

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

import java.util.List;
import java.util.Map;

public class CodeForces {

	public UserInfo getUserData(String user_name) {

		try {

			String url = String.format("https://codeforces.com/api/user.info?handles=%s", user_name);

			Connection conn = Jsoup.connect(url);
			conn = conn.followRedirects(false);
			conn = conn.validateTLSCertificates(false);
			conn = conn.ignoreContentType(true);

			String json = conn.get().text();
			JsonElement element = new JsonParser().parse(json);
			JsonObject object = element.getAsJsonObject();

			String status = object.get("status").getAsString();

			UserInfo userInfo = new UserInfo();

			if (status.equals("OK")) {

				JsonArray array = object.get("result").getAsJsonArray();

				for (int i = 0; i < array.size(); i++) {

					userInfo.name = user_name;

					JsonObject result = array.get(i).getAsJsonObject();
					boolean rated = true;

					JsonElement rank = result.get("rank");
					if (rank == null) {
						rated = false;
						userInfo.rated = false;
					}

					JsonElement city = result.get("city");
					if (city != null) {
						String s_city = result.get("city").getAsString();
						userInfo.city = s_city.equals("") ? "Unknown" : s_city;
					}

					JsonElement country = result.get("country");
					if (country != null) {
						String s_country = result.get("country").getAsString();
						userInfo.country = s_country.equals("") ? "Unknown" : result.get("country").getAsString();
					}

					JsonElement school = result.get("organization");
					if (school != null) {
						String s_school = result.get("organization").getAsString();
						userInfo.school = s_school.equals("") ? "Unknown" : result.get("organization").getAsString();
					}

					JsonElement avater = result.get("titlePhoto");
					if (avater != null) {
						userInfo.photo = result.get("titlePhoto").getAsString();
					}

					if (rated == true) {
						userInfo.max_rating = result.get("maxRating").getAsInt();
						userInfo.rank = result.get("rank").getAsString();
						userInfo.rating = result.get("rating").getAsInt();
						userInfo.max_rank = result.get("maxRank").getAsString();
					}

				}

				return userInfo;

			} else {

				return null;

			}

		} catch (HttpStatusException e) {

			return null;

		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
			
		}

	}

	public EmbedBuilder getUserEmbed(String user) {

		EmbedBuilder embed = new EmbedBuilder();
		UserInfo ui = getUserData(user);

		embed.setTitle(user);

		user = user.toLowerCase();
		Map<String, List<String>> solved = UserSubmissionDatabase.solved;
		String solvedCount = "";
		if(solved.containsKey(user)) {
			List<String> solvedTag = solved.get(user);
			solvedCount = String.valueOf(solvedTag.size());
		}else{
			solvedCount = "?";
		}

		if (ui == null) {

			embed.addField("錯誤", "找不到資料");
			return embed;

		} else {

			embed.setThumbnail("https:" + ui.photo);
			embed.setDescription(ui.rated == true ? ui.rank : "Unrated");
			embed.setUrl("https://codeforces.com/profile/"+user);
			
			if (!ui.country.equals("")) {
				embed.addInlineField("國家", ui.country);
			}
			if (!ui.city.equals("")) {
				embed.addInlineField("城市", ui.city);
			}
			if (!ui.school.equals("")) {
				embed.addField("代表學校", ui.school);
			}

			if (ui.rated == true) {

				embed.addInlineField("最高分數", ui.max_rating + " ");
				embed.addInlineField("最高階級", ui.max_rank + " ");
				embed.addField("--------------------------", "--------------------------");
				embed.addInlineField("目前分數", ui.rating + " ");
				embed.addInlineField("目前階級", ui.rank + " ");
				embed.addField("--------------------------", "--------------------------");
				embed.addField( "解題數量", solvedCount);
			}

		}

		return embed;

	}

}
