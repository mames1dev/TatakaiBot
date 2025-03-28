package mames1.net.mamesosu.irc.event;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.discord.Embed;
import mames1.net.mamesosu.tournament.Tourney;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeavePlayer extends ListenerAdapter {

    @Override
    public void onGenericMessage(GenericMessageEvent e) {

        if(!e.getUser().getNick().equals("BanchoBot")) {
            return;
        }

        if(Main.tourney.isGameEnd()) {
            return;
        }

        String regex = "^(.+?) left the game\\.$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(e.getMessage());

        if(matcher.find()) {
            Message message = Main.tourney.getInviteMessage();
            e.getBot().send().message(Main.tourney.getChannel(), "!mp close");
            message.editMessageEmbeds(
                    Embed.getAbortMatch(String.valueOf(Main.tourney.getMatchID())).build()
            ).queue((msg) -> {
                Main.tourney = new Tourney();
            });
        }
    }
}
