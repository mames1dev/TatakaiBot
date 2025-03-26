package mames1.net.mamesosu.irc.event;


import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.osu.UserAccount;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;
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
            String channel = "#mp_" + matchID;
            List<Integer> player = Main.tourney.getPlayers().stream()
                .flatMap(map -> map.values().stream())
                .toList();

            Main.tourney.setMatchID(Integer.parseInt(matchID));
            Main.tourney.setRoomName(matchName);
            Main.tourney.setChannel(channel); // #mpのリンク
            Main.tourney.setPlayerIDList(player);

            Main.ircClient.getBot().send().message(channel, "!mp size 2");
            Main.ircClient.getBot().send().message(channel, "!mp set 2 3");

            for(int i : player) {
                String name = UserAccount.getUserName(String.valueOf(i));
                Main.ircClient.getBot().send().message(channel, "!mp invite " + name);
            }

            /*
            if(Main.ircClient.isDebug()) {
                Main.ircClient.getBot().send().message(channel, "!mp close");
            }*/
        } else {
            System.out.println("Match ID not found");
        }
    }
}
