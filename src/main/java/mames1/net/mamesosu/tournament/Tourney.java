package mames1.net.mamesosu.tournament;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 大会情報を保存 (使用する度に初期化する)
@Setter
@Getter
public class Tourney {

    List<Map<Long, Integer>> players; // [DiscordID, BanchoID]
    List<Integer> playerIDList = new ArrayList<>(); // BanchoID
    Map<String, List<Map<String, Integer>>> pool = new HashMap<>();

    Message inviteMessage = null;
    Message invitePlayerMessage = null;

    Map<String, String> teamMember = new HashMap<>();

    boolean isAllPlayerJoined = false;
    boolean isCreated = false;
    String tourneyName = null;

    String channel = null;

    String roomName = null;
    int matchID = 0;


    public Tourney() {
        players = new ArrayList<>();
    }
}
