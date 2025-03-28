package mames1.net.mamesosu.tournament;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 大会情報を保存 (使用する度に初期化する)
@Setter
@Getter
public class Tourney {

    List<Map<Long, Integer>> players; // [DiscordID, BanchoID]
    List<Integer> playerIDList = new ArrayList<>(); // BanchoID

    Map<String, List<Map<String, Integer>>> pool = new HashMap<>(); // pool
    Map<String, String> banMaps = new HashMap<>(); // Team, Slot

    Message inviteMessage = null;
    Message invitePlayerMessage = null;

    Map<String, String> teamMember = new HashMap<>(); // TeamName, BanchoID

    Map<String, Integer> rollScore = new HashMap<>();
    Map<String, Integer> teamScore = new HashMap<>(); // TeamName, Score

    List<Integer> winTeam = new ArrayList<>();

    boolean isAllPlayerJoined = false;
    boolean isCreated = false;
    boolean isPickEnd = false;
    boolean isMatch = false;
    boolean isGameEnd = false;

    Map<String, Integer> teamEachScore = new HashMap<>();

    Map<String, Boolean> allBanned = new HashMap<>();

    Map<String, List<String>> pickedMaps = new HashMap<>();

    String currentPickTeam = null;
    String currentBanTeam = null;

    String channel = null;
    String tourneyName = null;
    String roomName = null;

    int matchID = 0;
    int bo;

    public Tourney() {

        Dotenv dotenv = Dotenv.configure().load();
        bo = Integer.parseInt(dotenv.get("BO"));

        players = new ArrayList<>();

        allBanned.put("red", false);
        allBanned.put("blue", false);

        teamScore.put("red", 0);
        teamScore.put("blue", 0);

        pickedMaps.put("red", new ArrayList<>());
        pickedMaps.put("blue", new ArrayList<>());

        teamEachScore.put("red", -1);
        teamEachScore.put("blue", -1);
    }

    public String getTeamMemberFromTeam(String teamName) {
        return teamMember.get(teamName);
    }

    public String getTeamNameFromUser(String banchoID) {
        if(teamMember.containsValue(banchoID)) {
            for (Map.Entry<String, String> entry : teamMember.entrySet()) {
                if (entry.getValue().equals(banchoID)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public Integer getValueFromPool(String poolName, String slot) {
        List<Map<String, Integer>> list = pool.get(poolName);
        if (list == null) return null; // 指定キーが存在しない場合

        for (Map<String, Integer> map : list) {
            if (map.containsKey(slot)) {
                return map.get(slot);
            }
        }

        return null; // 何も見つからなかった場合
    }

    public String getMod(String slot) {
        Pattern pattern = Pattern.compile("^(HR|DT|FL|HD|FI|FM|NM|EZ|RX|HT|SD|PF|AP)\\d*$");
        Matcher matcher = pattern.matcher(slot);

        if(matcher.find()) {
            return switch (matcher.group(1)) {
                case "NM" -> "None";
                case "FM" -> "FreeMod";
                case "EZ" -> "2";
                case "RX" -> "128";
                case "HT" -> "256";
                case "SD" -> "32";
                case "PF" -> "16384";
                case "AP" -> "8192";
                default -> matcher.group(1);
            };

        } else {
            return "FreeMod";
        }
    }

    public int getPickCount() {
        return pickedMaps.get("red").size() + pickedMaps.get("blue").size();
    }
}
