package mames1.net.mamesosu.discord.event;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.discord.Embed;
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

        e.getMessage().replyEmbeds(Embed.getPool(pool).build()).queue();
    }
}
