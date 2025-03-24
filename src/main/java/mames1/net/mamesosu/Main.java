package mames1.net.mamesosu;

import mames1.net.mamesosu.data.MySQL;
import mames1.net.mamesosu.discord.Bot;
import mames1.net.mamesosu.irc.IRCClient;
import mames1.net.mamesosu.osu.Osu;
import mames1.net.mamesosu.pool.PoolLoader;

public class Main {

    public static Bot bot;
    public static IRCClient ircClient;
    public static PoolLoader poolLoader;

    public static void main(String[] args) throws Exception{

        bot = new Bot();
        ircClient = new IRCClient();
        poolLoader = new PoolLoader();

        bot.start();
        // start: debug code
        System.out.println(poolLoader.loadAllPool());
        // end: debug code
        ircClient.start();
    }
}