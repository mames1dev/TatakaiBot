package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.osu.UserAccount;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Roll extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        Pattern pattern = Pattern.compile("^(.+?) rolls (\\d+) point\\(s\\)$");
        Matcher matcher = pattern.matcher(e.getMessage());

        if(!e.getUser().getNick().equals("BanchoBot")) {
            return;
        }

        if(matcher.find()) {
            String name = matcher.group(1);
            int point = Integer.parseInt(matcher.group(2));
            Map<String, Integer> rollScore = Main.tourney.getRollScore();

            rollScore.put(name, point);

            Main.tourney.setRollScore(rollScore);

            // ロールの結果を保存
            if(rollScore.size() == 2) {
                String banPlayer = Collections.max(rollScore.entrySet(), Map.Entry.comparingByValue()).getKey();
                String pickPlayer = Collections.min(rollScore.entrySet(), Map.Entry.comparingByValue()).getKey();

                int banPlayerID = UserAccount.getUserID(banPlayer);
                int pickPlayerID = UserAccount.getUserID(pickPlayer);

                Main.tourney.setCurrentBanTeam(Main.tourney.getTeamNameFromUser(String.valueOf(banPlayerID)));
                Main.tourney.setCurrentPickTeam(Main.tourney.getTeamNameFromUser(String.valueOf(pickPlayerID)));

                e.getBot().send().message(Main.tourney.getChannel(), banPlayer + "はbanを行ってください。");
                e.getBot().send().message(Main.tourney.getChannel(), "!ban <slot> にてbanを行うことができます。");
                e.getBot().send().message(Main.tourney.getChannel(), "!mp timer 60");
            }
        }
    }
}
