package sigtuna.discord.event;

import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class EmbedDeleteReactionEvent implements ReactionAddListener {

    static Map<String, User> MessageIDToUser = new HashMap<>();

    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        try {
            Optional<Message> messageOptional = event.getMessage();
            Message message = null;
            Reaction reaction = null;
            if (messageOptional.isPresent()) {
                message = messageOptional.get();
            }else{
                return;
            }
            String messageID = message.getIdAsString();
            Optional<Reaction> reactionOptional = event.getReaction();
            if (reactionOptional.isPresent()) {
                reaction = reactionOptional.get();
            }
            Emoji emoji = reaction.getEmoji();
            Optional<String> unicodeEmojiOptional = emoji.asUnicodeEmoji();
            String unicodeEmoji = "";
            if(unicodeEmojiOptional.isPresent()){
                unicodeEmoji = unicodeEmojiOptional.get();
            }
            unicodeEmoji = EmojiParser.parseToAliases(unicodeEmoji);
            System.out.println(unicodeEmoji);
            if (!MessageIDToUser.containsKey(messageID)) {
                return;
            }
            if (!unicodeEmoji.equalsIgnoreCase(":x:")) {
                return;
            }
            User messageAuthorUser = MessageIDToUser.get(messageID);
            Optional<User> clicked_User = event.getUser();
            User clickedUser = clicked_User.get();
            String messageAuthorUserID = messageAuthorUser.getIdAsString();
            String clickUserID = clickedUser.getIdAsString();
            if (clickUserID.equals(messageAuthorUserID) || clickedUser.isBotOwner()) {
//                System.out.println("delele execute");
                message.delete();
                MessageIDToUser.remove(messageID);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void addRemoveEmoji(Message message, User user){
        String emoji = EmojiParser.parseToUnicode(":x:");
        String messageID = message.getIdAsString();
        message.addReaction(emoji);
        MessageIDToUser.put(messageID, user);
    }
}
