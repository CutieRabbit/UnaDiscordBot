package sigtuna.discord.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.javacord.api.entity.message.embed.EmbedBuilder;

import cfapi.main.CodeForcesContest;
import cfapi.main.CodeForcesContestData;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import sigtuna.discord.classes.Pair;



public class ContestData {
	
	public static List<Pair<Long, CodeForcesContestData>> contestList = new ArrayList<>();
	
	public static void refresh() {
		try {
			contestList = new ArrayList<>();
			CodeForcesContest contest = new CodeForcesContest();
			List<CodeForcesContestData> list = contest.getBeforeContest(false);			
			for(CodeForcesContestData data : list) {
				long relative = Math.abs(data.getRelativeTimeSeconds());
				Pair<Long,CodeForcesContestData> pair = new Pair<Long,CodeForcesContestData>(relative,data);	
				contestList.add(pair);
			}			
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static String getTime(long second) {	
		long day = second / 86400;
		second %= 86400;
		long hour = second / 3600;
		second %= 3600;
		long minutes = second / 60;
		second %= 60;		
		String time = "";		
		if(day != 0) {
			time += day + " 天 ";
		}
		if(hour != 0) {
			time += hour + " 小時 ";
		}
		if(minutes != 0) {
			time += minutes + " 分鐘 ";
		}		
		return time;		
	}
	
	public static String getTimeString(long second) {
		second *= 1000;
		DateTime time = new DateTime(second);
		time = time.withZone(DateTimeZone.forOffsetHours(8));
		return time.toString("yyyy-MM-dd HH:mm");
	}
	
	public static EmbedBuilder getEmbed() {
		EmbedBuilder embed = new EmbedBuilder();
		int index = 0;
		Collections.sort(contestList, new Comparator<Pair<Long,CodeForcesContestData>>(){
			@Override
			public int compare(Pair<Long, CodeForcesContestData> arg0, Pair<Long, CodeForcesContestData> arg1) {
				return arg0.key < arg1.key ? -1 : 1;
			}
		});
		for(Pair<Long, CodeForcesContestData> pairs : contestList) {
			if(index == 10) break;
			CodeForcesContestData data = pairs.value;
			long startTime = data.getStartTimeSeconds();
			String contestName = data.getName();
			long duration = data.getDurationSeconds();
			long relative = data.getRelativeTimeSeconds();
			startTime = Math.abs(startTime);
			relative = Math.abs(relative);
			embed.addField(contestName, getTime(relative) + " - " + getTimeString(startTime) + " - " + getTime(duration));
			index++;
		}
		return embed;
	}

}
