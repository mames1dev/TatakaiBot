package mames1.net.mamesosu.osu;

import com.fasterxml.jackson.databind.JsonNode;
import mames1.net.mamesosu.data.Json;

public abstract class UserAccount {

    public static boolean isExist(String id) {

        String osuAPI = new Osu().getApi();
        String fetchURL = "https://osu.ppy.sh/api/get_user?k=" + osuAPI + "&u=" + id;

        JsonNode node = Json.getJson(fetchURL);

        return !node.isEmpty();
    }

    public static int getUserID(String nick) {

            String osuAPI = new Osu().getApi();
            String fetchURL = "https://osu.ppy.sh/api/get_user?k=" + osuAPI + "&u=" + nick;

            JsonNode node = Json.getJson(fetchURL);

            if(node.isEmpty()) {
                return -1;
            }

            return node.get(0).get("user_id").asInt();
    }

    public static String getUserName(String id) {

            String osuAPI = new Osu().getApi();
            String fetchURL = "https://osu.ppy.sh/api/get_user?k=" + osuAPI + "&u=" + id;

            JsonNode node = Json.getJson(fetchURL);

            if(node.isEmpty()) {
                return null;
            }

            return node.get(0).get("username").asText();
    }
}
