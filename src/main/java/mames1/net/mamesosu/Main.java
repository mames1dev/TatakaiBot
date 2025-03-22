package mames1.net.mamesosu;

import mames1.net.mamesosu.discord.Bot;
import mames1.net.mamesosu.irc.IRCClient;

public class Main {

    public static Bot bot;

    public static void main(String[] args) throws Exception{

        Bot bot = new Bot();
        IRCClient ircClient = new IRCClient();
        bot.start();
        ircClient.start();
    }
}