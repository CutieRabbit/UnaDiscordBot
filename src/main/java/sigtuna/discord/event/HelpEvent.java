package sigtuna.discord.event;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;

public class HelpEvent implements MessageCreateListener {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {

        Message message = event.getMessage();

        if (message.isPrivateMessage()) return;
        if (!message.getAuthor().isUser()) return;

        String content = message.getContent();
        String[] commandArray = content.split(" ");
        EmbedBuilder embedBuilder = new EmbedBuilder();

        String mentionTag = message.getAuthor().asUser().get().getMentionTag();

        embedBuilder.setTitle("幫助");

        if (commandArray[0].equalsIgnoreCase("<help")) {
            if (commandArray.length == 2) {
                if (commandArray[1].equalsIgnoreCase("-cfn")) {
                    embedBuilder.setColor(Color.magenta);
                    embedBuilder.addField("<cf | <account>", "查詢帳號的的CF資訊，若用戶已經綁定帳號，則可以用<cf來速查自己的資料。\n舉例：<cf Xuan");
                    embedBuilder.addField("<cf_handle <user>", "查詢「用戶」的的CF資訊，僅限於該用戶已經綁定帳號\n舉例：<cf_handle " + mentionTag);
                    embedBuilder.addField("<cfcontest", "查詢CF近期的競賽時間");
                    embedBuilder.addField("<ac | <account> | <year> <month> | <-rating/-day> | <day>", "查詢帳號的解題資料，僅限查詢已經綁定的帳號" +
                            "\n舉例：" +
                            "\n<ac Xuan 可以查詢Xuan的當月AC資料" +
                            "\n<ac Xuan 2020 07 可以查詢Xuan在2020年7月的AC資料" +
                            "\n<ac Xuan 2020 07 -day 11 可以查詢Xuan在2020年7月11的時候解掉了哪些題目" +
                            "\n<ac Xuan 2020 07 -rating 可以查詢Xuan在2020年7月時解掉的題目(以題目rating)分類。");
                    embedBuilder.addField("<cf_changeColor <hex_color>", "更換使用者的embed顏色，僅限已經綁定帳號的使用者使用\n舉例：<cf_changeColor #FFFFFF");
                } else if (commandArray[1].equalsIgnoreCase("-cfr")) {
                    embedBuilder.setColor(Color.magenta);
                    embedBuilder.addField("<cf_reg <account>", "綁定CF帳號至Una的資料庫\n舉例：<cf_reg Xuan");
                    embedBuilder.addField("<cf_regdrop", "放棄當前的註冊程序，僅限目前在註冊程序的使用者使用");
                    embedBuilder.addField("<cfdrop", "解除綁定帳號，僅限於已經綁定帳號的使用者使用");
                } else {
                    embedBuilder.setColor(Color.red);
                    embedBuilder.setDescription("未知的指令：<help " + commandArray[1]);
                }
            } else if (commandArray.length == 1) {
                embedBuilder.setColor(Color.magenta);
                embedBuilder.addField("<help -cfn", "查詢一般的CF指令");
                embedBuilder.addField("<help -cfr", "查詢註冊使用的CF指令");
            }
            message.getChannel().sendMessage(embedBuilder);
        }

    }

}
