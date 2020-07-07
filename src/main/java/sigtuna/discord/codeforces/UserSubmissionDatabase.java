package sigtuna.discord.codeforces;

import cfapi.main.CodeForcesProblemData;
import cfapi.main.CodeForcesStatus;
import cfapi.main.CodeForcesSubmissionData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import sigtuna.discord.main.CodeForces;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class UserSubmissionDatabase {

    public static Map<String, List<CodeForcesSubmissionData>> map = new HashMap<>();
    public static Map<String, List<String>> solved = new HashMap<>();
    public static Map<String, Map<String, List<String>>> acTime = new HashMap<>();

    public static void makeACTime(String name){
        List<CodeForcesSubmissionData> list = map.get(name);
        Map<String, List<String>> ACTime = new HashMap<>();
        Collections.reverse(list);
        List<String> tag = new ArrayList<>();
        for(CodeForcesSubmissionData cfpd : list){
            String problemID = cfpd.getProblemID();
            if(!tag.contains(problemID)){
                String time = cfpd.getTime();
                if(!ACTime.containsKey(time)){
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

    public static String getACTimeJson(String name){
        Map<String, List<String>> map = acTime.get(name);
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();
        for(Map.Entry<String, List<String>> entry : map.entrySet()){
            JsonObject subObject = new JsonObject();
            JsonArray subArray = new JsonArray();
            for(String problemID : entry.getValue()){
                subArray.add(problemID);
            }
            subObject.addProperty("time", entry.getKey());
            subObject.add("problem", subArray);
            array.add(subObject);
        }
        object.add("data", array);
        return object.toString();
    }

    public static void saveACTime(String name){
        name = name.toLowerCase();
        try{
            File file = new File("UserData/" + name + "/DayToAC.json");
            if(!file.exists()) file.createNewFile();
            PrintWriter printWriter = new PrintWriter(file);
            String data = getACTimeJson(name);
            printWriter.println(data);
            printWriter.close();
        }catch (IOException e){
            e.printStackTrace();
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
            solved.put(name, solvedList);
            map.put(name, list);
            makeACTime(name);
            saveACTime(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
