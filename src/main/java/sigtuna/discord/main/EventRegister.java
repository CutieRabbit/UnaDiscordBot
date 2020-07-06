package sigtuna.discord.main;

import org.javacord.api.DiscordApi;

import sigtuna.discord.event.CodeFocresRegisterEvent;
import sigtuna.discord.event.CodeForcesEvent;
import sigtuna.discord.event.JoinEvent;
import sigtuna.discord.event.PhotoEvent;
import sigtuna.discord.event.UnaEvent;

public class EventRegister {
	
	public static void register() {
		
		DiscordApi api = Main.api;
		
		api.addMessageCreateListener(new UnaEvent());
		api.addMessageCreateListener(new CodeForcesEvent());
		api.addMessageCreateListener(new CodeFocresRegisterEvent());
		api.addMessageCreateListener(new PhotoEvent());
		api.addServerJoinListener(new JoinEvent());
//		api.addReactionAddListener(new AddReactionEvent());
		
	}

}
