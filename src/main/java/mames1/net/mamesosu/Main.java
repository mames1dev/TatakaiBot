package mames1.net.mamesosu;

import mames1.net.mamesosu.discord.Bot;
import mames1.net.mamesosu.irc.IRCClient;
import mames1.net.mamesosu.tournament.Pool;
import mames1.net.mamesosu.tournament.PoolLoader;
import mames1.net.mamesosu.tournament.Tourney;

public class Main {

    public static Bot bot;
    public static IRCClient ircClient;
    public static Tourney tourney;
    public static Pool pool;

    public static void main(String[] args) throws Exception{

        bot = new Bot();
        ircClient = new IRCClient();
        tourney = new Tourney();

        // Load all pool data
        pool = new Pool();

        bot.start();
        ircClient.start();
    }
}