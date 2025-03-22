package mames1.net.mamesosu.irc;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

public class IRCClient {

    String name;
    String password;
    int port;
    String server;
    String channel;
    boolean debug;

    @Getter
    PircBotX bot; //irc client

    public IRCClient () {
        Dotenv dotenv = Dotenv.configure().load();
        name = dotenv.get("IRC_NAME");
        password = dotenv.get("IRC_PASSWORD");
        port = Integer.parseInt(dotenv.get("IRC_PORT"));
        server = dotenv.get("IRC_SERVER");
        channel = "BanchoBot";
        debug = Boolean.parseBoolean(dotenv.get("DEBUG"));
    }

    public void start() throws Exception {
        if(debug) {
            channel = "#osu";
        }

        Configuration configuration = new Configuration.Builder()
                .setName(name)
                .setServerPassword(password)
                .addServer(server)
                .setServerPort(port)
                .addAutoJoinChannel(channel)
                .addListener(new DebugListener())
                .buildConfiguration();

        bot = new PircBotX(configuration);
        bot.startBot();
    }
}
