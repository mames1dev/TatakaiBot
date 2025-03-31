package mames1.net.mamesosu.discord;

import mames1.net.mamesosu.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.util.Date;
import java.util.List;

public abstract class Embed {

    public static EmbedBuilder getErrorEmbed(String message) {
        return new EmbedBuilder()
                .setTitle("エラー")
                .setDescription(message)
                .setColor(Color.RED)
                .setTimestamp(new Date().toInstant());
    }

    public static EmbedBuilder getSuccessLinkCodeEmbed(String code) {
        return new EmbedBuilder()
                .setTitle("アカウントをリンクする")
                .setDescription("あなたの認証コードは以下の通りです。\n" +
                        "Banchoにログインし **" + Main.ircClient.getName() + "** に次のメッセージを送信してください。")
                .addField("コード","```" + code + "```", false)
                .setColor(Color.GRAY)
                .setTimestamp(new Date().toInstant());
    }

    public static EmbedBuilder getInviteSuccessEmbed() {
        return new EmbedBuilder()
                .setTitle("招待に成功しました！")
                .setDescription("招待が成功しました！\n" +
                        "相手がリアクションをするまでお待ちください。")
                .setColor(Color.GRAY)
                .setTimestamp(new Date().toInstant());
    }

    public static EmbedBuilder getInviteEmbed(Member m, String[] args) {
        return new EmbedBuilder()
                .setTitle("試合へ参加しますか？")
                .setDescription("**" + m.getEffectiveName() + "**さんが、**" + args[1] + "**を、**" + args[2] + "**に招待しました！\n" +
                        "参加する場合は **" + Main.ircClient.getName() + "** から招待を受けられるよう、ログインをして待機してください。")
                .addField("使い方", """
                        * :white_check_mark: をクリックすると、試合が自動で作成
                        * :x: をクリックで、招待を拒否
                        """, false)
                .setColor(Color.GRAY)
                .setTimestamp(new Date().toInstant());
    }

    public static EmbedBuilder getInviteDenyEmbed() {
        return new EmbedBuilder()
                .setTitle("招待が破棄されました！")
                .setDescription("招待したプレイヤーが招待を拒否しました！\n" +
                        "もう一度招待を行う場合は、招待を送信してください。")
                .setColor(Color.RED)
                .setTimestamp(new Date().toInstant());
    }


    public static EmbedBuilder getInviteDenyPlayerEmbed() {
        return new EmbedBuilder()
                .setTitle("招待を拒否しました！")
                .setDescription("この大会への招待を拒否しました！")
                .setColor(Color.RED)
                .setTimestamp(new Date().toInstant());
    }

    public static EmbedBuilder getInviteAcceptEmbed() {
        return new EmbedBuilder()
                .setTitle("招待が承認されました！")
                .setDescription("この大会への招待が承認されました！\n" +
                        "試合が開始されるまでお待ちください。")
                .setColor(Color.GRAY)
                .setTimestamp(new Date().toInstant());
    }

    public static EmbedBuilder getInviteAcceptPlayerEmbed() {
        return new EmbedBuilder()
                .setTitle("招待を承認しました！")
                .setDescription("この大会への招待を承認しました！\n" +
                        "試合が開始されるまでお待ちください。")
                .setColor(Color.GRAY)
                .setTimestamp(new Date().toInstant());
    }

    public static EmbedBuilder getAbortMatch(String id) {
        return new EmbedBuilder()
                .setTitle("TatakaiBot Match Result", "https://osu.ppy.sh/community/matches/" + id)
                .setDescription("この試合はプレイヤーが退出したため、中止されました！")
                .setColor(Color.RED)
                .setTimestamp(new Date().toInstant());
    }

    public static EmbedBuilder getPool(List<String> pool) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("プール一覧");
        eb.setDescription("現在の利用可能なプール一覧です。");

        StringBuilder poolList = new StringBuilder();

        for (String s : pool) {
            poolList.append(s).append("\n");
        }

        eb.addField("プール", poolList.toString(), false);
        eb.setColor(Color.GRAY);

        return eb;
    }
}
