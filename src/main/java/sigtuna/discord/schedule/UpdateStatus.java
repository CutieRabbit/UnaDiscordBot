package sigtuna.discord.schedule;

import cfapi.main.CodeForcesStatus;
import org.joda.time.DateTime;
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
            loadPeople();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPeople() throws IOException {
        List<String> name = makeList();
        String user = name.get(index).toLowerCase();
        int size = name.size();
        make(user, size);
    }

    public List<String> makeList(){
        List<String> list = new ArrayList<>();
        for(String str : updateHandle){
            list.add(str);
        }
        return list;
    }

    public static void make(String user, int size) throws IOException {
        List<String> name = DataBase.name;
        //System.out.println(String.format("Catch %s's Status (%d/%d)", user, index+1, size));
        CodeForcesStatus status = new CodeForcesStatus(user);
        status.save();
        UserSubmissionDatabase.load(user);
        index++;
        index %= updateHandle.size();
    }

    public static String getLoadCompleteTime(){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusSeconds(15 * updateHandle.size());
        return dateTime.toString("yyyy/MM/dd HH:mm:ss");
    }

    public static String getLoadCompleteMiutes(){
        int second = 15 * updateHandle.size();
        return String.valueOf((int)Math.ceil(second*1.0/60));
    }
}
