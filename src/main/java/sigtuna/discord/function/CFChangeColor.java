package sigtuna.discord.function;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sigtuna.discord.codeforces.UserSubmissionDatabase;
import sigtuna.discord.exception.EmbedException;

import java.awt.*;

public class CFChangeColor {

    public void run(TextChannel channel, String account, String hex) throws IllegalArgumentException, EmbedException {
        try {
            if (hex.length() == 7 && hex.charAt(0) == '#') {
                Color color = HEXToRGB(hex);
                UserSubmissionDatabase.userColor.put(account, color);
            } else {
                throw new EmbedException(channel, "錯誤", "你必須要註冊帳號才能設定自己的embed顏色。");
            }
        }catch (IllegalArgumentException e){
            throw new EmbedException(channel, "錯誤", "請輸入正確的Hex顏色代碼。");
        }
    }

    public static Color HEXToRGB(String hex){
        int r = Integer.valueOf(hex.substring(1, 3),16);
        int g = Integer.valueOf(hex.substring(3, 5),16);
        int b = Integer.valueOf(hex.substring(5, 7),16);
        return new Color(r,g,b);
    }

}
