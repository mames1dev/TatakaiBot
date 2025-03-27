package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.Main;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class ReadyMatch extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        if(!e.getUser().getNick().equals("BanchoBot")) {
            return;
        }

        if(!Main.tourney.isPickEnd()) {
            return;
        }

        if(Main.tourney.isMatch()) {
            return;
        }

        if(!e.getMessage().equals("Countdown finished")) {
            return;
        }

        e.getBot().send().message(Main.tourney.getChannel(), "!mp start 10");
        e.getBot().send().message(Main.tourney.getChannel(), "GLHF!");
        Main.tourney.setMatch(true);

        // この後にスケジューラーを追加してマッチを監視する
    }
}
