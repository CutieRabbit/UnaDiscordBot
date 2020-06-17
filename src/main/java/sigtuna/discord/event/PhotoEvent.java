package sigtuna.discord.event;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import sigtuna.discord.exception.CommandErrorException;
import sigtuna.discord.exception.OnlyServerOwnerException;
import sigtuna.discord.main.Main;
import sigtuna.discord.module.GooglePhoto;

public class PhotoEvent implements MessageCreateListener {

	String prefix = Main.prefix;
	
	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		try {
			Message message = event.getMessage();
			
			if(event.getServer() == null) return;
			
			Server server = event.getServer().get();
			TextChannel channel = message.getChannel();
			String content = message.getContent();
			String[] split = content.split(" ");
			String basePath = "./ServerBase/" + server.getIdAsString() + "/";
			MessageAuthor checkUser = message.getAuthor();
			if(checkUser == null) return;
			if(!checkUser.isUser()) return;
			User user = checkUser.asUser().get();
			if (split[0].equals(prefix + "photo-ban")) {
				if (split.length == 1) {
					throw new CommandErrorException(channel,"指令錯誤，指令長度應大於2");
				}
				if (user != server.getOwner()) {
					throw new OnlyServerOwnerException(channel,"此指令只僅限於伺服器擁有者使用");
				}
				File file = new File(basePath + "/PhotoBan.txt");
				Set<String> list = new HashSet<String>();
				Scanner cin = new Scanner(file);
				while(cin.hasNextLine()) {
					list.add(cin.nextLine());
				}
				list.add(split[1]);
				PrintWriter writer = new PrintWriter(file);
				for(String str : list) {
					writer.println(str);
				}
				cin.close();
				writer.close();
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("<photo-ban");
				embed.setDescription("已在此伺服器禁用圖片搜索詞：" + split[1]);
				channel.sendMessage(embed);
			}
			if (split[0].equals(prefix + "photo")) {
				if (split.length == 1) {
					throw new CommandErrorException(channel,"指令錯誤，指令長度應大於2");
				}
				File file = new File(basePath + "/PhotoBan.txt");
				Scanner cin = new Scanner(file);
				List<String> list = new ArrayList<String>();
				while(cin.hasNextLine()) {
					list.add(cin.nextLine());
				}
				cin.close();
				if(list.contains(split[1])) {
					EmbedBuilder embed = new EmbedBuilder();
					embed.setTitle("登登登，已檢測到危害");
					embed.setDescription("這個圖片搜索詞已在此伺服器河蟹：" + split[1]);
					channel.sendMessage(embed);
					return;
				}
				GooglePhoto photo = new GooglePhoto();
				photo.execute(event);

			}
			if (split[0].equals(prefix + "photo-unban")) {
				if (split.length == 1) {
					throw new CommandErrorException(channel,"指令錯誤，指令長度應大於2");
				}
				if (user != server.getOwner()) {
					throw new OnlyServerOwnerException(channel,"此指令只僅限於伺服器擁有者使用");
				}
				File file = new File(basePath + "/PhotoBan.txt");
				Set<String> list = new HashSet<String>();
				Scanner cin = new Scanner(file);
				while(cin.hasNextLine()) {
					list.add(cin.nextLine());
				}
				list.remove(split[1]);
				PrintWriter writer = new PrintWriter(file);
				for(String str : list) {
					writer.println(str);
				}
				cin.close();
				writer.close();
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("<photo-unban");
				embed.setDescription("已在此伺服器解禁圖片搜索詞：" + split[1]);
				channel.sendMessage(embed);
			}
		} catch (CommandErrorException e) {
			e.throwNotice();
		} catch (OnlyServerOwnerException e) {
			e.throwNotice();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
