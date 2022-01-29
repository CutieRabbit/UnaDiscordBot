package sigtuna.discord.util;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

public class FuncEmbedBuilder extends EmbedBuilder {
    public FuncEmbedBuilder(User user){
        this.setFooter(user.getName() + " " + user.getMentionTag(), user.getAvatar());
    }
}
