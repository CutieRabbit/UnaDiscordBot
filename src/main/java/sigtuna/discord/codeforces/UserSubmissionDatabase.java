package sigtuna.discord.codeforces;

import cfapi.main.CodeForcesSubmissionData;
import org.javacord.api.entity.user.User;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import sigtuna.discord.classes.Pair;
import sigtuna.discord.classes.UserStatus;
import sigtuna.discord.util.FuncEmbedBuilder;

import java.awt.*;
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

    public static FuncEmbedBuilder getMonthAC(User user, String account, int year, int month) {

        UserStatus userStatus = database.get(account);
        List<CodeForcesSubmissionData> submissionDataList = userStatus.getMonthSolvedList(year, month);
        List<Integer> ratingList = userStatus.convertSubmissionListToIntegerRatingList(submissionDataList);

        String data = userStatus.getMonthSolvedData(year, month);
        int total = submissionDataList.size();
        Pair<Object, Object> minAndMax = userStatus.getMaxAndMin(ratingList);
        Object median = userStatus.getMedian(ratingList);
        List<String> mode = userStatus.getMode(ratingList);
        Collections.sort(mode);
        String modeData = mode.size() == 0 ? "－－" : String.join(",", mode);
        int day = userStatus.getMonthDayCount(year, month);
        Color color = userColor.getOrDefault(account, Color.magenta);
        DateTime dateTime = DateTime.now();
        dateTime = dateTime.withZone(DateTimeZone.forID("Asia/Taipei"));

        FuncEmbedBuilder embed = new FuncEmbedBuilder(user);
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
        //embed.setFooter(dateTime.toString("yyyy-MM-dd HH:MM:ss"));
        embed.setColor(color);

        return embed;
    }

    public static FuncEmbedBuilder getDayAC(User user, String account, int year, int month, int day){

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


        FuncEmbedBuilder embedBuilder = new FuncEmbedBuilder(user);
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        //embedBuilder.setTimestampToNow();
        embedBuilder.setColor(color);

        return embedBuilder;
    }

    public static FuncEmbedBuilder getMonthACProblem(User user, String account, int year, int month){

        UserStatus userStatus = database.get(account);
        List<CodeForcesSubmissionData> list = userStatus.getMonthSolvedList(year, month);
        Map<Integer, List<CodeForcesSubmissionData>> map = userStatus.getRatingToProblemMap(list);
        List<Integer> RatingList = new ArrayList<>();
        for(Map.Entry<Integer, List<CodeForcesSubmissionData>> entry : map.entrySet()){
            RatingList.add(entry.getKey());
        }
        Collections.sort(RatingList);
        FuncEmbedBuilder embedBuilder = new FuncEmbedBuilder(user);
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
        //embedBuilder.setTimestampToNow();
        embedBuilder.setColor(color);

        return embedBuilder;
    }

    public static FuncEmbedBuilder getACRank(User user){
       List<Pair<String, Integer>> list = new ArrayList<>();
       for(Map.Entry<String, Integer> entry : DataBase.monthSolveRecord.entrySet()){
           list.add(new Pair<>(entry.getKey(), entry.getValue()));
       }
       list.sort((o1, o2) -> {
           if (o2.value < o1.value) {
               return -1;
           } else {
               return 0;
           }
       });
       FuncEmbedBuilder funcEmbedBuilder = new FuncEmbedBuilder(user);
       funcEmbedBuilder.setTitle("本月解題排名");
       funcEmbedBuilder.setDescription("僅會顯示前15名，若不足15名則以完整顯示為主");
       for(int i = 0; i < Math.min(list.size(), 10); i++){
            funcEmbedBuilder.addField(list.get(i).key, String.valueOf(list.get(i).value) + "題");
       }
       return funcEmbedBuilder;
    }

}
