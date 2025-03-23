package mames1.net.mamesosu.pool;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mames1.net.mamesosu.google.SpreadSheets.getSheetsService;

public class PoolLoader {

    String SPREADSHEET_ID;
    Sheets sheetsService;

    public PoolLoader () {
        Dotenv dotenv = Dotenv.configure().load();
        SPREADSHEET_ID = dotenv.get("SPREADSHEET_ID");
    }

    private int calculateRow(int[] row) {
        return (row[1] - row[0]);
    }

    private int[] loadRow() throws IOException, GeneralSecurityException {

        sheetsService = getSheetsService();
        String range = "D3:E3";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();
        List<List<Object>> values = response.getValues();

        for (List row : values) {
            return new int [] {Integer.parseInt(row.get(0).toString()), Integer.parseInt(row.get(1).toString())};
        }

        return new int[] {0, 0};
    }

    public List<Map<String, Integer>> loadPool () throws IOException, GeneralSecurityException {

        int[] row = loadRow();

        String mod_range = "J6:" + "J" + (6 + calculateRow(row));
        String id_range = "L6:" + "L" + (6 + calculateRow(row));

        ValueRange mod_response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, mod_range)
                .execute();
        ValueRange id_response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, id_range)
                .execute();

        List<List<Object>> mod_values = mod_response.getValues();
        List<List<Object>> id_values = id_response.getValues();

        List<Map<String, Integer>> pool = new ArrayList<>();

        for(int i = 0; i < mod_values.size(); i++) {
            pool.add(Map.of(
                    mod_values.get(i).get(0).toString(),
                    Integer.parseInt(id_values.get(i).get(0).toString())
            ));
        }

        return pool;
    }
}
