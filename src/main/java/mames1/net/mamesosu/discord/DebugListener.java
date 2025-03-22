package mames1.net.mamesosu.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DebugListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        System.out.println(e.getAuthor().getName() + ": " + e.getMessage().getContentRaw());
    }
}
