package sigtuna.discord.event;

import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import sigtuna.discord.function.RemoveMessage;
import sigtuna.discord.main.Main;

public class NormalEvent implements MessageCreateListener {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        String content = message.getContent();
        String[] split = content.split(" ");
        String prefix = Main.prefix;

        if(message.getAuthor().isYourself()) return;
        if(!message.getAuthor().isBotOwner()) return;

        if(split[0].equals(prefix + "<<") && split.length == 2){
            message.removeContentAndEmbed();
            int count = Integer.parseInt(split[1]);
            RemoveMessage removeMessage = new RemoveMessage();
            removeMessage.clearMessage(message, count);
        }
    }
}
