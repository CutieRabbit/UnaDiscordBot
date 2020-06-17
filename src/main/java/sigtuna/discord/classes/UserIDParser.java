package sigtuna.discord.classes;

public class UserIDParser {

	public static String parser(String str) {
		return str.replaceAll("<,@,>", "");
	}
}
