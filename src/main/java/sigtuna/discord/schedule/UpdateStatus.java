package sigtuna.discord.schedule;

import cfapi.main.CodeForcesStatus;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.codeforces.UserSubmissionDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class UpdateStatus extends TimerTask {
    static int index = 0;
    @Override
    public void run() {
        try {
            List<String> name = DataBase.name;
            String user = name.get(index);
            int size = name.size();
            make(user, size, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
