package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.osu.UserAccount;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BanMap extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        if(e.getMessage().contains("!ban")) {
            String[] args = e.getMessage().split(" ");

            if(args.length != 2) {
                e.getBot().send().message(Main.tourney.getChannel(), "使い方: !ban <slot>");
                return;
            }

            args[1] = args[1].toUpperCase();

            if(Main.tourney.getAllBanned().values().stream().allMatch(Boolean::booleanValue)) {
                if(Main.ircClient.isDebug()) {
                    e.getBot().send().message(Main.tourney.getChannel(), "!mp close");
                }
                return;
            }

            List<String> keys = Main.tourney.getPool().values().stream()
                .flatMap(List::stream)
                .flatMap(m -> m.keySet().stream())
                .toList();

            if(!keys.contains(args[1])) {
                e.getBot().send().message(Main.tourney.getChannel(), "そのスロットは存在しません。");
                return;
            }

            int userID = UserAccount.getUserID(e.getUser().getNick());
            String teamName = Main.tourney.getTeamNameFromUser(String.valueOf(userID));
            Map<String, Boolean> allBanned = Main.tourney.getAllBanned();
            Map<String, String> banMaps = Main.tourney.getBanMaps();

            if(!Objects.equals(teamName, Main.tourney.getCurrentBanTeam())) {
                e.getBot().send().message(Main.tourney.getChannel(), "あなたはこの譜面をbanする権利がありません。");
                return;
            }

            allBanned.put(teamName, true);
            banMaps.put(teamName, args[1]);
            Main.tourney.setAllBanned(allBanned);
            Main.tourney.setBanMaps(banMaps);

            e.getBot().send().message(Main.tourney.getChannel(),  teamName.toUpperCase() + "チームは、" + args[1].toUpperCase() + "をbanしました。");
            System.out.println(banMaps);
        }
    }
}
