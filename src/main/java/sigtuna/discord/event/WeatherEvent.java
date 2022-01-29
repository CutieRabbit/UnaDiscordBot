package sigtuna.discord.event;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import sigtuna.discord.main.Main;
import sigtuna.discord.util.CVCatcher;

import java.io.IOException;
import java.util.Optional;

public class WeatherEvent implements MessageCreateListener {
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

        if(content.equalsIgnoreCase(prefix + "CV")){
            message.delete();
            try {
                textChannel.sendMessage(CVCatcher.get(user));
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
