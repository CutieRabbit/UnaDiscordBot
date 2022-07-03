package sigtuna.discord.event;

import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageType;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import sigtuna.discord.main.Main;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhoTheFuckMentionMeEvent implements MessageCreateListener {
    public TextChannel findWTFMM_channel(List<ServerChannel> channels){
        for(ServerChannel serverChannel : channels){
            String name = serverChannel.getName();
            if(!serverChannel.asTextChannel().isPresent()) continue;
            TextChannel textChannel = serverChannel.asTextChannel().get();
            if(name.equalsIgnoreCase("WTFMM")){
                return textChannel;
            }
        }
        return null;
    }
    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        /*set WTFMM channel*/
        String prefix = Main.prefix;
        MessageAuthor author = messageCreateEvent.getMessageAuthor();
        Message message = messageCreateEvent.getMessage();

        if(author.isYourself()) return;
        if(author.getIdAsString().equals("348900618087432194")) return;

        Optional<Server> optionalServer = messageCreateEvent.getServer();
        Server server = optionalServer.orElse(null);

        if(server == null) return;

        List<ServerChannel> list = server.getChannels();
        TextChannel WTFMM_channel = findWTFMM_channel(list);

        if(WTFMM_channel == null) return;
        if(!author.asUser().isPresent()) return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Who the fuck mentioned me v2.0");
        String description = "標記者：" + author.asUser().get().getMentionTag() + "\n" + "標記頻道：" + messageCreateEvent.getChannel().asServerChannel().get().getName();
        embedBuilder.setDescription(description);

        embedBuilder.setThumbnail("https://cdn.discordapp.com/emojis/543171569346674701.png?v=1");
        List<User> mentionedUserList = message.getMentionedUsers();
        List<Role> mentionedRolesList = message.getMentionedRoles();
        String mentionedUser = "";
        String mentionedRole = "";
        List<String> alreadyRecordID = new ArrayList<>();
        for(User user : mentionedUserList){
            if(user.getIdAsString().equals("348900618087432194")) continue;
            mentionedUser += user.getMentionTag() + "\n";
        }
        for(Role role : mentionedRolesList){
            if(role.getIdAsString().equals("892441971816276008")) continue;
            mentionedRole += role.getMentionTag() + "\n";
        }

        if(!(mentionedUser.length() > 0 || mentionedRole.length() > 0)) return;

        if(!mentionedUser.equalsIgnoreCase("")){
            embedBuilder.addField("被標記的人：", mentionedUser);
        }
        if(!mentionedRole.equalsIgnoreCase("")){
            embedBuilder.addField("被標記的群組：", mentionedRole);
        }
        if(message.mentionsEveryone()){
            embedBuilder.addField("Everyone", "標記了everyone");
        }

        embedBuilder.setTimestampToNow();
        embedBuilder.setAuthor(author.getName(), message.getLink().toString(), author.getAvatar());
        embedBuilder.setColor(Color.CYAN);
        WTFMM_channel.sendMessage(embedBuilder);
    }
}
