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
	int number;
	long time;

	boolean ce = false;
	boolean same_index = false;
	boolean same_contestID = false;

	public RegisterData(String user, String cfacc, int number, Message message, long time) {
		this.user = user;
		this.cfAccount = cfacc;
		this.number = number;
		this.message = message;
		this.time = time;
	}

	public boolean verificate_pass() throws IOException {
		String url = String.format("https://codeforces.com/api/user.status?handle=%s&from=1&count=15", cfAccount);
		Connection conn = Jsoup.connect(url);
		conn = conn.followRedirects(false);
		conn = conn.validateTLSCertificates(false);
		conn = conn.ignoreContentType(true);

		String json = conn.get().text();
		JsonElement element = new JsonParser().parse(json);
		JsonObject object = element.getAsJsonObject();
		String status = object.get("status").getAsString();
		if (status.equals("OK")) {
			JsonArray result = object.get("result").getAsJsonArray();
			for (int i = 0; i < 1; i++) {
				long cts = result.get(i).getAsJsonObject().get("creationTimeSeconds").getAsLong();
				long now = System.currentTimeMillis() / 1000;
				if (Math.abs(cts - now) > 180)
					break;
				JsonObject problem = result.get(i).getAsJsonObject().get("problem").getAsJsonObject();
				String problem_status = result.get(i).getAsJsonObject().get("verdict").getAsString();
				String contest_id = problem.get("contestId").getAsString();
				String problem_id = problem.get("index").getAsString();
				if (problem_status.equals("COMPILATION_ERROR")) {
					ce = true;
				}
				if (contest_id.equals(String.valueOf(number))) {
						same_contestID = true;
				}
				if (problem_id.equals("A")) {
					same_index = true;
				}
				if (ce && same_contestID && same_index) {
					return true;
				} else {
					ce = false;
					same_contestID = false;
					same_index = false;
				}
			}
		}
		return false;
	}
}