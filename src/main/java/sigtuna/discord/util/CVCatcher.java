package sigtuna.discord.util;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class CVCatcher {

    public static EmbedBuilder get(User user) throws InterruptedException, IOException {
        String stringUrl = "https://www.cwb.gov.tw/Data/radar/CV1_1000.png";
        URL url = new URL(stringUrl);
        BufferedImage image = (BufferedImage) ImageIO.read(url);
        EmbedBuilder embedBuilder = new FuncEmbedBuilder(user);
        embedBuilder.setTitle("雷達合成回波圖");
        embedBuilder.setImage(image);
        embedBuilder.setTimestampToNow();
        return embedBuilder;
    }
}
