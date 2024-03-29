package sigtuna.discord.module;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import sigtuna.discord.main.Main;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Remind extends TimerTask{

	TextChannel channel;
	String userID, thing;
	Date date;
	DiscordApi api;

	public Remind(TextChannel channel, String userID, String thing, Date date, DiscordApi api) {
		this.channel = channel;
		this.userID = userID;
		this.thing = thing;
		this.date = date;
		this.api = api;
	}
	
	public Remind() {
		
	}

	@Override
	public void run() {
		try {
			EmbedBuilder embed = new EmbedBuilder();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd aa hh:mm");
			String timeString = sdf.format(date);
			String mentionTag = api.getUserById(userID).get().getMentionTag();
			embed.setDescription("給" + mentionTag + "的提醒");
			embed.setTitle(thing);
			embed.setFooter(timeString);
			channel.sendMessage(mentionTag);
			channel.sendMessage(embed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void execute(MessageCreateEvent event) {
		
		String content = event.getMessage().getContent();
		String[] contentSplit = content.split(" ");
		Message message = event.getMessage();
		TextChannel channel = event.getChannel();
		
		try {

			int year = toi(contentSplit[1]);
			int month = toi(contentSplit[2]);
			int day = toi(contentSplit[3]);
			int hour = toi(contentSplit[4]);
			int min = toi(contentSplit[5]);
			String thing = contentSplit[6];
			String user = message.getAuthor().getIdAsString();

			DateTimeZone zone = DateTimeZone.getDefault();
			DateTimeZone taipei_zone = DateTimeZone.forID("Asia/Taipei");
			int diff_hour = (taipei_zone.toTimeZone().getRawOffset() - zone.toTimeZone().getRawOffset()) / (1000 * 60 * 60);
			System.out.println(diff_hour);

			DateTime dateTime = new DateTime(year, month, day, hour, min, 0, 0).minusHours(diff_hour);
			long second = dateTime.getMillis();

			if (second - System.currentTimeMillis() < 0) {
				
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("你設定的時間已經逾時，無法提醒");
				channel.sendMessage(embed);

			} else {

				Timer timer = new Timer();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd aa hh:mm");
				EmbedBuilder embed = new EmbedBuilder();
				DateTime currentTime = DateTime.now();
				timer.schedule(new Remind(event.getChannel(), user, thing, dateTime.toDate(), Main.api), dateTime.toDate());
				embed.setTitle("新增提醒");
				embed.setColor(Color.GREEN);
				embed.setDescription(thing);
				embed.addField("提醒時間", sdf.format(dateTime.plusHours(diff_hour).toDate()));
				embed.setFooter(sdf.format(currentTime.plusHours(diff_hour).toDate()));

				channel.sendMessage(embed);
			}

		} catch (Exception e) {
			
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("指令輸入錯誤");
			embed.setDescription("<remind <年> <月> <日> <時> <分> <事件>");
			embed.setColor(Color.red);
			channel.sendMessage(embed);

			 e.printStackTrace();;
			
		}
	}
	
	public int toi(String str) {
		return Integer.parseInt(str);
	}

}
