package sigtuna.discord.schedule;

import cfapi.main.CodeForcesStatus;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.codeforces.UserSubmissionDatabase;

import java.io.IOException;
import java.util.*;

public class UpdateStatus extends TimerTask {

    static int index = 0;
    public static Set<String> updateHandle = new HashSet<String>();

    @Override
    public void run() {
        try {
            List<String> name = makeList();
            String user = name.get(index).toLowerCase();
            int size = name.size();
            make(user, size, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> makeList(){
        List<String> list = new ArrayList<>();
        for(String str : updateHandle){
            list.add(str);
        }
        return list;
    }

    public static void make(String user, int size, boolean handmade) throws IOException {
        List<String> name = DataBase.name;
        System.out.println(String.format("Catch %s's Status (%d/%d)", user, index+1, size));
        CodeForcesStatus status = new CodeForcesStatus(user);
        status.save();
        if(!handmade) {
            index++;
            index %= size;
        }
        UserSubmissionDatabase.load(user);
    }
}
