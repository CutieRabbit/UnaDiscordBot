package sigtuna.discord.function;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.ServerUpdater;

import java.util.*;

public class RemoveMessage {
    public void clearMessage(Message message, int count, boolean forAll){
        try {
            Channel channel = message.getChannel();
            Optional<TextChannel> optionalTextChannel = channel.asTextChannel();
            TextChannel textChannel = null;
            if (optionalTextChannel.isPresent()) {
                textChannel = optionalTextChannel.get();
            }
            int cnt = 0;
            try {
                MessageSet set = textChannel.getMessages(200).get();
                if (!forAll) {
                    List<Message> orginal = new ArrayList<>(set);
                    Collections.reverse(orginal);
                    List<Message> list = new ArrayList<>();
                    for (Message msg : orginal) {
                        if (msg.getAuthor().isBotOwner()) {
                            list.add(msg);
                            cnt += 1;
                        }
                        if (cnt == count+1) {
                            break;
                        }
                    }
                    textChannel.bulkDelete(list);
                } else {
                    set = textChannel.getMessages(count).get();
                    cnt = set.size();
                    set.deleteAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("清除訊息成功！");
            embed.setDescription(String.format("清除了 %d 筆訊息", cnt-1));
            textChannel.sendMessage(embed).get().delete();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
