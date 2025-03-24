package mames1.net.mamesosu.tournament;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 大会情報を保存 (使用する度に初期化する)
@Setter
@Getter
public class Tourney {

    List<Map<Long, Integer>> players; // [DiscordID, BanchoID]
    Map<String, List<Map<String, Integer>>> pool = new HashMap<>();

    public Tourney() {
        players = new ArrayList<>();
    }
}
