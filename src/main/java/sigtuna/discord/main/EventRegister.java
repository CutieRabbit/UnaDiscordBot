package sigtuna.discord.main;

import org.javacord.api.DiscordApi;

import sigtuna.discord.event.*;

public class EventRegister {
	
	public static void register() {
		
		DiscordApi api = Main.api;
		
		api.addMessageCreateListener(new UnaEvent());
		api.addMessageCreateListener(new CodeForcesEvent());
		api.addMessageCreateListener(new CodeFocresRegisterEvent());
		api.addServerJoinListener(new JoinEvent());
		api.addMessageCreateListener(new NormalEvent());
		api.addMessageCreateListener(new HelpEvent());
		api.addReactionAddListener(new EmbedDeleteReactionEvent());
		api.addMessageCreateListener(new AutoAddEmbedRemoveEmojiEvent());
		api.addMessageCreateListener(new WhoTheFuckMentionMeEvent());
		api.addMessageCreateListener(new WeatherEvent());
	}

}
