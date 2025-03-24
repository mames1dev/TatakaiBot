package mames1.net.mamesosu.discord;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Date;

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
                        "Banchoにログインし、Rikuimaに次のメッセージを送信してください。")
                .addField("コード","```" + code + "```", false)
                .setColor(Color.GRAY)
                .setTimestamp(new Date().toInstant());
    }

    public static EmbedBuilder getInviteEmbed(String[] args) {
        return new EmbedBuilder()
                .setTitle("試合へ参加しますか？")
                .setDescription(args[1] + "さんが" + args[2] + "を試合に招待しました！\n" +
                        "このメッセージにリアクションをすると、試合に参加することができます。\n" +
                        "Rikuimaから招待を受けられるよう、ログインをして待機してください。")
                .setColor(Color.GRAY)
                .setTimestamp(new Date().toInstant());
    }
}
