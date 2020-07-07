package sigtuna.discord.codeforces;

import cfapi.main.CodeForcesProblemData;
import cfapi.main.CodeForcesStatus;
import cfapi.main.CodeForcesSubmissionData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.icu.text.Transliterator;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.joda.time.DateTime;
import sigtuna.discord.main.CodeForces;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class UserSubmissionDatabase {

    public static Map<String, List<CodeForcesSubmissionData>> map = new HashMap<>();
    public static Map<String, List<String>> solved = new HashMap<>();
    public static Map<String, Map<String, List<String>>> acTime = new HashMap<>();

    public static void makeACTime(String name) throws NullPointerException{
        if(map.containsKey(name)) {
            List<CodeForcesSubmissionData> list = map.get(name);
            Map<String, List<String>> ACTime = new HashMap<>();
            Collections.reverse(list);
            List<String> tag = new ArrayList<>();
            for (CodeForcesSubmissionData cfpd : list) {
                String problemID = cfpd.getProblemID();
                if (!tag.contains(problemID)) {
                    String time = cfpd.getTime();
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
        }else{
            throw new NullPointerException(String.format("沒有該用戶 %s 的資料。", name));
        }
    }

    public static void load(String name) {
        name = name.toLowerCase();
        try {
            File file = new File("UserData/" + name + "/Submission.json");
            Scanner cin = new Scanner(file);
            String text = "";
            while(cin.hasNextLine()) {
                text += cin.nextLine();
            }
            //System.out.println(name + " " + text.length());
            cin.close();
            List<CodeForcesSubmissionData> list = CodeForcesStatus.make(text);
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
            map.put(name, list);
            makeACTime(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EmbedBuilder makeACData(String account, int year, int month) throws NullPointerException{
        Map<String, List<String>> map = acTime.get(account);
        List<Integer> list = new ArrayList<Integer>();
        DateTime dateTime = new DateTime(year, month, 1, 0, 0, 0, 0);
        list.add(0);
        int total = 0;
        int day = 0;
        while(dateTime.getMonthOfYear() == month){
            String format = dateTime.toString("yyyy-MM-dd");
            if(map.containsKey(format)){
                int size = map.get(format).size();
                total += size;
                list.add(size);
            }else{
                list.add(0);
            }
            day++;
            dateTime = dateTime.plusDays(1);
        }
        String data = formatACData(year, month, list);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(account + String.format("的解題數量(%04d/%02d)", year, month));
        embed.setDescription(data);
        embed.addInlineField(String.format("%04d/%02d的總解題數量", year, month), total + "");
        embed.addInlineField(String.format("%04d/%02d的總解題平均", year, month), String.format("%.2f", total*1.0/day));
        embed.setColor(Color.magenta);

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
}
