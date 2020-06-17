package sigtuna.discord.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import sigtuna.discord.bus.Bus;

public class BusEvent implements MessageCreateListener {

	@Override
	public void onMessageCreate(MessageCreateEvent event) {

		String content = event.getMessage().getContent();
		String[] contentSplit = content.split(" ");
		TextChannel channel = event.getChannel();

		if (contentSplit[0].equals("<routeStop")) {
			try {
				String location = contentSplit[1];
				String routeID = contentSplit[2];
				int routeNumber = Integer.parseInt(contentSplit[3]);
				Bus bus = new Bus();
				bus.refreshBusRoute(location, routeID, true);
				List<EmbedBuilder> list = new ArrayList<EmbedBuilder>();
				list.addAll(bus.getRouteStopListEmbed(location, routeID, routeNumber));
				for(int i = 0; i < list.size(); i++) {
					channel.sendMessage(list.get(i));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (contentSplit[0].equals("<routeList")) {
			try {
				String commanad = contentSplit[1];
				if(commanad.equals("strict")) {
					String location = contentSplit[2];
					String routeID = contentSplit[3];
					Bus bus = new Bus();
					bus.refreshBusRoute(location, routeID, true);
					channel.sendMessage(bus.getRouteListEmbed(location, routeID));
				}else {
					String location = contentSplit[1];
					String routeID = contentSplit[2];
					Bus bus = new Bus();
					bus.refreshBusRoute(location, routeID, false);
					channel.sendMessage(bus.getRouteListEmbed(location, routeID));
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
