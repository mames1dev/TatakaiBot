package mames1.net.mamesosu.discord;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import mames1.net.mamesosu.discord.event.CreateMatch;
import mames1.net.mamesosu.discord.event.LinkAccount;
import mames1.net.mamesosu.discord.event.Pool;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Getter
public class Bot {

    String token;
    String presence;
    JDA jda;

    public Bot() {
        Dotenv dotenv = Dotenv.configure().load();
        token = dotenv.get("BOT_TOKEN");
        presence = dotenv.get("BOT_PRESENCE");
    }

    public void start() {
        jda = JDABuilder.createDefault(token)
                .setRawEventsEnabled(true)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS
                ).enableCache(
                        CacheFlag.MEMBER_OVERRIDES,
                        CacheFlag.ROLE_TAGS,
                        CacheFlag.EMOJI
                )
                .disableCache(
                        CacheFlag.VOICE_STATE,
                        CacheFlag.STICKER,
                        CacheFlag.SCHEDULED_EVENTS
                ).setActivity(
                        Activity.playing(presence)
                 ).setMemberCachePolicy(
                        MemberCachePolicy.ALL
                ).setChunkingFilter(
                        ChunkingFilter.ALL
                )
                .addEventListeners(
                        new LinkAccount(),
                        new CreateMatch(),
                        new Pool()
                )
                .build();
    }
}
