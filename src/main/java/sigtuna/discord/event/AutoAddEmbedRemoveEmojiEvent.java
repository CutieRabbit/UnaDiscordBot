package sigtuna.discord.event;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import sigtuna.discord.main.Main;

import java.util.Optional;

public class AutoAddEmbedRemoveEmojiEvent implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        try {
            Message message = messageCreateEvent.getMessage();
            if (message == null) {
                return;
            }
            MessageAuthor author = message.getAuthor();
            if (!author.isYourself()) {
                return;
            }
            if (message.getEmbeds().size() == 0) {
                return;
            }
            Embed embed = message.getEmbeds().get(0);
            Optional<EmbedFooter> embedFooterOptional = embed.getFooter();
            if (!embedFooterOptional.isPresent()) {
                return;
            }
            EmbedFooter embedFooter = embedFooterOptional.get();
            Optional<String> embedFooterTextOptional = embedFooter.getText();
            if (!embedFooterTextOptional.isPresent()) {
                return;
            }
            String footerText = embedFooterTextOptional.get();
            String userMentionTag = footerText.split(" ")[1];
            String userID = userMentionTag.replaceAll("[<>!@]", "");
            User user = Main.api.getUserById(userID).get();
            EmbedDeleteReactionEvent.addRemoveEmoji(message, user);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
