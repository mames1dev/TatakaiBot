package mames1.net.mamesosu.tournament;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;

import java.util.*;

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

    boolean isAllPlayerJoined = false;
    boolean isCreated = false;

    Map<String, Boolean> allBanned = new HashMap<>();

    String currentPickTeam = null;
    String currentBanTeam = null;

    String channel = null;
    String tourneyName = null;
    String roomName = null;
    int matchID = 0;
    int bo = 9;

    public Tourney() {
        players = new ArrayList<>();

        allBanned.put("red", false);
        allBanned.put("blue", false);

        teamScore.put("red", 0);
        teamScore.put("blue", 0);
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
}
