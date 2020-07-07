package sigtuna.discord.exception;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

public class EmbedException extends Exception {
    EmbedBuilder embedBuilder;
    TextChannel textChannel;
    public EmbedException(TextChannel textChannel, String title, String description){
        embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setColor(Color.red);
        this.textChannel = textChannel;
    }
    public void print(){
        textChannel.sendMessage(embedBuilder);
    }
}
