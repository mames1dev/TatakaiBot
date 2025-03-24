package mames1.net.mamesosu.data;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    final String user;
    final String password;
    final String host;
    final String database;


    public MySQL() {
        Dotenv dotenv = Dotenv.configure().load();
        user = dotenv.get("MYSQL_USER");
        password = dotenv.get("MYSQL_PASSWORD");
        host = dotenv.get("MYSQL_HOST");
        database = dotenv.get("MYSQL_DATABASE");
    }

    public Connection getConnection() {

        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://" + host + "/" + database + "?useSSL=false",
                    user,
                    password
            );
        } catch (SQLException ex) {
            System.out.println("!!!!!! 正しくデータベースと接続できませんでした。Botを強制終了します。 !!!!!!");
            throw new RuntimeException();
        }
    }
}
