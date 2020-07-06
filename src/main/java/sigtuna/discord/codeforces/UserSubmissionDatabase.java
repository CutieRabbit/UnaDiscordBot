package sigtuna.discord.codeforces;

import cfapi.main.CodeForcesStatus;
import cfapi.main.CodeForcesSubmissionData;

import java.io.File;
import java.util.*;

public class UserSubmissionDatabase {

    public static Map<String, List<CodeForcesSubmissionData>> map = new HashMap<>();
    public static Map<String, List<String>> solved = new HashMap<>();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
