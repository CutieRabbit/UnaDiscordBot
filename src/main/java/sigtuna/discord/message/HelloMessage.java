package sigtuna.discord.message;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class HelloMessage extends Messager {
	
	public static void run(Message message) {	
		
		EmbedBuilder mainEmbed = new EmbedBuilder();	
		mainEmbed.setTitle("Hello!");
		mainEmbed.setDescription("關於Una");
		mainEmbed.addInlineField("星座", "摩羯座");
		mainEmbed.addInlineField("生日", "12/24日");
		mainEmbed.addField("年齡", "16");
		mainEmbed.addField("開發人員", "6ya");
		mainEmbed.addField("邀請我到妳的伺服器? 好阿~", "https://reurl.cc/yQQra");
		mainEmbed.addField("給約嗎", "考慮");
		mainEmbed.setFooter("Author = 6ya");	
		
		EmbedBuilder commandEmbed = new EmbedBuilder();	
		commandEmbed.setTitle("指令&功能面板");
		
		commandEmbed.addField("在CodeCommunity連接你的CF帳號", "<cf_reg <CodeForces暱稱>");
		commandEmbed.addField("在CodeCommunity取消連接你的CF帳號", "<cf_drop");
		commandEmbed.addField("CF競賽查詢", "<cfcontest");
		commandEmbed.addField("查詢某人的CF帳號", "<cf <CodeForces暱稱>");
		commandEmbed.addField("查詢群內某人的CF帳號(對方要有連接)", "<cf_handle @<要查詢的人>");
		commandEmbed.addField("查詢自己的CF帳號(自己要有連接)", "<cf");
		
		commandEmbed.addField("提醒系統", "<remind <月> <日> <時> <分> <提醒事項>");
		commandEmbed.setFooter("Author = 6ya");
		
		sendMessage(message,mainEmbed);
		sendMessage(message,commandEmbed);
	}
}
