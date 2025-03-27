package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.osu.UserAccount;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckJoin extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        if(!e.getUser().getNick().equals("BanchoBot")) {
            return;
        }

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
            playerName = playerName.replace(" ", "_");

            playerTeam.put(matcher.group(2), String.valueOf(UserAccount.getUserID(playerName)));

            Main.tourney.setTeamMember(playerTeam);

            if(playerTeam.size() == 2) {
                Main.tourney.setAllPlayerJoined(true);
                e.getBot().send().message(Main.tourney.getChannel(), "TatakaiBotへようこそ!　このマッチは、" + Main.tourney.getTourneyName() + "のプールによって進行されます。Best of 9で行われます。");
                e.getBot().send().message(Main.tourney.getChannel(), "それではrollをしてください。rollが高かった人が1st Ban 2nd Pickになります。");
            }
        }
    }
}
