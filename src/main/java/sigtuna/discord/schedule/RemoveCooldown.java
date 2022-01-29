package sigtuna.discord.schedule;

import sigtuna.discord.classes.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

public class RemoveCooldown extends TimerTask {

    static Set<String> set = new HashSet<>();

    String command, userID;

    public RemoveCooldown(String command, String userID){
        set.add(command + "-" + userID);
        this.command = command;
        this.userID = userID;
    }

    public static boolean isCooldown(String command, String userID){
        return set.contains(command + "-" + userID);
    }

    @Override
    public void run() {
        set.remove(command + "-" + userID);
    }
}
