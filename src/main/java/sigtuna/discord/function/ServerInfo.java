package sigtuna.discord.function;

import org.javacord.api.entity.Icon;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.joda.time.DateTime;

import java.awt.*;
import java.time.Instant;
import java.util.Optional;

public class ServerInfo {
    public EmbedBuilder getServerInfoEmbed(Message message){
        Optional<Server> optionalServer = message.getServer();
        Server server = null;
        if(optionalServer.isPresent()){
            server = optionalServer.get();
        }
        Optional<Icon> optionalIcon = server.getIcon();
        Icon icon = null;
        if(optionalIcon.isPresent()){
            icon = optionalIcon.get();
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(server.getName());
        embed.setThumbnail(icon);
        embed.addField("伺服器創立者", server.getOwner().get().getNicknameMentionTag());
        embed.addInlineField("創立時間", getCreationTime(server));
        embed.addInlineField("伺服器人數", getServerMemberCount(server));
        embed.setFooter("伺服器ID: " + getServerID(server));
        embed.setColor(Color.magenta);
        return embed;
    }
    public String getServerMemberCount(Server server){
        return String.valueOf(server.getMemberCount());
    }
    public String getServerID(Server server){
        return server.getIdAsString();
    }
    public String getCreationTime(Server server){
        Instant creationTimestamp = server.getCreationTimestamp();
        long time = creationTimestamp.getEpochSecond() * 1000;
        DateTime dateTime = new DateTime(time);
        String format = dateTime.toString("yyyy/MM/dd");
        return format;
    }

}
