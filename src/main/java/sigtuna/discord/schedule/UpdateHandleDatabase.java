package sigtuna.discord.schedule;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import sigtuna.discord.codeforces.DataBase;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TimerTask;

public class UpdateHandleDatabase extends TimerTask {
    @Override
    public void run() {
        try {
            File file = new File("C:\\xampp\\htdocs\\cfhandle\\handle.json");
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.println(format());
            printWriter.close();;
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public String format(){
        Map<String,String> map = DataBase.UIDToAccount;
        JsonObject object = new JsonObject();
        object.addProperty("status", "ok");
        JsonArray array = new JsonArray();
        for(Map.Entry<String, String> entry : map.entrySet()){
            JsonObject subObject = new JsonObject();
            subObject.addProperty("DiscoidUID", entry.getKey());
            subObject.addProperty("CFHandle", entry.getValue());
            array.add(subObject);
        }
        object.add("result", array);
        return object.toString();
    }
}
