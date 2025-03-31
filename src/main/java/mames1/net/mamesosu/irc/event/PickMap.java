package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.osu.UserAccount;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PickMap extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        if(!e.getMessage().contains("!pick")) {
            return;
        }


        String[] args = e.getMessage().split(" ");

        if(e.getUser().getNick().equals("BanchoBot")) {
            return;
        }

        if (Main.tourney.isPickEnd()) {
            return;
        }

        if(Main.tourney.isGameEnd()) {
            return;
        }

        if(!Main.tourney.isBanEnd()) {
            return;
        }

        if(args.length != 2) {
            e.getBot().send().message(Main.tourney.getChannel(), "使い方: !pick <slot>");
            return;
        }

        String currentPickTeam = Main.tourney.getCurrentPickTeam();

        args[1] = args[1].toUpperCase();

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
        Map<String, String> banMaps = Main.tourney.getBanMaps();
        List<String> maps = new ArrayList<>(banMaps.values());

        if (maps.contains(args[1])) {
            e.getBot().send().message(Main.tourney.getChannel(), "この譜面はbanされているためpickすることはできません。");
            return;
        }

        if(args[1].contains("TB")) {
            e.getBot().send().message(Main.tourney.getChannel(), "この譜面はTB譜面です。");
            return;
        }

        if(!Objects.equals(teamName, currentPickTeam)) {
            e.getBot().send().message(Main.tourney.getChannel(), "あなたはこの譜面をpickする権利がありません。");
            return;
        }

        Map<String, List<String>> pickMaps = Main.tourney.getPickedMaps();

        for(String team : pickMaps.keySet()) {
            if(pickMaps.get(team).contains(args[1])) {
                e.getBot().send().message(Main.tourney.getChannel(), "この譜面は既にpickされています。");
                return;
            }
        }

        pickMaps.get(currentPickTeam).add(args[1]);

        int mapID = Main.tourney.getValueFromPool(Main.tourney.getTourneyName(), args[1]);

        e.getBot().send().message(Main.tourney.getChannel(), "!mp map " + mapID);

        e.getBot().send().message(Main.tourney.getChannel(), "!mp mods NF " + Main.tourney.getMod(args[1]));

        e.getBot().send().message(Main.tourney.getChannel(), "スロット: " + args[1] + " がpickされました。");

        Main.tourney.setPickedMaps(pickMaps);
        Main.tourney.setCurrentPickTeam(currentPickTeam.equals("blue") ? "red" : "blue");

        e.getBot().send().message(Main.tourney.getChannel(), "!mp timer cancel");
        e.getBot().send().message(Main.tourney.getChannel(), "!mp timer 120");

        e.getBot().send().message(Main.tourney.getChannel(), "約120秒後に試合が始まります！");

        Main.tourney.setPickEnd(true);
    }
}
