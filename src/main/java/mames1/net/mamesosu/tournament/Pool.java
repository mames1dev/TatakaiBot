package mames1.net.mamesosu.tournament;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// プール情報を保存
@Setter
@Getter
public class Pool {

    List<Map<String, List<Map<String, Integer>>>> allPool;

    public Pool () throws IOException, GeneralSecurityException {
        PoolLoader poolLoader = new PoolLoader();
        allPool = poolLoader.loadAllPool();
    }

    public List<String> getTourneyName() {

        return allPool.stream()
        .flatMap(map -> map.keySet().stream())
        .collect(Collectors.toList());
    }

    public Map<String, List<Map<String, Integer>>> getPool(String targetKey) {

        return allPool.stream()
            .filter(pool -> pool.containsKey(targetKey))
            .findFirst()
            .orElse(null);
    }
}
