package sigtuna.discord.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GooglePhoto {
	
	public List<String> getData(String text) {
		String url = "https://www.google.com/search?q="+text+"&source=lnms&tbm=isch";
		try {
			Document doc = Jsoup.connect(url).timeout(10000).validateTLSCertificates(false).get();
			Elements rating = doc.select("div.rg_meta");
			List<String> list = new ArrayList<String>();
			for(Element e : rating) {
				if(e.childNodeSize() > 0) {				
					JsonObject jo = (JsonObject) new JsonParser().parse(e.childNode(0).toString());
					list.add(jo.get("ou").getAsString());				
				}
			}		
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getPhoto(String text) {
		List<String> list = getData(text);
		System.out.println(list.size());
		if(list.size() == 0) {
			return "NO_PHOTO";
		}
		Collections.shuffle(list);
		String url = list.get(0);
		String urlSubString = url.substring(url.length()-4);
		int count = 0;
		while(!urlSubString.equals(".jpg") && !urlSubString.equals(".png") && count < list.size()) {
			Collections.shuffle(list);
			url = list.get(0);
			urlSubString = url.substring(url.length()-4);
			count++;
		}
		/*if(count == list.size()) {
			return "NO_PHOTO";
		}*/
		return url;
	}
	
	public void execute(MessageCreateEvent event) {
		
		String content = event.getMessage().getContent();
		String[] contentSplit = content.split(" ");
		TextChannel channel = event.getChannel();
		
		GooglePhoto googlePhoto = new GooglePhoto();

		String[] arrayKeyword = Arrays.copyOfRange(contentSplit, 1, contentSplit.length);
		String keyword = String.join("+", arrayKeyword);

		String url = googlePhoto.getPhoto(keyword);

		EmbedBuilder embed = new EmbedBuilder();

		if (url.equals("NO_PHOTO")) {
			embed = new EmbedBuilder();
			embed.setTitle("找不到圖");			
			embed.setDescription("的啦");
		} else {
			embed.setImage(url);
		}

		channel.sendMessage(embed);
		
	}
	
}
