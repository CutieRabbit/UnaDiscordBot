package sigtuna.debug;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Debug {
    public static void main(String[] args) throws IOException {
        Map<String,String> header = new HashMap<>();
        Map<String,String> cookie = new HashMap<>();
        cookie.put("sessionId", "it0FQ1mCmBvqqAjTJjN0FaPO+Bj91HKWt9qHKqVxqZZWGvkb794CvuygCNxVOGZ8");
        cookie.put("reqFrom", "Portal");
        cookie.put("userid", "109590031");
        cookie.put("userType", "50");
        Connection con = Jsoup.connect("https://aps.ntut.edu.tw/course/tw/courseSID.jsp");
        con = con.headers(header);
        con = con.ignoreContentType(true);
        con = con.maxBodySize(0);
        con = con.cookies(cookie);

        System.out.println(con.get().text());
    }
}