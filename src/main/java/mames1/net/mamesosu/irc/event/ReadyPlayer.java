package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.Main;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class ReadyPlayer extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        if(!e.getUser().getNick().equals("BanchoBot")) {
            return;
        }

        if(Main.tourney.isMatch()) {
            return;
        }

        if(!Main.tourney.isPickEnd()) {
            return;
        }

        if(Main.tourney.isGameEnd()) {
            return;
        }

        if(e.getMessage().equals("All players are ready")) {
            e.getBot().send().message(Main.tourney.getChannel(), "!mp timer cancel");
            e.getBot().send().message(Main.tourney.getChannel(), "全員の準備が完了した為、試合を開始します。");
            e.getBot().send().message(Main.tourney.getChannel(), "!mp start 10");
            e.getBot().send().message(Main.tourney.getChannel(), "GLHF!");
            Main.tourney.setMatch(true);
        }
    }
}
