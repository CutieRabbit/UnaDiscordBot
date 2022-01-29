package sigtuna.discord.codeforces;

import java.io.IOException;

import org.javacord.api.entity.message.Message;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RegisterData {

	Message message;
	String user;
	String cfAccount;
	String randomString;
	long time;

	public RegisterData(String user, String cfAccount, String randomString, Message message, long time) {
		this.user = user;
		this.cfAccount = cfAccount;
		this.randomString = randomString;
		this.message = message;
		this.time = time;
	}

	public boolean verificate_pass() throws IOException {
		String url = String.format("https://codeforces.com/api/user.info?handles=%s", cfAccount);
		Connection conn = Jsoup.connect(url);
		conn = conn.followRedirects(false);
		conn = conn.ignoreContentType(true);

		String json = conn.get().text();
		JsonElement element = new JsonParser().parse(json);
		JsonObject object = element.getAsJsonObject();
		String status = object.get("status").getAsString();
		if (status.equals("OK")) {
			JsonArray result = object.get("result").getAsJsonArray();
			JsonObject userData = result.get(0).getAsJsonObject();
			String firstName = userData.get("firstName").getAsString();
			return firstName.equals(randomString);
		}
		return false;
	}
}