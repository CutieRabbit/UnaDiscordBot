package sigtuna.discord.function;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.Collections;
import java.util.Optional;

public class RemoveMessage {
    public void clearMessage(Message message, int count, boolean forAll){
        Channel channel = message.getChannel();
        Optional<TextChannel> optionalTextChannel = channel.asTextChannel();
        TextChannel textChannel = null;
        if(optionalTextChannel.isPresent()){
            textChannel = optionalTextChannel.get();
        }
        int cnt = 0;
        try {
            MessageSet set = textChannel.getMessages(count).get();
            if(forAll == false) {
                for (Message m : set) {
                    if (m.getAuthor().isBotOwner()) {
                        cnt += 1;
                        m.delete();
                    }
                }
            }else{
                cnt = count;
                set.deleteAll();
            }
        }catch (Exception e){
            e.printStackTrace();;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("清除訊息成功！");
        embed.setDescription(String.format("清除了 %d 筆訊息", cnt));
        textChannel.sendMessage(embed);
    }
}
