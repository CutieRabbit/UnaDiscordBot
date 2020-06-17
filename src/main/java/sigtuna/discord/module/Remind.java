package sigtuna.discord.module;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import sigtuna.discord.classes.DateFormat;
import sigtuna.discord.exception.DateFormatException;
import sigtuna.discord.main.Main;

public class Remind extends TimerTask{

	TextChannel channel;
	String channelID, userID, thing;
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
			SimpleDateFormat sdf = new SimpleDateFormat();
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

			Calendar cal = Calendar.getInstance();
			new DateFormat(year,month,day,hour,min,0);

			cal.set(year, month - 1, day, hour, min, 0);

			if (cal.getTimeInMillis() - System.currentTimeMillis() < 0) {
				
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("你設定的時間已經逾時，無法提醒");
				channel.sendMessage(embed);
				return;
				
			} else {

				Date date = cal.getTime();
				Timer timer = new Timer();
				SimpleDateFormat sdf = new SimpleDateFormat();
				EmbedBuilder embed = new EmbedBuilder();

				timer.schedule(new Remind(event.getChannel(), user, thing, cal.getTime(), Main.api), date);
				embed.setTitle("新增提醒");
				embed.setColor(Color.GREEN);
				embed.setDescription(thing);
				embed.setFooter(sdf.format(date));

				channel.sendMessage(embed);
			}

		} catch (DateFormatException e) {

			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("時間設定錯誤");
			embed.setDescription(e.getMessage());
			embed.setColor(Color.red);
			channel.sendMessage(embed);

		} catch (Exception e) {
			
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("指令輸入錯誤");
			embed.setDescription("<remind <年> <月> <日> <時> <分> <事件>");
			embed.setColor(Color.red);
			channel.sendMessage(embed);
			
		}
	}
	
	public int toi(String str) {
		return Integer.parseInt(str);
	}

}
