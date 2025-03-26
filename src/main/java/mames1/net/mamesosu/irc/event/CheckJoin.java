package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.Main;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckJoin extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        if(!e.getMessage().contains("joined in slot")) {
            return;
        }

        if (Main.tourney.isAllPlayerJoined()) {
            return;
        }

        Pattern pattern = Pattern.compile("^(\\S+) joined in slot \\d+ for team (\\S+)\\.");
        Matcher matcher = pattern.matcher(e.getMessage());

        if (matcher.find()) {
            Map<String, String> playerTeam = Main.tourney.getTeamMember();
            String playerName = matcher.group(1);

            if(!playerTeam.containsKey("red") || playerTeam.get("red").isEmpty()) {
                playerTeam.put("red", playerName);
                e.getBot().send().message(Main.tourney.getChannel(), "!mp team red " + playerName);

            } else {
                playerTeam.put("blue", playerName);
                e.getBot().send().message(Main.tourney.getChannel(), "!mp team blue " + playerName);
            }

            Main.tourney.setTeamMember(playerTeam);

            // ボッチデバッグ用
            if(playerTeam.size() == 1 ? Main.ircClient.isDebug() : playerTeam.size() == 2) {
                Main.tourney.setAllPlayerJoined(true);
                e.getBot().send().message(Main.tourney.getChannel(), "TatakaiBotへようこそ!　このマッチは、" + Main.tourney.getTourneyName() + "のプールによって進行されます。");
                e.getBot().send().message(Main.tourney.getChannel(), "!mp close");
            }
        }
    }
}
