package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.osu.UserAccount;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FinishMatch extends ListenerAdapter {

     private String getResult() {
        List<Integer> winTeam = new ArrayList<>(Main.tourney.getWinTeam().stream().toList());

        Map<String, List<String>> pickMaps = Main.tourney.getPickedMaps();
        List<String> redPicks = pickMaps.getOrDefault("red", new ArrayList<>());
        List<String> bluePicks = pickMaps.getOrDefault("blue", new ArrayList<>());

        StringBuilder s = new StringBuilder();
        int index = 0;
        int redIndex = 0, blueIndex = 0;
        boolean isRedTurn = Main.tourney.getCurrentBanTeam().equals("red"); // 最初のピックを決定

        while (redIndex < redPicks.size() || blueIndex < bluePicks.size()) {
            if (isRedTurn && redIndex < redPicks.size()) {
                String pick = redPicks.get(redIndex++);
                if (s.length() > 0) s.append("\n");
                if(pick.contains("TB")) {
                    s.append(":fire: picked ``").append(pick).append("`` ").append(winTeam.get(index) == 1 ? ":red_square:" : ":blue_square:").append(" wins!");
                } else {
                    s.append(":red_square: picked ``").append(pick).append("`` ").append(winTeam.get(index) == 1 ? ":red_square:" : ":blue_square:").append(" wins!");
                }
                index++;
            } else if (!isRedTurn && blueIndex < bluePicks.size()) {
                String pick = bluePicks.get(blueIndex++);
                if (s.length() > 0) s.append("\n");
                if(pick.contains("TB")) {
                   s.append(":fire: picked ``").append(pick).append("`` ").append(winTeam.get(index) == 1 ? ":red_square:" : ":blue_square:").append(" wins!");
                } else {
                    s.append(":blue_square: picked ``").append(pick).append("`` ").append(winTeam.get(index) == 1 ? ":red_square:" : ":blue_square:").append(" wins!");
                }
                index++;
            }
            isRedTurn = !isRedTurn; // ピックのターンを交互に切り替え
        }

        return s.toString();
    }


    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        if(!e.getUser().getNick().equals("BanchoBot")) {
            return;
        }

        Pattern pattern = Pattern.compile("(\\w+) finished playing \\(Score: (\\d+), (\\w+)\\)\\.");
        Matcher matcher = pattern.matcher(e.getMessage());

        if(matcher.find()) {
            Map<String, Integer> playerScore = Main.tourney.getTeamEachScore();
            String team = Main.tourney.getTeamNameFromUser(String.valueOf(UserAccount.getUserID(matcher.group(1).replace(" ", "_"))));

            playerScore.put(team, Integer.parseInt(matcher.group(2)));

            if(playerScore.get("red") != -1 && playerScore.get("blue") != -1) {

                List<Integer> winTeam = new ArrayList<>(Main.tourney.getWinTeam().stream().toList());
                Map<String, Integer> teamScore = Main.tourney.getTeamScore();

                if(playerScore.get("red") > playerScore.get("blue")) {
                    winTeam.add(1);
                    teamScore.put("red", teamScore.get(team) + 1);
                }  else {
                    winTeam.add(2);
                    teamScore.put("blue", teamScore.get(team) + 1);
                }

                Main.tourney.setTeamScore(teamScore);

                Main.tourney.setWinTeam(winTeam);
                Main.tourney.setMatch(false);

                Main.ircClient.getBot().send().message(Main.tourney.getChannel(),  UserAccount.getUserName(Main.tourney.getTeamMember().get("red")) + " " + Main.tourney.getTeamScore().get("red") + " - " + Main.tourney.getTeamScore().get("blue") + " " + UserAccount.getUserName(Main.tourney.getTeamMember().get("blue")) +
                             " | Pick: " + UserAccount.getUserName(Main.tourney.getTeamMember().get(Main.tourney.getCurrentPickTeam().equals("red") ? "blue" : "red")) + " | Bo9");

                // マッチ閉じる動作書く
                if(Main.tourney.getTeamScore().get("red") == (Main.tourney.getBo() + 1) / 2 || Main.tourney.getTeamScore().get("blue") == (Main.tourney.getBo() + 1) / 2) {
                    EmbedBuilder eb = new EmbedBuilder();
                    int team1 = Main.tourney.getTeamScore().get("red");
                    int team2 = Main.tourney.getTeamScore().get("blue");

                    eb.setTitle("TatakaiBot Match Result", "https://osu.ppy.sh/community/matches/" + Main.tourney.getMatchID());
                    eb.setDescription((team1 > team2 ? ":medal: " : "") + ":red_square: " + UserAccount.getUserName(Main.tourney.getTeamMember().get("red")) + " " + Main.tourney.getTeamScore().values().stream().toList().get(0) + " - " + Main.tourney.getTeamScore().values().stream().toList().get(1) +  " " + UserAccount.getUserName(Main.tourney.getTeamMember().get("blue"))  + " :blue_square: " + (team1 < team2 ? ":medal:" : ""));
                    eb.addField("Mappool", Main.tourney.getTourneyName(), false);
                    eb.addField("Ban",  UserAccount.getUserName(Main.tourney.getTeamMember().get("red")) + ": ``" + Main.tourney.getBanMaps().values().stream().toList().get(0) + "``\n" +
                            UserAccount.getUserName(Main.tourney.getTeamMember().get("blue")) + ": ``" + Main.tourney.getBanMaps().values().stream().toList().get(1) + "``", false);
                    eb.addField("Pick", getResult(), false);
                    eb.setColor(team1 > team2 ? Color.RED : Color.BLUE);
                    eb.setTimestamp(new Date().toInstant());

                    Message message = Main.tourney.getInviteMessage();
                    message.editMessageEmbeds(eb.build()).queue();

                    Main.ircClient.getBot().send().message(Main.tourney.getChannel(),
                            "お疲れさまでした。試合結果はDiscordからいつでも閲覧する事ができます。");
                    Main.ircClient.getBot().send().message(Main.tourney.getChannel(),
                            "この部屋は60秒後に自動で閉じられます。");
                    Main.ircClient.getBot().send().message(Main.tourney.getChannel(), "!mp timer 60");
                    Main.tourney.setGameEnd(true);

                    return;
                }

                if(Main.tourney.getTeamScore().get("red") == ((Main.tourney.getBo() + 1) / 2 - 1) && Main.tourney.getTeamScore().get("blue") == (Main.tourney.getBo() + 1) / 2 - 1) {

                    int mapID = Main.tourney.getValueFromPool(Main.tourney.getTourneyName(), "TB");
                    Map<String, List<String>> pickMaps = Main.tourney.getPickedMaps();

                    Main.ircClient.getBot().send().message(Main.tourney.getChannel(), "!mp map " + mapID);
                    Main.ircClient.getBot().send().message(Main.tourney.getChannel(), "!mp mods FreeMod");
                    Main.ircClient.getBot().send().message(Main.tourney.getChannel(), "!mp timer cancel");
                    Main.ircClient.getBot().send().message(Main.tourney.getChannel(), "!mp timer 120");

                    pickMaps.get(Main.tourney.getCurrentPickTeam()).add("TB");
                    Main.tourney.setPickedMaps(pickMaps);

                    Main.tourney.setPickEnd(true);
                    return;
                }

                String pName = UserAccount.getUserName(Main.tourney.getTeamMemberFromTeam(Main.tourney.getCurrentPickTeam()));

                e.getBot().send().message(Main.tourney.getChannel(), pName + "はpickを行ってください。");
                e.getBot().send().message(Main.tourney.getChannel(), "!pick <slot> にてpickを行うことができます。");
                e.getBot().send().message(Main.tourney.getChannel(), "!mp timer 60");

                playerScore.put("red", -1);
                playerScore.put("blue", -1);

                Main.tourney.setPickEnd(false);
            }
            Main.tourney.setTeamEachScore(playerScore);
        }
    }
}
