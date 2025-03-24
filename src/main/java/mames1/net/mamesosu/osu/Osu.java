package mames1.net.mamesosu.osu;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

public class Osu {

    @Getter
    final String api;

    public Osu() {
        Dotenv dotenv = Dotenv.configure().load();
        api = dotenv.get("OSU_API");
    }
}
