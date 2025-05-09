package mames1.net.mamesosu.discord.event;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.discord.Embed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
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

     @Override
    public void onReady(ReadyEvent e) {

        List<Message> messages = new ArrayList<>();
        int MAX_DESIRED = 10000;

        MessageChannel messageChannel = e.getJDA().getGuildById(Main.bot.getGuildID()).getTextChannelById(Main.bot.getPoolChannelID());

         messageChannel.getIterableHistory()
                .forEachAsync(message -> {
                    messages.add(message);
                    return messages.size() < MAX_DESIRED;
                }).thenAccept(_ignored -> {
                    for (Message message : messages) {
                        message.delete().queue();
                    }

                    messageChannel.sendMessageEmbeds(Embed.getPool(Main.pool.getTourneyName()).build()).queue();
                });
    }
}
