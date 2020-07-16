package sigtuna.discord.codeforces;

import cfapi.main.CodeForcesProblemData;
import cfapi.main.CodeForcesSubmissionData;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sigtuna.discord.classes.UserStatus;
import sigtuna.discord.main.Main;
import sigtuna.discord.schedule.UpdateStatus;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ProblemSelect {

    List<CodeForcesProblemData> list = Main.problemList;
    List<String> handleList = new ArrayList<>();

    public ProblemSelect(){
        try {
            File file = new File("ProblemJoin.txt");
            if(!file.exists()) file.createNewFile();
            Scanner cin = new Scanner(file);
            while(cin.hasNext()){
                String handle = cin.next();
                File userFile = new File("UserData/" + handle + "/");
                if(userFile.exists()){
                    UserSubmissionDatabase.load(handle);
                }
                handleList.add(handle);
            }
            cin.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        addQuery();
    }
    public void addQuery(){
        UpdateStatus.updateHandle.addAll(handleList);
    }

    public boolean removeProblem(){
        Set<String> problemIDSet = new HashSet<>();
        List<Integer> indexList = new ArrayList<>();
        for(String handle : handleList){
            if(!UserSubmissionDatabase.dataBaseContain(handle)){
                return false;
            }
            UserStatus userStatus = new UserStatus(handle);
            List<CodeForcesSubmissionData> list = userStatus.getAllSubmission();
            for(CodeForcesSubmissionData submissionData : list){
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
        return true;
    }

    public Map<Integer, String> selectProblem(int min, int max){
        Collections.shuffle(list);
        Map<Integer, String> map = new HashMap<>();
        int count = 0;
        for(int i = 0; i < list.size(); i++){
            if(list.get(i) == null) continue;
            CodeForcesProblemData problemData = list.get(i);
            int rating = problemData.getRating();
            String problemID = problemData.getContestID() + problemData.getIndex();
            if(map.containsKey(rating)){
                continue;
            }
            if(rating >= min && rating <= max){
                map.put(rating, problemID);
            }
        }
        return map;
    }

    public EmbedBuilder getEmbed(int min, int max){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        boolean status = removeProblem();
        if(!status){
            embedBuilder.setTitle("再等等");
            embedBuilder.setDescription("還有玩家的Status沒有讀取到。");
            embedBuilder.setColor(Color.RED);
            return embedBuilder;
        }
        embedBuilder.setTitle("題目選擇完ㄌ");
        embedBuilder.setDescription("題目難度：" + min + "~" + max);
        embedBuilder.setColor(Color.magenta);
        Map<Integer, String> map = selectProblem(min, max);
        for(int i = min; i <= max; i += 100){
            embedBuilder.addInlineField("" + i, map.get(i));
        }
        return embedBuilder;
    }
}
