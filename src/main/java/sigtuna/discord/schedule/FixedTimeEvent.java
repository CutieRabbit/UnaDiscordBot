package sigtuna.discord.schedule;

import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.joda.time.DateTime;

import sigtuna.discord.main.Main;
import sigtuna.discord.module.GooglePhoto;

public class FixedTimeEvent extends TimerTask{
	
	public void run(){
		loop();
	}
	
	TextChannel channel = (TextChannel) Main.api.getChannelById(594886407835877387L).get();
	
	public void loop() {
		send(18,0,"晚上6點了，該吃晚餐ㄌ","晚餐",null);
		send(23,0,"晚上11點了，該睡ㄌ",null,"https://doqvf81n9htmm.cloudfront.net/data/crop_article/86449/0122-1.jpg_1140x855.jpg");
		send(0,0,"凌晨ㄌ，簽到！",null,"https://www.moedict.tw/%E7%B0%BD%E5%88%B0.png");
		send(3,0,"凌晨三點了，誰會想要在凌晨的時候吃美味蟹堡阿？",null,"https://truth.bahamut.com.tw/s01/201602/5cbd45eb16e03c6382f96654bf2a570a.JPG");
		send(6,30,"凌晨六點了，各位修仙人士該睡囉！",null,"http://cdn.clm02.com/ezvivi.com/186833/20150107151214797.jpg");
	}

	public void send(int hours,int minutes,String text,String search,String url) {
		DateTime dt = new DateTime();
		if(dt.getHourOfDay() == hours && dt.getMinuteOfHour() == minutes && dt.getSecondOfMinute() == 0) {
			GooglePhoto gp = new GooglePhoto();
			String message = "";
			if(search != null) {
				List<String> list = gp.getData(search);
				Collections.shuffle(list);
				message = list.get(0);
			}
			if(url != null) {
				message = url;
			}
			channel.sendMessage(text);
			EmbedBuilder embed = new EmbedBuilder();
			embed.setImage(message);
			channel.sendMessage(embed);
		}
	}
	
	
	
}
