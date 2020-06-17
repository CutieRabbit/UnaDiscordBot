package sigtuna.discord.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

public class AddReactionEvent implements ReactionAddListener{

	public String getEmojiName(String mentionTag) {
		int leftIndex = 0, rightIndex = 0;
		for(int i = 0; i < mentionTag.length(); i++) {
			if(leftIndex == 0 && mentionTag.charAt(i) == ':') {
				leftIndex = i;
			}else if(mentionTag.charAt(i) == ':') {
				rightIndex = i;
			}
		}
		return mentionTag.substring(leftIndex, rightIndex).replaceAll(":", "");
	}
	
	@Override
	public void onReactionAdd(ReactionAddEvent event) {
		
		if(event.getMessage().get().isPrivateMessage()) return;
		
		Collection<KnownCustomEmoji> emojiMentionTag = event.getServer().get().getCustomEmojis();
		Map<String, KnownCustomEmoji> map = new HashMap<String, KnownCustomEmoji>();
		
		for(KnownCustomEmoji kce : emojiMentionTag) {
			String emojiName = getEmojiName(kce.getMentionTag());
			map.put(emojiName, kce);
		}
		
		String addMentionName = getEmojiName(event.getReaction().get().getEmoji().getMentionTag());
		
		if(map.containsKey(addMentionName)) {
			event.getMessage().get().getChannel().asTextChannel().get().sendMessage(map.get(addMentionName).getMentionTag());
		}
		
	}

}
