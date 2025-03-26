package mames1.net.mamesosu.discord.event;

import mames1.net.mamesosu.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;

public class Pool extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if(e.getMember() == null) {
            return;
        }

        if(e.getMember().getUser().isBot()) {
            return;
        }

        if(!e.getMessage().getContentRaw().equals("!pool")) {
            return;
        }

        List<String> pool = Main.pool.getTourneyName();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("プール一覧");
        eb.setDescription("現在の利用可能なプール一覧です。");

        StringBuilder poolList = new StringBuilder();

        for (String s : pool) {
            poolList.append(s).append("\n");
        }

        eb.addField("プール", poolList.toString(), false);
        eb.setColor(Color.GRAY);

        e.getMessage().replyEmbeds(eb.build()).queue();
    }
}
