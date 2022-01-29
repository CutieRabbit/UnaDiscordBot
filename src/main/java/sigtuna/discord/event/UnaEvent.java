package sigtuna.discord.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class UnaEvent implements MessageCreateListener {

	@Override
	public void onMessageCreate(MessageCreateEvent event) {

		Message message = event.getMessage();
		String content = message.getContent();
		TextChannel channel = event.getChannel();

		String[] array = content.split(" ");
		String connectedMessage = array.length < 1 ? " " : String.join("",Arrays.copyOfRange(array, 1, array.length));
		
		if (message.getAuthor().isYourself())
			return;
		if (!array[0].replaceAll("!", "").equals("<@472408420356456468>"))
			return;

		if (!connectedMessage.equals("")) {
			if (connectedMessage.contains("機率")) {
				int total = 0;
				for (int i = 0; i < connectedMessage.length(); i++) {
					total += (int) connectedMessage.charAt(i);
				}
				channel.sendMessage(total % 100 + "%");
				return;
			} else if (connectedMessage.contains("好不好")) {
				channel.sendMessage((int) (Math.random() * 2) == 0 ? "好" : "不好");
				return;
			} else if (connectedMessage.contains("是不是") || connectedMessage.contains("484")) {
				channel.sendMessage((int) (Math.random() * 2) == 0 ? "是" : "不是");
				return;
			}
		}

		List<String> list = new ArrayList<String>();	
		
		if (array[0].equals("<@472408420356456468>") || array[0].equals("<@!472408420356456468>")) {

			list.clear();

			list.add("你好，我是Una，若有事情請找作者 Xuan#1811。");
			list.add("Bla");
			list.add("@6ya");
			list.add("<shutup");
			list.add("好多事情做不完啊...你問我做什麼事情？沒啦我在挑衣服");
			list.add("一個月薪水十位數，000090000");
			list.add("你覺得黑色裙子與白色襯衫的穿搭比較好，還是白色裙子與粉色襯衫的穿搭比較好？");
			list.add("再tag我我都不用寫程式了");
			list.add("正在以O(logN)的速度和6ya打嘴砲中");
			list.add("AAAAAAAAAAAAA");
			list.add("卡布奇諾好喝嗎？嗯？你說不好喝？");
			list.add("=tex shutup");
			list.add("當我們拿起刀，就是要切蛋糕");
			list.add("風箏會飛是因為逆風\r\n" + 
					"人會成長是因為逆境\r\n" + 
					"我會撞車是因為逆向");
			list.add("天拉拉雨灑灑，下班時間你在哪");
			list.add("你好，我不是Una");
			list.add("求6ya下次國文會及格的機率");
			list.add("郭");
			list.add("???");
			list.add("一時睡覺一時爽，一直睡覺火葬場");
			list.add("叫6ya去刷電學");
			list.add("早安");
			list.add("6ya沒梗想內建句子了，所以才會看到這一句");
			list.add("不要跟我說話吧，我只有" + (list.size() + 1) + "句內建句子。");
			event.getChannel().sendMessage(list.get((int) (Math.random() * list.size())));

		}
		
	}

}
