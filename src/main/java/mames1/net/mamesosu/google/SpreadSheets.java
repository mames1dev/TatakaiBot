package mames1.net.mamesosu.google;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class SpreadSheets {

    private static Credential authorize() throws IOException, GeneralSecurityException {

        InputStream in = SpreadSheets.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                GsonFactory.getDefaultInstance(), new InputStreamReader(in)
        );

        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(),
                clientSecrets, scopes
        ).setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
                .setAccessType("offline")
                .build();
        String redirectUri = "http://localhost/";

        AuthorizationCodeRequestUrl authorizationCodeRequestUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);

        System.out.println("Googleと接続しました。");
        System.out.println("URLから認証codeを取得してください: " + authorizationCodeRequestUrl);

        System.out.print("コードはこのコマンドラインにペーストしてください。\n");

        String code = new java.util.Scanner(System.in).nextLine();

        TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();

        Credential credential = flow.createAndStoreCredential(tokenResponse, "user");

        return credential;
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Dotenv dotenv = Dotenv.configure().load();
        String APPLICATION_NAME = dotenv.get("APPLICATION_NAME");
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
