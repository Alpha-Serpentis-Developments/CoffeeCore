package dev.alphaserpentis.coffeecore.core;

import dev.alphaserpentis.coffeecore.data.bot.BotSettings;
import dev.alphaserpentis.coffeecore.handler.api.discord.servers.AbstractServerDataHandler;
import dev.alphaserpentis.coffeecore.handler.api.discord.servers.ServerDataHandler;
import io.reactivex.rxjava3.annotations.Experimental;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Builder class for {@link CoffeeCore}.
 */
public class CoffeeCoreBuilder {

    /**
     * Enum used to determine how the {@link JDABuilder} is configured initially
     *
     * @see JDABuilder#create(Collection)
     * @see JDABuilder#createDefault(String)
     * @see JDABuilder#createLight(String)
     */
    enum JDABuilderConfiguration {
        /**
         * Creates a {@link JDABuilder} with its predefined configuration.
         * @see JDABuilder#create(String, Collection)
         */
        NONE,
        /**
         * Creates a {@link JDABuilder} with a light configuration.
         * @see JDABuilder#createLight(String)
         */
        LIGHT,
        /**
         * Creates a {@link JDABuilder} with JDA's recommended default configuration.
         * <p>These may change by JDA over time.
         * @see JDABuilder#createDefault(String)
         */
        DEFAULT
    }

    protected BotSettings settings = null;
    protected Constructor<?> serverDataHandler = null;
    protected Object[] serverDataHandlerParameters = null;
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
    protected Collection<GatewayIntent> disabledGatewayIntents = new ArrayList<>();
    protected JDABuilderConfiguration jdaBuilderConfiguration = JDABuilderConfiguration.DEFAULT;
    protected MemberCachePolicy memberCachePolicy = MemberCachePolicy.NONE;

    /**
     * Builds a {@link CoffeeCore} instance and automatically initializes {@link ServerDataHandler}.
     * Coffee Core will shut down if the bot is misconfigured within Discord (e.g., invalid token, invalid permissions).
     * <p><b>Be aware that {@link CoffeeCoreBuilder#disabledCacheFlags}, {@link CoffeeCoreBuilder#enabledGatewayIntents}, and {@link CoffeeCoreBuilder#jdaBuilderConfiguration} contain default values.</b>
     * @param token The Discord bot token.
     * @return {@link CoffeeCore}.
     * @throws IOException If the bot fails to read or write to the server data file.
     */
    @NonNull
    public CoffeeCore build(
            @NonNull String token
    ) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(serverDataHandler != null) {
            return new CoffeeCore(
                    settings,
                    createJDABuilderInstance(token).build(),
                    serverDataHandler,
                    serverDataHandlerParameters
            );
        } else {
            return new CoffeeCore(settings, createJDABuilderInstance(token).build());
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
     * Sets the {@link AbstractServerDataHandler} constructor parameters.
     * @param serverDataHandlerParameters The {@link AbstractServerDataHandler} constructor parameters.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @Experimental
    @NonNull
    public CoffeeCoreBuilder setServerDataHandlerParameters(@NonNull Object... serverDataHandlerParameters) {
        this.serverDataHandlerParameters = serverDataHandlerParameters;
        return this;
    }

    /**
     * Sets the {@link ChunkingFilter}.
     * @param chunkingFilter The chunking filter.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     * @see JDABuilder#setChunkingFilter(ChunkingFilter)
     */
    @NonNull
    public CoffeeCoreBuilder setChunkingFilter(@NonNull ChunkingFilter chunkingFilter) {
        this.chunkingFilter = chunkingFilter;
        return this;
    }

    /**
     * Sets the enabled {@link CacheFlag}.
     * @param enabledCacheFlags The enabled cache flags.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     * @see JDABuilder#enableCache(Collection)
     */
    @NonNull
    public CoffeeCoreBuilder setEnabledCacheFlags(@NonNull Collection<CacheFlag> enabledCacheFlags) {
        this.enabledCacheFlags = enabledCacheFlags;
        return this;
    }

    /**
     * Sets the disabled {@link CacheFlag}.
     * @param disabledCacheFlags The disabled cache flags.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     * @see JDABuilder#disableCache(Collection)
     */
    @NonNull
    public CoffeeCoreBuilder setDisabledCacheFlags(@NonNull Collection<CacheFlag> disabledCacheFlags) {
        this.disabledCacheFlags = disabledCacheFlags;
        return this;
    }

    /**
     * Sets the enabled {@link GatewayIntent}.
     * @param enabledGatewayIntents The enabled gateway intents.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     * @see JDABuilder#enableIntents(Collection)
     */
    @NonNull
    public CoffeeCoreBuilder setEnabledGatewayIntents(@NonNull Collection<GatewayIntent> enabledGatewayIntents) {
        this.enabledGatewayIntents = enabledGatewayIntents;
        return this;
    }

    /**
     * Sets the disabled {@link GatewayIntent}.
     * @param disabledGatewayIntents The disabled gateway intents.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     * @see JDABuilder#disableIntents(Collection)
     */
    @NonNull
    public CoffeeCoreBuilder setDisabledGatewayIntents(@NonNull Collection<GatewayIntent> disabledGatewayIntents) {
        this.disabledGatewayIntents = disabledGatewayIntents;
        return this;
    }

    /**
     * Sets the {@link MemberCachePolicy}.
     * @param memberCachePolicy The member cache policy.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     * @see JDABuilder#setMemberCachePolicy(MemberCachePolicy)
     */
    @NonNull
    public CoffeeCoreBuilder setMemberCachePolicy(@NonNull MemberCachePolicy memberCachePolicy) {
        this.memberCachePolicy = memberCachePolicy;
        return this;
    }

    /**
     * Creates and configures a {@link JDABuilder} based upon this class's configuration.
     * @param token The Discord bot token.
     * @return {@link JDABuilder} object
     * @see JDABuilder#create(String, Collection)
     * @see JDABuilder#createLight(String)
     * @see JDABuilder#createDefault(String)
     */
    @NonNull
    private JDABuilder createJDABuilderInstance(@NonNull String token) {
        JDABuilder jdaBuilder = null;

        switch(jdaBuilderConfiguration) {
            case NONE -> jdaBuilder = JDABuilder.create(token, enabledGatewayIntents);
            case LIGHT -> jdaBuilder = JDABuilder.createLight(token);
            case DEFAULT -> jdaBuilder = JDABuilder.createDefault(token);
        }

        return jdaBuilder
                .setChunkingFilter(chunkingFilter)
                .enableCache(enabledCacheFlags)
                .disableCache(disabledCacheFlags)
                .enableIntents(enabledGatewayIntents)
                .disableIntents(disabledGatewayIntents)
                .setMemberCachePolicy(memberCachePolicy);
    }
}
