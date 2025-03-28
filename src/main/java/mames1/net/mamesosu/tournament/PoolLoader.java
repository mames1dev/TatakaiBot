package mames1.net.mamesosu.tournament;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mames1.net.mamesosu.google.SpreadSheets.getSheetsService;

// プール情報をロード
public class PoolLoader {

    String SPREADSHEET_ID;
    Sheets sheetsService;

    public PoolLoader () throws IOException, GeneralSecurityException {
        Dotenv dotenv = Dotenv.configure().load();
        SPREADSHEET_ID = dotenv.get("SPREADSHEET_ID");
        sheetsService = getSheetsService();
    }

    private int calculateRow(int[] row) {
        return (row[1] - row[0]);
    }

    private int[] loadRow(String sheet) throws IOException {

        String range = sheet + "!D3:E3";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();
        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            return new int[] {0, 0};
        }

        for (List row : values) {
            return new int [] {Integer.parseInt(row.get(0).toString()), Integer.parseInt(row.get(1).toString())};
        }

        return new int[] {0, 0};
    }


    // load all pools
    // ["pool_name": [{"NM1": 1}, {"NM2": 2}, ...], ...]
    public List<Map<String, List<Map<String, Integer>>>> loadAllPool() throws IOException, GeneralSecurityException {

        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(SPREADSHEET_ID).execute();
        List<Sheet> sheets = spreadsheet.getSheets();
        List<Map<String, List<Map<String, Integer>>>> pool = new ArrayList<>();

        for(Sheet sheet : sheets) {
            Map<String, List<Map<String, Integer>>> p = loadPool(sheet.getProperties().getTitle());
            if(p != null) {
                pool.add(p);
            }
        }

        return pool;
    }

    private Map<String, List<Map<String, Integer>>> loadPool(String sheet) throws IOException {

        int[] row = loadRow(sheet);

        if(row[0] == 0 && row[1] == 0) {
            return null;
        }

        String pool_name = sheet + "!A3";
        String mod_range = sheet + "!J6:" + "J" + (6 + calculateRow(row));
        String id_range = sheet + "!L6:" + "L" + (6 + calculateRow(row));

        ValueRange mod_response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, mod_range)
                .execute();
        ValueRange id_response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, id_range)
                .execute();

        ValueRange name = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, pool_name)
                .execute();

        List<List<Object>> mod_values = mod_response.getValues();
        List<List<Object>> id_values = id_response.getValues();
        List<List<Object>> name_values = name.getValues();

        if (mod_values == null || mod_values.isEmpty() || id_values == null || id_values.isEmpty()) {
            return null;
        }

        if(mod_values.size() != id_values.size()) {
            return null;
        }

        if (name_values == null || name_values.isEmpty()) {
            return null;
        }

        List<Map<String, Integer>> pool = new ArrayList<>();

        for(int i = 0; i < mod_values.size(); i++) {
            pool.add(Map.of(
                    mod_values.get(i).get(0).toString(),
                    Integer.parseInt(id_values.get(i).get(0).toString())
            ));
        }

        return Map.of(name_values.get(0).get(0).toString().toLowerCase().replaceAll(" ", "_") , pool);
    }
}
