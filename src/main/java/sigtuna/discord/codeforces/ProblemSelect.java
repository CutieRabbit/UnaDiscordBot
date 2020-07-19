package sigtuna.discord.codeforces;

import cfapi.main.CodeForcesProblemData;
import cfapi.main.CodeForcesSubmissionData;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sigtuna.discord.classes.UserStatus;
import sigtuna.discord.main.Main;
import sigtuna.discord.schedule.RemoveCooldown;
import sigtuna.discord.schedule.UpdateStatus;

import java.awt.*;
import java.util.List;
import java.util.*;

public class ProblemSelect {

    public static void addHandleSequence(String handleSequence){
        String[] handleArray = handleSequence.split(",");
        for(String handle : handleArray) {
            UpdateStatus.updateHandle.add(handle.toLowerCase());
        }
    }

    public static boolean checkSequenceVaild(String userID, String handleSequence){
        try {
            handleSequence = handleSequence.replaceAll(",", ";");
            Document document = Jsoup.connect("https://codeforces.com/api/user.info?handles=" + handleSequence).ignoreContentType(true).get();
            Timer timer = new Timer();
            timer.schedule(new RemoveCooldown("mpa", userID), 10000);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public List<CodeForcesProblemData> removeProblem(String handleSequence){
        List<CodeForcesProblemData> list = Main.problemList;
        Set<String> problemIDSet = new HashSet<>();
        List<Integer> indexList = new ArrayList<>();
        String[] array = handleSequence.split(",");
        List<String> handleList = new ArrayList<>(Arrays.asList(array));
        for(String handle : handleList){
            System.out.println(handle);
            if(handle.equals("")) continue;
            handle = handle.toLowerCase();
            if(!UserSubmissionDatabase.dataBaseContain(handle)){
                return null;
            }
            UserStatus userStatus = new UserStatus(handle);
            List<CodeForcesSubmissionData> submissionDataList = userStatus.getAllSubmission();
            for(CodeForcesSubmissionData submissionData : submissionDataList){
                problemIDSet.add(submissionData.getProblemID());
            }
        }
        for(int i = 0; i < list.size(); i++){
            if(list.get(i) == null) continue;
            CodeForcesProblemData problemData = list.get(i);
            String problemID = problemData.getContestID() + problemData.getIndex();
            List<String> tagList = problemData.getTagList();
            if(problemIDSet.contains(problemID)){
                indexList.add(i);
            }
            if(tagList.contains("*special")){
                indexList.add(i);
            }
        }
        for(int index : indexList){
            list.set(index, null);
        }
        return list;
    }

    public String selectProblem(List<CodeForcesProblemData> list, int rating, List<String> selected){
        Collections.shuffle(list);
        for (CodeForcesProblemData codeForcesProblemData : list) {
            if (codeForcesProblemData == null) continue;
            int contestID = Integer.parseInt(codeForcesProblemData.getContestID());
            String problemID = contestID + codeForcesProblemData.getIndex();
            if(selected.contains(problemID)) continue;
            if(contestID <= 300) continue;
            if (rating == codeForcesProblemData.getRating()) {
               return problemID;
            }
        }
        return "No Such Problem";
    }

    public EmbedBuilder getEmbed(String sequence, int min, int max, int count){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        List<CodeForcesProblemData> problemDataList = removeProblem(sequence);
        if(problemDataList == null){
            embedBuilder.setTitle("再等等");
            embedBuilder.setDescription("還有玩家的Status沒有讀取到。");
            embedBuilder.addField("最晚完整讀取完成時間：", UpdateStatus.getLoadCompleteTime());
            embedBuilder.setColor(Color.RED);
            return embedBuilder;
        }
        embedBuilder.setTitle("題目選擇完ㄌ");
        embedBuilder.setDescription("題目難度：" + min + "~" + max);
        embedBuilder.setColor(Color.magenta);
        List<String> selected = new ArrayList<>();
        for(int i = 0; i < Math.min(10, count); i++){
            int random = (int) (min + 100 * (int)(Math.random() * ((max-min)/100)));
            String problemTag = selectProblem(problemDataList, random, selected);
            selected.add(problemTag);
            embedBuilder.addInlineField("||" + random + "||",  problemTag);
        }
        return embedBuilder;
    }
}
