package sigtuna.discord.event;

import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javacord.api.util.NonThrowingAutoCloseable;
import sigtuna.discord.codeforces.DataBase;
import sigtuna.discord.exception.EmbedException;
import sigtuna.discord.main.CodeForces;
import sigtuna.discord.util.ContestData;
import sigtuna.discord.util.FuncEmbedBuilder;

import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeForcesSlashCommandListener implements SlashCommandCreateListener {
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent slashCommandCreateEvent) {
        SlashCommandInteraction slashCommandInteraction = slashCommandCreateEvent.getSlashCommandInteraction();
        ServerChannel channel = slashCommandInteraction.getOptionChannelValueByIndex(0).orElse(null);
        User user = slashCommandInteraction.getUser();
        List<SlashCommandInteractionOption> options = slashCommandInteraction.getArguments();
        CodeForces cf = new CodeForces();

        if(slashCommandInteraction.getCommandName().equalsIgnoreCase("cf")){

            FuncEmbedBuilder responseEmbed;

            // 如果使用者有輸入 handle 的話
            if(options.size() == 1) {

                String cfAccount = options.get(0).getStringValue().orElse(null);
                assert cfAccount != null;

                Pattern nameVerification = Pattern.compile("[0-9A-Za-z\\_\\-\\.]+");
                Matcher matcher = nameVerification.matcher(cfAccount);

                if (!matcher.matches()) {
                    responseEmbed = new EmbedException(user, "Handle 錯誤", "請輸入正確的 Handle").getEmbedBuilder();
                    slashCommandInteraction.createImmediateResponder().addEmbed(responseEmbed).respond();
                    return;
                }

                responseEmbed = cf.getUserEmbed(user, cfAccount);

            }else{

                String userID = user.getIdAsString();

                if (DataBase.UIDToAccount.containsKey(userID)) {
                    String CFAccount = DataBase.UIDToAccount.get(userID);
                    responseEmbed = cf.getUserEmbed(user, CFAccount);
                } else {
                    responseEmbed = new EmbedException(user, "你目前無法使用此功能。", "你必須註冊帳號後，才能夠使用<cf指令速查你的帳號。\n如果你沒有註冊，你只能使用<cf <帳號>來查詢cf帳號。").getEmbedBuilder();
                }

            }

            slashCommandInteraction.createImmediateResponder().addEmbed(responseEmbed).respond();
        }
    }
}
