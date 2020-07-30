package sigtuna.discord.codeforces;

import cfapi.main.CodeForcesProblemData;
import cfapi.main.CodeForcesStatus;
import cfapi.main.CodeForcesSubmissionData;
import com.ibm.icu.text.Transliterator;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.joda.time.DateTime;
import sigtuna.discord.classes.Pair;
import sigtuna.discord.classes.UserStatus;
import sigtuna.discord.main.Main;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class UserSubmissionDatabase {

    static Map<String, UserStatus> database = new HashMap<>();
    public static Map<String, Color> userColor = new HashMap<>();

    public static void load(String account) {
        account = account.toLowerCase();
        System.out.println("Load User: " + account);
        UserStatus userStatus = new UserStatus(account);
        database.put(account, userStatus);
    }

    public static boolean dataBaseContain(String account){
        return database.containsKey(account);
    }

    public static UserStatus getUserStatus(String account){
        return database.get(account);
    }

    public static EmbedBuilder getMonthAC(String account, int year, int month) {

        UserStatus userStatus = database.get(account);
        List<CodeForcesSubmissionData> submissionDataList = userStatus.getMonthSolvedList(year, month);
        List<Integer> ratingList = userStatus.convertSubmissionListToIntegerRatingList(submissionDataList);

        String data = userStatus.getMonthSolvedData(year, month);
        int total = submissionDataList.size();
        Pair<Integer, Integer> minAndMax = userStatus.getMaxAndMin(ratingList);
        int median = userStatus.getMedian(ratingList);
        List<String> mode = userStatus.getMode(ratingList);
        String modeData = mode.size() == 0 ? "0" : String.join(",", mode);
        int day = userStatus.getMonthDayCount(year, month);
        Color color = userColor.getOrDefault(account, Color.magenta);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(account + String.format("的解題數量(%04d/%02d)", year, month));
        embed.setDescription(data);
        embed.addInlineField("總解題數量", total + "");
        embed.addInlineField("總解題平均", String.format("%.2f", total*1.0/day));
        embed.addField("================", "================");
        embed.addInlineField("解題難度最大值", minAndMax.value + "");
        embed.addInlineField("解題難度最小值",minAndMax.key + "");
        embed.addField("================", "================");
        embed.addInlineField("解題難度中位數", median + "");
        embed.addInlineField("解題難度眾數", modeData);
        embed.setTimestampToNow();
        embed.setColor(color);

        return embed;
    }

    public static EmbedBuilder getDayAC(String account, int year, int month, int day){

        UserStatus userStatus = database.get(account);
        List<CodeForcesSubmissionData> list = userStatus.getDaySolvedList(year, month, day);
        List<String> problemTag = userStatus.getProblemIDList(list);

        String title = String.format("%s的今日解題記錄(%d/%d/%d)", account, year, month, day);
        String description = "";
        Color color = userColor.getOrDefault(account, Color.magenta);

        if(problemTag.size() == 0){
            description = "這天沒有解題紀錄。";
        }else{
            description = String.join(",", problemTag);
        }


        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setTimestampToNow();
        embedBuilder.setColor(color);

        return embedBuilder;
    }

    public static EmbedBuilder getMonthACProblem(String account, int year, int month){

        UserStatus userStatus = database.get(account);
        List<CodeForcesSubmissionData> list = userStatus.getMonthSolvedList(year, month);
        Map<Integer, List<CodeForcesSubmissionData>> map = userStatus.getRatingToProblemMap(list);
        List<Integer> RatingList = new ArrayList<>();
        for(Map.Entry<Integer, List<CodeForcesSubmissionData>> entry : map.entrySet()){
            RatingList.add(entry.getKey());
        }
        Collections.sort(RatingList);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        for(int rating : RatingList){
            String ratingString = "?";
            if(rating != 0){
                ratingString = String.valueOf(rating);
            }
            List<String> problemID = userStatus.getProblemIDList(map.get(rating));
            String data = String.join(",", problemID);
            embedBuilder.addInlineField(ratingString, data);
        }
        String title = String.format("%s的月解題記錄(%d/%02d)", account, year, month);
        String description = String.format("總題數：%d", list.size());
        Color color = userColor.getOrDefault(account, Color.magenta);

        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setTimestampToNow();
        embedBuilder.setColor(color);

        return embedBuilder;
    }

}
