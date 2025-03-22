package mames1.net.mamesosu.irc;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class DebugListener extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {
        System.out.println(e.getUser().getNick() + ": " + e.getMessage());
    }
}
