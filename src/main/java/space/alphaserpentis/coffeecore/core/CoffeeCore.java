package space.alphaserpentis.coffeecore.core;

import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.nio.file.Path;
import java.util.Collection;

public class CoffeeCore {

    public static JDA api;
    public final long botOwnerId;
    public final Path serverDataPath;
    public CoffeeCore(
            @NonNull String token,
            @NonNull Path serverDataPath,
            long botOwnerId
    ) throws InterruptedException {
        this.serverDataPath = serverDataPath;
        this.botOwnerId = botOwnerId;
        api = JDABuilder.createDefault(token)
                .setChunkingFilter(ChunkingFilter.ALL)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI, CacheFlag.VOICE_STATE)
                .disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
                .build();

        api.awaitReady();
    }

    public CoffeeCore(
            @NonNull String token,
            @NonNull Path serverDataPath,
            long botOwnerId,
            @NonNull ChunkingFilter chunkingFilter,
            @NonNull Collection<CacheFlag> disabledCache,
            @NonNull Collection<CacheFlag> enabledCache,
            @NonNull Collection<GatewayIntent> enabledIntents,
            @NonNull Collection<GatewayIntent> disabledIntents
    ) throws InterruptedException {
        this.serverDataPath = serverDataPath;
        this.botOwnerId = botOwnerId;
        api = JDABuilder.createDefault(token)
                .setChunkingFilter(chunkingFilter)
                .disableCache(disabledCache)
                .enableCache(enabledCache)
                .disableIntents(disabledIntents)
                .enableIntents(enabledIntents)
                .build();

        api.awaitReady();
    }
}