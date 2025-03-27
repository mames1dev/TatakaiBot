package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.Main;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class DebugClose extends ListenerAdapter {

    // 非常用のマッチ強制終了コマンド
    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        if(!Main.ircClient.isDebug()) {
            return;
        }

        if(e.getMessage().equals("!dclose")) {
            e.getBot().send().message(Main.tourney.getChannel(), "!mp close");
        }
    }
}
