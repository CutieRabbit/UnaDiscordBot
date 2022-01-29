package sigtuna.discord.classes;

public class RandomStringGenerate {
	public static String randomString(int length){
		String result = "";
		for(int i = 0; i < length; i++){
			int type = (int)(Math.random() * 3);
			if(type == 0){
				int word = (int)(Math.random() * 26);
				result += (char)('a' + word);
			}else if(type == 1){
				int word = (int)(Math.random() * 26);
				result += (char)('A' + word);
			}else{
				int word = (int)(Math.random() * 10);
				result += (char)('0' + word);
			}
		}
		return result;
	}
}
