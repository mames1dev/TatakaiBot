package mames1.net.mamesosu.discord.event;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.data.MySQL;
import mames1.net.mamesosu.discord.Embed;
import mames1.net.mamesosu.osu.UserAccount;
import mames1.net.mamesosu.tournament.Tourney;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CreateMatch extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if(e.getMessage().getContentRaw().contains("!match")) {
            String[] args = e.getMessage().getContentRaw().split(" ");
            List<Map<Long, Integer>> playerList = new ArrayList<>();
            Map<String, List<Map<String, Integer>>> pool;

            if(args.length < 3) {
                e.getMessage().replyEmbeds(
                        Embed.getErrorEmbed(
                                "引数が不足しているか、正しくありません！\n" +
                                        "正しい使い方: !match <招待するプレイヤー> <トーナメント名>"
                        ).build()
                ).queue();
            }

            String tournamentName = String.join(" ", Arrays.copyOfRange(args, 2, args.length)).toLowerCase();

            MySQL mySQL = new MySQL();
            Connection connection = mySQL.getConnection();
            PreparedStatement ps;
            ResultSet result;

            try {

                // player1の情報セット
                ps = connection.prepareStatement("select bancho_id from link where discord_id = ?");
                ps.setLong(1, e.getAuthor().getIdLong());
                result = ps.executeQuery();

                if(!result.next()) {
                    e.getMessage().replyEmbeds(
                            Embed.getErrorEmbed(
                                    "あなたのBanchoアカウントがリンクされていません！\n" +
                                                "``!link``でリンクを行ってからもう一度お試しください！"
                            ).build()
                    ).queue();
                    return;
                } else {
                    playerList.add(Map.of(e.getAuthor().getIdLong(), result.getInt("bancho_id")));
                }

                // player2の情報セット
                int p = UserAccount.getUserID(args[1]);
                if(p == -1) {
                    e.getMessage().replyEmbeds(
                            Embed.getErrorEmbed(
                                    "指定されたプレイヤーが存在しません！\n" +
                                                "プレイヤー名をもう一度確認してみてください！"
                            ).build()
                    ).queue();
                    return;
                }

                ps = connection.prepareStatement("select discord_id from link where bancho_id = ?");
                ps.setInt(1, p);
                result = ps.executeQuery();

                if(result.next()) {
                    playerList.add(Map.of(result.getLong("discord_id"), p));
                }

                // プレイヤーチェック完了
                // トーナメント名が存在するか確認

                try {
                    Main.tourney = new Tourney();
                    List<String> t = Main.pool.getTourneyName();
                    System.out.println(t);

                    if(!t.contains(tournamentName)) {
                        e.getMessage().replyEmbeds(
                                Embed.getErrorEmbed(
                                        "指定されたトーナメントが存在しません！\n" +
                                                "トーナメント名をもう一度確認してみてください！"
                                ).build()
                        ).queue();
                        return;
                    }

                    // トーナメントチェック完了
                    // 試合準備

                    Main.tourney.setPlayers(playerList);

                    // 試合用にプールを検索してセット
                    pool = Main.pool.getPool(tournamentName);
                    Main.tourney.setPool(pool);

                    List<Long> discordUsers = playerList.stream()
                            .filter(map -> !map.isEmpty())
                            .map(map -> map.keySet().iterator().next())
                            .toList();

                    System.out.println(discordUsers);
                    System.out.println(discordUsers.get(discordUsers.size()-1));

                    Member member = e.getGuild().getMemberById(discordUsers.get(1));
                    User user = member.getUser();

                    user.openPrivateChannel()
                                .flatMap(channel -> channel.sendMessageEmbeds(
                                        Embed.getInviteEmbed(args).build()
                                )).queue((msg)-> {
                                    msg.addReaction(Emoji.fromUnicode("U+2705")).queue();
                            });
                    System.out.println("試合招待を送信しました。");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
