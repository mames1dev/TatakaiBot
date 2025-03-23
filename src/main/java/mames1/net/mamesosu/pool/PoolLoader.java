package mames1.net.mamesosu.pool;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static mames1.net.mamesosu.google.SpreadSheets.getSheetsService;

public class PoolLoader {

    String SPREADSHEET_ID;
    Sheets sheetsService;

    public PoolLoader () {
        Dotenv dotenv = Dotenv.configure().load();
        SPREADSHEET_ID = dotenv.get("SPREADSHEET_ID");
    }

    public int[] loadRow() throws IOException, GeneralSecurityException {

        int[] r = new int[2];
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
}
