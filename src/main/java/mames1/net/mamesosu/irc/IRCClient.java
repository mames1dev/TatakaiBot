package mames1.net.mamesosu.irc;

import io.github.cdimascio.dotenv.Dotenv;
import io.opencensus.trace.Link;
import lombok.Getter;
import mames1.net.mamesosu.irc.event.*;
import net.dv8tion.jda.api.entities.Guild;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

public class IRCClient {

    @Getter
    String name;
    String password;
    int port;
    String server;
    String channel;
    @Getter
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

        Configuration configuration = new Configuration.Builder()
                .setName(name)
                .setServerPassword(password)
                .addServer(server)
                .setServerPort(port)
                .addListener(new ReceiveCode())
                .addListener(new CreateMatch())
                .addListener(new CheckJoin())
                .addListener(new Roll())
                .addListener(new BanMap())
                .addListener(new DebugClose())
                .addListener(new PickMap())
                .addListener(new ReadyPlayer())
                .addListener(new FinishMatch())
                .addListener(new CloseRoom())
                .addListener(new LeavePlayer())
                .buildConfiguration();

        bot = new PircBotX(configuration);
        bot.startBot();
    }
}
