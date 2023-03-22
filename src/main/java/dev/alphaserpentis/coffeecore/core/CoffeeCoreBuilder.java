package dev.alphaserpentis.coffeecore.core;

import dev.alphaserpentis.coffeecore.data.bot.BotSettings;
import dev.alphaserpentis.coffeecore.handler.api.discord.servers.AbstractServerDataHandler;
import dev.alphaserpentis.coffeecore.handler.api.discord.servers.ServerDataHandler;
import io.reactivex.rxjava3.annotations.Experimental;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Builder class for CoffeeCore
 */
public class CoffeeCoreBuilder {
    enum JDABuilderConfiguration {
        NONE,
        LIGHT,
        DEFAULT
    }

    protected BotSettings settings;
    protected Constructor<?> serverDataHandler = null;
    protected ChunkingFilter chunkingFilter = ChunkingFilter.ALL;
    protected Collection<CacheFlag> enabledCacheFlags = new ArrayList<>();
    protected Collection<CacheFlag> disabledCacheFlags = new ArrayList<>() {
        {
            add(CacheFlag.MEMBER_OVERRIDES);
            add(CacheFlag.VOICE_STATE);
        }
    };
    protected Collection<GatewayIntent> enabledGatewayIntents = new ArrayList<>() {
        {
            add(GatewayIntent.GUILD_MEMBERS);
        }
    };
    protected Collection<GatewayIntent> disabledGatewayIntents = null;
    protected boolean isDetailedConfiguration = false;
    protected JDABuilderConfiguration jdaBuilderConfiguration = JDABuilderConfiguration.DEFAULT; // TODO: Allow developers to determine how they want to configure the JDABuilder

    /**
     * Builds a {@link CoffeeCore} instance and automatically initializes {@link ServerDataHandler}.
     * <p>Coffee Core will shut down if the bot is misconfigured within Discord (e.g., invalid token, invalid permissions).
     * @param token The Discord bot token.
     * @return {@link CoffeeCore}.
     * @throws IOException If the bot fails to read or write to the server data file.
     */
    @NonNull
    public CoffeeCore build(
            @NonNull String token
    ) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(isDetailedConfiguration) {
            return new CoffeeCore(
                    token,
                    settings,
                    serverDataHandler,
                    chunkingFilter,
                    enabledCacheFlags,
                    disabledCacheFlags,
                    enabledGatewayIntents,
                    disabledGatewayIntents
            );
        } else {
            return new CoffeeCore(token, settings, serverDataHandler);
        }
    }

    /**
     * Sets the bot's settings.
     * @param settings The bot's settings.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @NonNull
    public CoffeeCoreBuilder setSettings(@NonNull BotSettings settings) {
        this.settings = settings;
        return this;
    }

    /**
     * Sets the {@link AbstractServerDataHandler} constructor.
     * @param serverDataHandler The {@link AbstractServerDataHandler} constructor.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @Experimental
    @NonNull
    public CoffeeCoreBuilder setServerDataHandler(@NonNull Constructor<?> serverDataHandler) {
        this.serverDataHandler = serverDataHandler;
        return this;
    }

    /**
     * Sets the {@link ChunkingFilter}. This will cause the builder to use the detailed configuration.
     * @param chunkingFilter The chunking filter.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @NonNull
    public CoffeeCoreBuilder setChunkingFilter(@NonNull ChunkingFilter chunkingFilter) {
        this.chunkingFilter = chunkingFilter;
        isDetailedConfiguration = true;
        return this;
    }

    /**
     * Sets the enabled {@link CacheFlag}. This will cause the builder to use the detailed configuration.
     * @param enabledCacheFlags The enabled cache flags.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @NonNull
    public CoffeeCoreBuilder setEnabledCacheFlags(@NonNull Collection<CacheFlag> enabledCacheFlags) {
        this.enabledCacheFlags = enabledCacheFlags;
        isDetailedConfiguration = true;
        return this;
    }

    /**
     * Sets the disabled {@link CacheFlag}. This will cause the builder to use the detailed configuration.
     * @param disabledCacheFlags The disabled cache flags.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @NonNull
    public CoffeeCoreBuilder setDisabledCacheFlags(@NonNull Collection<CacheFlag> disabledCacheFlags) {
        this.disabledCacheFlags = disabledCacheFlags;
        isDetailedConfiguration = true;
        return this;
    }

    /**
     * Sets the enabled {@link GatewayIntent}. This will cause the builder to use the detailed configuration.
     * @param enabledGatewayIntents The enabled gateway intents.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @NonNull
    public CoffeeCoreBuilder setEnabledGatewayIntents(@NonNull Collection<GatewayIntent> enabledGatewayIntents) {
        this.enabledGatewayIntents = enabledGatewayIntents;
        isDetailedConfiguration = true;
        return this;
    }

    /**
     * Sets the disabled {@link GatewayIntent}. This will cause the builder to use the detailed configuration.
     * @param disabledGatewayIntents The disabled gateway intents.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @NonNull
    public CoffeeCoreBuilder setDisabledGatewayIntents(@NonNull Collection<GatewayIntent> disabledGatewayIntents) {
        this.disabledGatewayIntents = disabledGatewayIntents;
        isDetailedConfiguration = true;
        return this;
    }
}
