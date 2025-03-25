package mames1.net.mamesosu.irc.event;


import mames1.net.mamesosu.Main;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateMatch extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        if(!e.getMessage().contains("Created the tournament match")) {
            return;
        }

        Pattern pattern = Pattern.compile("mp/(\\d+) (.+)");
        Matcher matcher = pattern.matcher(e.getMessage());

        if(matcher.find()) {
            String matchID = matcher.group(1);
            String matchName = matcher.group(2);

            Main.tourney.setMatchID(Integer.parseInt(matchID));
            Main.tourney.setRoomName(matchName);


        }
    }
}
