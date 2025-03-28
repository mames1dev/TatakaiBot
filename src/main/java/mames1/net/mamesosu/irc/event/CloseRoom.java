package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.tournament.Tourney;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class CloseRoom extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        if(!Main.tourney.isGameEnd()) {
            return;
        }

        if(!e.getUser().getNick().equals("BanchoBot")) {
            return;
        }

        if(e.getMessage().equals("Countdown finished")) {
            e.getBot().send().message(Main.tourney.getChannel(), "!mp close");

            Main.tourney = new Tourney();
        }
    }
}
