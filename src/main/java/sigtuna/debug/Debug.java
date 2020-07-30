package sigtuna.debug;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Debug{
    public static void main(String[] args) throws IOException {
        Timer timer = new Timer();
        timer.schedule(new UpdateClass(), 0, 1000*120);
    }
}
class UpdateClass extends TimerTask{

    @Override
    public void run() {
        //do update;
    }
}