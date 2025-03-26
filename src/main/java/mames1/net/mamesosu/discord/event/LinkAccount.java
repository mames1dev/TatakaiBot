package mames1.net.mamesosu.discord.event;


import mames1.net.mamesosu.data.MySQL;
import mames1.net.mamesosu.discord.Embed;
import mames1.net.mamesosu.osu.UserAccount;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class LinkAccount extends ListenerAdapter {

    private String getLinkCode() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Random random = new Random();

        int verification_code = random.nextInt(100001, 999999);

        String hash = encoder.encode(String.valueOf(verification_code));
        hash = hash.substring(hash.length() - 12);

        return hash;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if (e.getAuthor().isBot()) {
            return;
        }

        if(e.getChannelType().isGuild()) {
            return;
        }

        if(e.getMessage().getContentRaw().contains("!link")) {
            String[] args = e.getMessage().getContentRaw().split(" ");

            if(args.length != 2) {
                e.getMessage().replyEmbeds(
                        Embed.getErrorEmbed(
                                "引数が不足しているか、正しくありません！\n" +
                                        "正しい使い方: !link <BanchoID>"
                        ).build()
                ).queue();
                return;
            }

            String banchoID = args[1];

            if(!UserAccount.isExist(banchoID)) {
                e.getMessage().replyEmbeds(
                        Embed.getErrorEmbed("指定されたIDのユーザーは存在しません。\n" +
                                "IDをもう一度確認してみてください！").build()
                ).queue();
                return;
            }

            MySQL mySQL = new MySQL();
            Connection connection = mySQL.getConnection();
            PreparedStatement ps;
            ResultSet result;

            try {

                ps = connection.prepareStatement("select bancho_id, code from link where discord_id = ?");
                ps.setLong(1, e.getAuthor().getIdLong());
                result = ps.executeQuery();
                if(result.next()) {
                    if(result.getInt("bancho_id") != 0) {
                        e.getMessage().replyEmbeds(
                               Embed.getErrorEmbed(
                                        "既にリンク済みのアカウントです。\n" +
                                                "リンク済みのアカウント: " +
                                                UserAccount.getUserName(result.getString("bancho_id"))
                                ).build()
                        ).queue();

                        return;
                    }

                    e.getMessage().replyEmbeds(
                        Embed.getSuccessLinkCodeEmbed(result.getString("code")).build()
                    ).queue();
                    return;
                }

                String code = getLinkCode();

                ps = connection.prepareStatement("insert into link (discord_id, code) values (?, ?)");
                ps.setLong(1, e.getAuthor().getIdLong());
                ps.setString(2, code);
                ps.executeUpdate();

                e.getMessage().replyEmbeds(
                        Embed.getSuccessLinkCodeEmbed(code).build()
                ).queue();

            } catch (SQLException ex) {
                System.out.println("!!!!!! データベースエラーが発生しました。 !!!!!!");
                ex.printStackTrace();
            }
        }
    }
}
