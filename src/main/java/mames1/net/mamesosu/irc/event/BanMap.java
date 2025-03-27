package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.osu.UserAccount;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BanMap extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        if(e.getMessage().contains("!ban")) {

            String[] args = e.getMessage().split(" ");

            if(e.getUser().getNick().equals("BanchoBot")) {
                return;
            }

            if(args.length != 2) {
                e.getBot().send().message(Main.tourney.getChannel(), "使い方: !ban <slot>");
                return;
            }

            args[1] = args[1].toUpperCase();

            if(Main.tourney.getAllBanned().values().stream().allMatch(Boolean::booleanValue)) {
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
            List<String> maps = new ArrayList<>(banMaps.values());

            if (maps.contains(args[1])) {
                e.getBot().send().message(Main.tourney.getChannel(), "その譜面は既にbanされています。");
                return;
            }

            if(!Objects.equals(teamName, Main.tourney.getCurrentBanTeam())) {
                e.getBot().send().message(Main.tourney.getChannel(), "あなたはこの譜面をbanする権利がありません。");
                return;
            }

            allBanned.put(teamName, true);
            banMaps.put(teamName, args[1]);

            String p = Main.tourney.getTeamMemberFromTeam(teamName);
            System.out.println(p);
            String pName = UserAccount.getUserName(p);
            System.out.println(pName);

            e.getBot().send().message(Main.tourney.getChannel(),  pName + "は、" + args[1] + "をbanしました。");

            teamName = teamName.equals("blue") ? "red" : "blue";

            Main.tourney.setAllBanned(allBanned);
            Main.tourney.setBanMaps(banMaps);
            Main.tourney.setCurrentBanTeam(teamName);

            Map<String, String> teamMember = Main.tourney.getTeamMember();

            if(Main.tourney.getAllBanned().values().stream().allMatch(Boolean::booleanValue)) {

                Map<String, Integer> teamScore = Main.tourney.getTeamScore();
                p = Main.tourney.getTeamMemberFromTeam(teamName);
                pName = UserAccount.getUserName(p);

                e.getBot().send().message(Main.tourney.getChannel(), banMaps.get("blue") + "と" + banMaps.get("red") + "のマップがbanされました。");
                e.getBot().send().message(Main.tourney.getChannel(), UserAccount.getUserName(teamMember.get("red")) + " " + teamScore.get("red") + " - " + teamScore.get("blue") + " " + UserAccount.getUserName(teamMember.get("blue")) +
                        " | Pick: " + pName + " | Bo9");
                e.getBot().send().message(Main.tourney.getChannel(), pName + "はpickを行ってください。");
                e.getBot().send().message(Main.tourney.getChannel(), "!pick <slot> にてpickを行うことができます。");
                e.getBot().send().message(Main.tourney.getChannel(), "!mp timer 60");

                return;
            }

            e.getBot().send().message(Main.tourney.getChannel(), "!mp timer cancel");
            e.getBot().send().message(Main.tourney.getChannel(),  "次に、" + UserAccount.getUserName(teamMember.get(teamName)) + "はbanを行ってください。");
            e.getBot().send().message(Main.tourney.getChannel(), "!mp timer 60");
        }
    }
}
