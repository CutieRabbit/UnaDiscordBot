package sigtuna.discord.main;

import org.javacord.api.DiscordApi;

import sigtuna.discord.event.*;

public class EventRegister {
	
	public static void register() {
		
		DiscordApi api = Main.api;
		
		api.addMessageCreateListener(new UnaEvent());
		api.addMessageCreateListener(new CodeForcesEvent());
		api.addMessageCreateListener(new CodeFocresRegisterEvent());
		api.addMessageCreateListener(new PhotoEvent());
		api.addServerJoinListener(new JoinEvent());
		api.addMessageCreateListener(new NormalEvent());
//		api.addReactionAddListener(new AddReactionEvent());
		
	}

}
