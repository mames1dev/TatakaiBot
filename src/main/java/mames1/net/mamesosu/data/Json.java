package mames1.net.mamesosu.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class Json {

    public static JsonNode getJson(String url) {
        JsonNode node = null;
        try {
            String line;
            URL obj = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) obj.openConnection();

            urlConnection.setRequestMethod("GET");

            ObjectMapper mapper = new ObjectMapper();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder responce = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                responce.append(line);
            }
            reader.close();

            node = mapper.readTree(responce.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return node;
    }
}
