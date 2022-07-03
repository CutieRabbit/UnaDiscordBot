package sigtuna.discord.util;

import cfapi.main.CodeForcesUser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BufferedImageUtil {

    public static BufferedImage contestImageDraw(String[] dateString, String[] eventString) throws IOException, FontFormatException {

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("TaipeiSansTCBeta-Bold.ttf")));

        URL url = new URL("https://i.imgur.com/2nLayBK.png");
        BufferedImage image = ImageIO.read(url);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.BLACK);

        graphics.setFont(new Font("Taipei Sans TC Beta", Font.BOLD, 28));

        for(int i = 0; i < 7; i++){
            graphics.drawString(dateString[i], 824, 153 + 128 * i);
        }

        for(int i = 0; i < 7; i++){

            if(eventString[i].length() > 50){
                eventString[i] = eventString[i].substring(0, 50) + "...";
            }

            int length = eventString[i].length();
            int fontSize = (int) Math.min((-0.6 * (length-30) + 36), 36);
            int heightOffset = (int) Math.min(Math.max((-0.6 * (length-30)), -5), 0);
            graphics.setFont(new Font("Taipei Sans TC Beta", Font.BOLD, fontSize));
            graphics.drawString(eventString[i], 974, 177 + 128 * i + heightOffset);
        }

        graphics.setFont(new Font("Taipei Sans TC Beta", Font.BOLD, 64));
        graphics.drawString(dateString[0] + "-" + dateString[6], 75, 340);

        return image;
    }
    
    public static BufferedImage userProfileImageDraw(CodeForcesUser codeForcesUser) throws IOException, FontFormatException, URISyntaxException {

        Color firstColor = Color.BLACK;
        Color firstOpacityColor = Color.BLACK;
        Color secondColor = Color.BLACK;
        Color secondOpacityColor = Color.BLACK;

        Map<String, Color> map = new HashMap<>();
        map.put("newbie", new Color(128, 128, 128));
        map.put("pupil", new Color(0, 128, 0));
        map.put("specialist", new Color(3, 168, 158));
        map.put("expert", new Color(0, 0, 255));
        map.put("candidate master", new Color(170, 0, 170));
        map.put("international master", new Color(250, 140, 0));
        map.put("master", new Color(250, 140, 0));
        map.put("international grandmaster", new Color(255, 0, 0));
        map.put("grandmaster", new Color(255, 0, 0));
        map.put("legendary grandmaster", new Color(255, 0, 0));

        if(!codeForcesUser.getRank().equalsIgnoreCase("legendary grandmaster")){
            firstColor = map.get(codeForcesUser.getRank());
        }
        secondColor = map.get(codeForcesUser.getRank());
        firstOpacityColor = new Color(firstColor.getRed(), firstColor.getGreen(), firstColor.getBlue(), 51);
        secondOpacityColor = new Color(secondColor.getRed(), secondColor.getGreen(), secondColor.getBlue(), 51);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("TaipeiSansTCBeta-Bold.ttf")));
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Open 24 Display St.ttf")));
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("IBMPlexMono-Medium.ttf")));

        BufferedImage bufferedImage = new BufferedImage(9000, 4500, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.createGraphics();

        graphics.setColor(new Color(255, 255, 255));
        graphics.fillRect(0, 0, 9000, 4500);

        graphics.setColor(new Color(255, 255, 0, 51));
        int[] triangleX = {0, 0, 500};
        int[] triangleY = {0, 1500, 0};
        graphics.fillPolygon(triangleX, triangleY, 3);

        graphics.setColor(new Color(0, 0, 255, 51));
        triangleX = new int[]{500, 250, 1500};
        triangleY = new int[]{0, 750, 0};
        graphics.fillPolygon(triangleX, triangleY, 3);

        graphics.setColor(new Color(255, 0, 0, 51));
        triangleX = new int[]{880, 2500, 1500};
        triangleY = new int[]{380, 0, 0};
        graphics.fillPolygon(triangleX, triangleY, 3);

        graphics.setColor(firstOpacityColor);
        triangleX = new int[]{9000, 8500, 9000};
        triangleY = new int[]{3500, 3500, 3000};
        graphics.fillPolygon(triangleX, triangleY, 3);

        graphics.setColor(secondOpacityColor);
        triangleX = new int[]{9000, 8500, 7500, 9000};
        triangleY = new int[]{3500, 3500, 4500, 4500};
        graphics.fillPolygon(triangleX, triangleY, 4);

        graphics.setColor(secondColor);
        graphics.setFont(new Font("IBM Plex Mono Medium", Font.BOLD, 280));
        graphics.drawString(codeForcesUser.getRank().substring(1), 440, 550);
        graphics.setColor(firstColor);
        graphics.drawString(codeForcesUser.getRank().substring(0, 1), 270, 550);

        graphics.setColor(secondColor);
        graphics.setFont(new Font("IBM Plex Mono Medium", Font.BOLD, 480));
        graphics.drawString(codeForcesUser.getHandle().substring(1), 520, 1150);
        graphics.setColor(firstColor);
        graphics.drawString(codeForcesUser.getHandle().substring(0, 1), 250, 1150);

        graphics.setColor(new Color(0, 0, 0));
        graphics.setFont(new Font("IBM Plex Mono Medium", Font.PLAIN, 200));
        graphics.drawString(codeForcesUser.getCountry() + ", " + codeForcesUser.getCity(), 3500, 1750);
        graphics.drawString(codeForcesUser.getOrganization(), 3500, 2150);
        graphics.setFont(new Font("Open 24 Display St", Font.PLAIN, 720));
        graphics.setColor(map.get(codeForcesUser.getRank()));
        graphics.drawString(String.valueOf(codeForcesUser.getRating()), 3500, 3250);
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawString("/", 4800, 3250);
        graphics.setColor(map.get(codeForcesUser.getMaxRank()));
        graphics.drawString(String.valueOf(codeForcesUser.getMaxRating()), 5200, 3250);

        graphics.setColor(new Color(0, 0, 0));
        graphics.setFont(new Font("IBM Plex Mono Medium", Font.PLAIN, 200));
        graphics.drawString("The max of rank is " + codeForcesUser.getMaxRank(), 3500, 4050);

        Image image = ImageIO.read(new URL(codeForcesUser.getTitlePhotoURL().toString().substring(5)));
        Image newImage;
        if(image.getWidth(null) <= image.getHeight(null)) {
            newImage = image.getScaledInstance((int) (image.getWidth(null) * (2500.0 / image.getHeight(null))), 2500, Image.SCALE_DEFAULT);
        }else{
            newImage = image.getScaledInstance(2500, (int) (image.getHeight(null) * (2500.0 / image.getWidth(null))), Image.SCALE_DEFAULT);
        }
        int width = newImage.getWidth(null);
        int fixedImageX = (3000 - width) / 2;
        graphics.drawImage(newImage, 250 + fixedImageX, 1550, null);

        return bufferedImage;

    }

}
