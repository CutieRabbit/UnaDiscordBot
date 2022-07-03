package sigtuna.discord.event;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.permission.RoleBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.exception.EmbedException;
import sigtuna.discord.function.RemoveMessage;
import sigtuna.discord.function.ServerInfo;
import sigtuna.discord.main.Main;
import sigtuna.discord.module.Remind;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class NormalEvent implements MessageCreateListener {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        String content = message.getContent();
        String[] split = content.split(" ");
        String prefix = Main.prefix;


        if(message.getAuthor().isYourself()) return;
        if(message.isPrivateMessage()) return;

        User user = null;

        Optional<User> userOptional = message.getUserAuthor();
        if(userOptional.isPresent()){
            user = userOptional.get();
        }

        TextChannel textChannel = message.getChannel();

        if(split[0].equals(prefix + "<<")) {
            try {
                MessageAuthor author = message.getAuthor();
                if (author.isBotOwner()) {
                    message.delete();
                    if (split.length == 2) {
                        int count = Integer.parseInt(split[1]);
                        RemoveMessage removeMessage = new RemoveMessage();
                        removeMessage.clearMessage(message, count, false);
                    } else if (split.length == 3 && split[2].equalsIgnoreCase("--forall")) {
                        int count = Integer.parseInt(split[1]);
                        RemoveMessage removeMessage = new RemoveMessage();
                        removeMessage.clearMessage(message, count, true);
                    } else {
                        textChannel.sendMessage("工三小，腦袋壞掉是不是？");
                    }
                } else {
                    throw new EmbedException(user, textChannel, "權限不足", "這個功能僅限Bot主人使用");
                }
            } catch (EmbedException e) {
                e.print();
            }
        }

        if(split[0].equalsIgnoreCase(prefix + "serverInfo")){
            ServerInfo serverInfo = new ServerInfo();
            EmbedBuilder embedBuilder = serverInfo.getServerInfoEmbed(message);
            TextChannel channel = message.getChannel();
            channel.sendMessage(embedBuilder);
        }

        if(split[0].equalsIgnoreCase(prefix + "remind")){
            Remind remind = new Remind();
            remind.execute(event);
        }

    }
}
