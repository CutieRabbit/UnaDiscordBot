package sigtuna.discord.event;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import sigtuna.discord.exception.EmbedException;
import sigtuna.discord.function.RemoveMessage;
import sigtuna.discord.function.ServerInfo;
import sigtuna.discord.main.Main;
import sigtuna.discord.module.Remind;

public class NormalEvent implements MessageCreateListener {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        String content = message.getContent();
        String[] split = content.split(" ");
        String prefix = Main.prefix;

        if(message.getAuthor().isYourself()) return;
        if(message.isPrivateMessage()) return;

        TextChannel textChannel = message.getChannel();

        if(split[0].equals(prefix + "<<") && split.length == 2){
            try {
                MessageAuthor author = message.getAuthor();
                if (author.isBotOwner()) {
                    message.delete();
                    int count = Integer.parseInt(split[1]);
                    RemoveMessage removeMessage = new RemoveMessage();
                    removeMessage.clearMessage(message, count);
                } else {
                    throw new EmbedException(textChannel, "權限不足", "這個功能僅限Bot主人使用");
                }
            }catch (EmbedException e){
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
