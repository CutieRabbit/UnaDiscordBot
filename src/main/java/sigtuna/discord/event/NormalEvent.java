package sigtuna.discord.event;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import sigtuna.discord.function.RemoveMessage;
import sigtuna.discord.function.ServerInfo;
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

        if(split[0].equalsIgnoreCase(prefix + "serverInfo")){
            ServerInfo serverInfo = new ServerInfo();
            EmbedBuilder embedBuilder = serverInfo.getServerInfoEmbed(message);
            TextChannel channel = message.getChannel();
            channel.sendMessage(embedBuilder);
        }
    }
}
