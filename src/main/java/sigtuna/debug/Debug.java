package sigtuna.debug;

import cfapi.main.CodeForcesUser;
import cfapi.main.NoUserException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import sigtuna.discord.util.BufferedImageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Debug {

    public static void main(String[] args) throws IOException, FontFormatException, NoUserException, URISyntaxException {

        CodeForcesUser codeForcesUser = new CodeForcesUser("Potassium");

        BufferedImage bufferedImage = BufferedImageUtil.userProfileImageDraw(codeForcesUser);

        ImageIO.write(bufferedImage, "png", new File("ARC.png"));

    }
}