package mames1.net.mamesosu.discord.event;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.data.MySQL;
import mames1.net.mamesosu.discord.Embed;
import mames1.net.mamesosu.osu.UserAccount;
import mames1.net.mamesosu.tournament.Tourney;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CreateMatch extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {

        if(e.getUser().isBot()) {
            return;
        }

        if(e.isFromGuild()) {
            return;
        }

        User user = e.getUser();

        // トーナメントが存在しない場合は処理を行わない
        if(Main.tourney.getTourneyName() == null) {
            user.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(
                            Embed.getErrorEmbed(
                                    "この招待は無効です！\n" +
                                            "もう一度招待を送信する必要があります。"
                            ).build()
                    )).queue();
            return;
        }

        // リアクションが送信者のものでない場合は処理を行わない
        if(e.getUserIdLong() != Main.tourney.getPlayers().get(1).keySet().iterator().next()) {
            user.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(
                            Embed.getErrorEmbed(
                                    "この招待は無効です！\n" +
                                            "もう一度招待を送信する必要があります。"
                            ).build()
                    )).queue();
            return;
        }

        if(Main.tourney.isCreated()) {
            return;
        }

        if(e.getReaction().getEmoji().equals(Emoji.fromUnicode("U+274C"))) {
            Message m = Main.tourney.getInviteMessage();
            m.editMessageEmbeds(
                Embed.getInviteDenyEmbed().build()
            ).queue();

            Message m_p = Main.tourney.getInvitePlayerMessage();

            m_p.removeReaction(Emoji.fromUnicode("U+2705")).queue();
            m_p.removeReaction(Emoji.fromUnicode("U+274C")).queue();

            m_p.editMessageEmbeds(
                Embed.getInviteDenyPlayerEmbed().build()
            ).queue();

            Main.tourney = new Tourney();

            return;
        }

        if(!e.getReaction().getEmoji().equals(Emoji.fromUnicode("U+2705"))) {
            return;
        }

        // プレイヤー情報取得
        List<Integer> banchoID = new ArrayList<>();
        List<String> banchoName = new ArrayList<>();

        Main.tourney.getPlayers().forEach(map -> map.forEach((k, v) -> banchoID.add(v)));
        for(int id : banchoID) {
            banchoName.add(UserAccount.getUserName(String.valueOf(id)));
        }

        // 試合開始
        Main.tourney.setCreated(true);
        Main.ircClient.getBot().send().message("BanchoBot", "!mp make TatakaiBot Match | " + banchoName.get(0) + " vs " + banchoName.get(1));

        Message m = Main.tourney.getInviteMessage();
        Message m_p = Main.tourney.getInvitePlayerMessage();

        m.editMessageEmbeds(
            Embed.getInviteAcceptEmbed().build()
        ).queue();

        m_p.editMessageEmbeds(
            Embed.getInviteAcceptPlayerEmbed().build()
        ).queue();
        m_p.removeReaction(Emoji.fromUnicode("U+2705")).queue();
        m_p.removeReaction(Emoji.fromUnicode("U+274C")).queue();

        System.out.println("Created match: " + banchoName.get(0) + " vs " + banchoName.get(1));
    }

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
                return;
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
                } else {
                    e.getMessage().replyEmbeds(
                            Embed.getErrorEmbed(
                                    "指定されたプレイヤーが存在しません！\n" +
                                                 "プレイヤー名をもう一度確認してみてください！"
                            ).build()
                    ).queue();
                    return;
                }

                // プレイヤーチェック完了
                // トーナメント名が存在するか確認

                try {
                    if(!Main.tourney.getPlayers().isEmpty()) {
                        e.getMessage().replyEmbeds(
                                Embed.getErrorEmbed(
                                        "現在進行中の試合があるため、部屋を作成できません。\n" +
                                                "終了までお待ちください。"
                                ).build()
                        ).queue();
                        return;
                    }

                    Main.tourney = new Tourney();
                    List<String> t = Main.pool.getTourneyName();

                    if(!t.contains(tournamentName)) {
                        e.getMessage().replyEmbeds(
                                Embed.getErrorEmbed(
                                        "指定されたトーナメントが存在しません！\n" +
                                                "``!pool`` で確認してみてください！"
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

                    // トーナメント名をセット
                    Main.tourney.setTourneyName(tournamentName);

                    // player2に招待を送信
                    List<Long> discordUsers = playerList.stream()
                            .filter(map -> !map.isEmpty())
                            .map(map -> map.keySet().iterator().next())
                            .toList();

                    Member member = e.getGuild().getMemberById(discordUsers.get(1));
                    User user = member.getUser();

                    user.openPrivateChannel()
                                .flatMap(channel -> channel.sendMessageEmbeds(
                                        Embed.getInviteEmbed(e.getMember(), args).build()
                                )).queue((msg)-> {
                                    msg.addReaction(Emoji.fromUnicode("U+2705")).queue();
                                    msg.addReaction(Emoji.fromUnicode("U+274C")).queue();
                                    Main.tourney.setInvitePlayerMessage(msg);
                            });

                    e.getMessage().replyEmbeds(
                        Embed.getInviteSuccessEmbed().build()
                    ).queue((msg) -> {
                        Main.tourney.setInviteMessage(msg);
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
