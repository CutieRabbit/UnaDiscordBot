package sigtuna.discord.codeforces;

import cfapi.main.CodeForcesProblemData;
import cfapi.main.CodeForcesStatus;
import cfapi.main.CodeForcesSubmissionData;
import com.ibm.icu.text.Transliterator;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.joda.time.DateTime;
import sigtuna.discord.classes.Pair;
import sigtuna.discord.main.Main;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class UserSubmissionDatabase {

    public static Map<String, List<CodeForcesSubmissionData>> userSubmission = new HashMap<>();
    public static Map<String, List<String>> solved = new HashMap<>();
    public static Map<String, Map<String, List<String>>> acTime = new HashMap<>();
    public static Map<String, Integer> problemRating = new HashMap<>();
    public static Map<String, String> updateTime = new HashMap<>();
    public static Map<String, Color> userColor = new HashMap<>();

    public static void makeProblemRating(){
        List<CodeForcesProblemData> problemList = Main.problemList;
        for(CodeForcesProblemData problemData : problemList){
            String problemID = problemData.getContestID() + problemData.getIndex();
            int rating = problemData.getRating();
            problemRating.put(problemID, rating);
        }
    }

    public static void makeACTime(String name) throws NullPointerException {
        name = name.toLowerCase();
        if (!userSubmission.containsKey(name)) {
            throw new NullPointerException(String.format("沒有該用戶 %s 的資料。", name));
        }
        List<CodeForcesSubmissionData> list = userSubmission.get(name);
        Map<String, List<String>> ACTime = new HashMap<>();
        Collections.reverse(list);
        List<String> tag = new ArrayList<>();
        for (CodeForcesSubmissionData submissionData : list) {
            String verdict = submissionData.getVerdict();
            if (!verdict.equals("OK")) {
                continue;
            }
            String problemID = submissionData.getProblemID();
            if (!tag.contains(problemID)) {
                String time = submissionData.getTime();
                if (!ACTime.containsKey(time)) {
                    ACTime.put(time, new ArrayList<>());
                }
                List<String> temp = ACTime.get(time);
                temp.add(problemID);
                ACTime.put(time, temp);
                tag.add(problemID);
            }
        }
        acTime.put(name, ACTime);
    }

    public static void load(String name) {
        name = name.toLowerCase();
        try {
            File file = new File("UserData/" + name + "/Submission.json");
            DateTime timeDate = new DateTime(file.lastModified());
            String time = timeDate.toString("yyyy-MM-dd HH:mm:ss");
            Scanner cin = new Scanner(file);
            StringBuilder text = new StringBuilder();
            while(cin.hasNextLine()) {
                text.append(cin.nextLine());
            }
            cin.close();
            List<CodeForcesSubmissionData> list = CodeForcesStatus.make(text.toString());
            List<String> solvedList = new ArrayList<>();
            for(CodeForcesSubmissionData data : list){
                String problemID = data.getProblemID();
                String verdict = data.getVerdict();
                if(verdict.equals("OK") && !solvedList.contains(problemID)){
                    solvedList.add(problemID);
                }
            }
            System.out.println("make ACTime: " + name);
            solved.put(name, solvedList);
            updateTime.put(name, time);
            userSubmission.put(name, list);
            makeACTime(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EmbedBuilder makeACData(String account, int year, int month) throws NullPointerException{
        Map<String, List<String>> map = acTime.get(account);
        List<Integer> list = new ArrayList<Integer>();
        List<String> allDaySolved = new ArrayList<>();
        List<Integer> allDaySolvedRating = new ArrayList<>();
        DateTime dateTime = new DateTime(year, month, 1, 0, 0, 0, 0);
        list.add(0);
        while(dateTime.getMonthOfYear() == month){
            String format = dateTime.toString("yyyy-MM-dd");
            if(map.containsKey(format)){
                List<String> daySolved = map.get(format);
                allDaySolved.addAll(daySolved);
                int size = daySolved.size();
                list.add(size);
            }else{
                list.add(0);
            }
            dateTime = dateTime.plusDays(1);
        }
        for(String problemID : allDaySolved){
            if(!problemRating.containsKey(problemID)){
                continue;
            }
            int rating = problemRating.get(problemID);
            allDaySolvedRating.add(rating);
        }

        Pair<Integer, Integer> minAndMax = getMaxAndMin(allDaySolvedRating);
        int median = getMedian(allDaySolvedRating);
        List<Integer> IntegerModeList = getMode(allDaySolvedRating);
        List<String> StringmodeList = new ArrayList<>();

        for(int mode : IntegerModeList){
            String value = String.valueOf(mode);
            StringmodeList.add(value);
        }

        String data = formatACData(year, month, list);
        String modeData = StringmodeList.size() == 0 ? "0" : String.join(",", StringmodeList);
        int total = allDaySolved.size();
        int day = list.size()-1;
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
        embed.setFooter("最後更新時間：" + updateTime.get(account));
        embed.setColor(color);

        return embed;
    }
    public static String formatACData(int year, int month, List<Integer> dayData){
        DateTime dateTime = new DateTime(year,month,1,0,0,0);
        List<List<String>> array = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            List<String> temp = new ArrayList<>();
            for(int j = 0; j < 7; j++){
                temp.add("－－");
            }
            array.add(temp);
        }
        int startWeek = 1;
        int startDay = dateTime.getDayOfWeek();
        int day = 1;
        for(int i = 0; month == dateTime.getMonthOfYear(); i++){
            day = dateTime.getDayOfMonth();
            startDay = dateTime.getDayOfWeek();
            if(startDay == 7) startWeek += 1;
            Transliterator tl = Transliterator.getInstance("Halfwidth-Fullwidth");
            String data = tl.transliterate(String.valueOf(String.format("%02d", dayData.get(day))));
            array.get(startWeek).set(startDay%7, data);
            dateTime = dateTime.plusDays(1);
        }
        String data = "";
        for(int i = 1; i < array.size()-1; i++){
            List<String> list = array.get(i);
            data += "｜" + String.join("｜", list) + "｜" + "\n";
        }
        return data;
    }

    public static EmbedBuilder getUserSolved(String account, int year, int month, int day){
        DateTime dateTime = new DateTime(year, month, day, 0, 0, 0);
        String title = String.format("%s的今日解題記錄(%d/%d/%d)", account, year, month, day);
        String format = dateTime.toString("yyyy-MM-dd");
        String description = "";
        if(!acTime.get(account).containsKey(format)){
            description = "這天沒有解題紀錄。";
        }else{
            List<String> list = acTime.get(account).get(format);
            description = String.join(",", list);
        }
        String footer = "最後更新時間：" + updateTime.get(account);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setFooter(footer);
        embedBuilder.setColor(userColor.getOrDefault(account, Color.magenta));

        return embedBuilder;
    }

    public static Pair<Integer, Integer> getMaxAndMin(List<Integer> list){
        if(list.size() == 0){
            return new Pair<> (0, 0);
        }
        Collections.sort(list);
        int size = list.size();
        int min = list.get(0);
        int max = list.get(size-1);
        Pair<Integer, Integer> pair = new Pair<>(min, max);
        return pair;
    }

    public static int getMedian(List<Integer> list){
        if(list.size() == 0){
            return 0;
        }
        Collections.sort(list);
        int size = list.size();
        if(size % 2 == 0){
            return list.get(size/2);
        }else{
            return (list.get(size/2) + list.get(size/2+1))/2;
        }
    }

    public static List<Integer> getMode(List<Integer> list){
        if(list.size() == 0){
            return new ArrayList<>();
        }
        Map<Integer, Integer> map = new HashMap<>();
        int maxValue = 0;
        for(int i = 0; i < list.size(); i++){
            int value = list.get(i);
            if(!map.containsKey(value)){
                map.put(value, 0);
            }
            int count = map.get(value);
            maxValue = Math.max(maxValue, count+1);
            map.put(value, count+1);
        }
        List<Integer> arrayList = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry : map.entrySet()){
            if(entry.getValue() == maxValue){
                arrayList.add(entry.getKey());
            }
        }
        return arrayList;
    }

}
