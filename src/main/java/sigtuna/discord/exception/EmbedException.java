package sigtuna.discord.exception;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sigtuna.discord.util.FuncEmbedBuilder;

import java.awt.*;

public class EmbedException extends Exception {
    FuncEmbedBuilder embedBuilder;
    TextChannel textChannel;
    public EmbedException(User user, TextChannel textChannel, String title, String description){
        embedBuilder = new FuncEmbedBuilder(user);
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setColor(Color.red);
        this.textChannel = textChannel;
    }
    public EmbedException(User user, String title, String description){
        embedBuilder = new FuncEmbedBuilder(user);
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setColor(Color.red);
    }
    public FuncEmbedBuilder getEmbedBuilder(){
        return embedBuilder;
    }
    public void print(){
        textChannel.sendMessage(embedBuilder);
    }
}
