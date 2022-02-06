package sigtuna.discord.util;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.List;

public class PageFuncEmbedBuilder extends FuncEmbedBuilder {

	int page = 0;
	List<FuncEmbedBuilder> list;

	public PageFuncEmbedBuilder(User user){
		super(user);
		list = new ArrayList<>();
	}

	public void addPage(FuncEmbedBuilder funcEmbedBuilder){
		list.add(funcEmbedBuilder);
	}



}
