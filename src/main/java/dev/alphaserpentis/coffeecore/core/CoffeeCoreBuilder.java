package dev.alphaserpentis.coffeecore.core;

import dev.alphaserpentis.coffeecore.data.bot.BotSettings;
import dev.alphaserpentis.coffeecore.handler.api.discord.commands.CommandsHandler;
import dev.alphaserpentis.coffeecore.handler.api.discord.entities.AbstractDataHandler;
import dev.alphaserpentis.coffeecore.helper.BuilderHelper;
import io.reactivex.rxjava3.annotations.Experimental;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.Collection;
import java.util.List;

/**
 * Builder class for {@link CoffeeCore}.
 * @param <T> Represents either a {@link JDABuilder} or {@link DefaultShardManagerBuilder}
 */
public class CoffeeCoreBuilder<T> {

    /**
     * Enum used to determine how the {@link JDABuilder} or {@link DefaultShardManagerBuilder} is configured initially.
     * Configuration may change by JDA.
     * @see JDABuilder#create(Collection)
     * @see JDABuilder#createDefault(String)
     * @see JDABuilder#createLight(String)
     */
    public enum BuilderConfiguration {
        /**
         * Creates a {@link JDABuilder} or {@link ShardManager} with JDA's low memory profile.
         * @see JDABuilder#create(String, Collection)
         * @see DefaultShardManagerBuilder#create(String, Collection)
         */
        NONE,
        /**
         * Creates a {@link JDABuilder} or {@link ShardManager} with a light configuration.
         * @see JDABuilder#createLight(String)
         * @see DefaultShardManagerBuilder#createLight(String)
         */
        LIGHT,
        /**
         * Creates a {@link JDABuilder} or {@link ShardManager} with JDA's recommended default configuration.
         * <p>These may change by JDA over time.
         * @see JDABuilder#createDefault(String)
         * @see DefaultShardManagerBuilder#createDefault(String)
         */
        DEFAULT
    }

    protected BotSettings settings = null;
    protected AbstractDataHandler<?> dataHandler = null;
    protected CommandsHandler commandsHandler = null;
    protected Object[] additionalListeners = null;
    protected ChunkingFilter chunkingFilter = ChunkingFilter.NONE;
    protected Collection<CacheFlag> enabledCacheFlags = List.of();
    protected Collection<CacheFlag> disabledCacheFlags = List.of(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
    protected Collection<GatewayIntent> enabledGatewayIntents = List.of();
    protected Collection<GatewayIntent> disabledGatewayIntents = List.of();
    protected BuilderConfiguration builderConfiguration = BuilderConfiguration.DEFAULT;
    protected MemberCachePolicy memberCachePolicy = MemberCachePolicy.NONE;
    protected boolean enableSharding = false;
    protected int shardsTotal = -1;

    /**
     * Builds a {@link CoffeeCore} instance with the configured settings. Initialization will begin inside Coffee Core's
     * constructor.
     * Coffee Core will shut down if the bot is misconfigured within Discord (e.g., invalid token, invalid permissions).
     * <p><b>Be aware that {@link CoffeeCoreBuilder#disabledCacheFlags}, {@link CoffeeCoreBuilder#builderConfiguration}, and
     * {@link CoffeeCoreBuilder#memberCachePolicy} contain default values.</b>
     * @param token The Discord bot token.
     * @return {@link CoffeeCore}.
     */
    @NonNull
    public CoffeeCore build(@NonNull String token) {
        return new CoffeeCore(
                settings,
                new BuilderHelper<>(createBuilderInstance(token)).build(),
                dataHandler,
                commandsHandler,
                additionalListeners
        );
    }

    /**
     * Sets the bot's settings.
     * @param settings The bot's settings.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     * @see BotSettings
     */
    @NonNull
    public CoffeeCoreBuilder<?> setSettings(@NonNull BotSettings settings) {
        this.settings = settings;
        return this;
    }

    /**
     * Sets the {@link AbstractDataHandler} for {@link CoffeeCore}.
     * @param dataHandler The {@link AbstractDataHandler}.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @Experimental
    @NonNull
    public CoffeeCoreBuilder<?> setDataHandler(@NonNull AbstractDataHandler<?> dataHandler) {
        this.dataHandler = dataHandler;
        return this;
    }

    /**
     * Sets the {@link CommandsHandler} for {@link CoffeeCore}.
     * @param commandsHandler The {@link CommandsHandler}.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @Experimental
    @NonNull
    public CoffeeCoreBuilder<?> setCommandsHandler(@NonNull CommandsHandler commandsHandler) {
        this.commandsHandler = commandsHandler;
        return this;
    }

    /**
     * Sets the additional listeners for {@link CoffeeCore}.
     * @param additionalListeners The additional listeners.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @NonNull
    public CoffeeCoreBuilder<?> setAdditionalListeners(@NonNull Object... additionalListeners) {
        this.additionalListeners = additionalListeners;
        return this;
    }

    /**
     * Sets the {@link ChunkingFilter}.
     * @param chunkingFilter The chunking filter.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     * @see JDABuilder#setChunkingFilter(ChunkingFilter)
     */
    @NonNull
    public CoffeeCoreBuilder<?> setChunkingFilter(@NonNull ChunkingFilter chunkingFilter) {
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
    public CoffeeCoreBuilder<?> setEnabledCacheFlags(@NonNull Collection<CacheFlag> enabledCacheFlags) {
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
    public CoffeeCoreBuilder<?> setDisabledCacheFlags(@NonNull Collection<CacheFlag> disabledCacheFlags) {
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
    public CoffeeCoreBuilder<?> setEnabledGatewayIntents(@NonNull Collection<GatewayIntent> enabledGatewayIntents) {
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
    public CoffeeCoreBuilder<?> setDisabledGatewayIntents(@NonNull Collection<GatewayIntent> disabledGatewayIntents) {
        this.disabledGatewayIntents = disabledGatewayIntents;
        return this;
    }

    /**
     * Sets the {@link BuilderConfiguration}.
     * @param builderConfiguration The builder configuration.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @NonNull
    public CoffeeCoreBuilder<?> setBuilderConfiguration(@NonNull BuilderConfiguration builderConfiguration) {
        this.builderConfiguration = builderConfiguration;
        return this;
    }

    /**
     * Sets the {@link MemberCachePolicy}.
     * @param memberCachePolicy The member cache policy.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     * @see JDABuilder#setMemberCachePolicy(MemberCachePolicy)
     */
    @NonNull
    public CoffeeCoreBuilder<?> setMemberCachePolicy(@NonNull MemberCachePolicy memberCachePolicy) {
        this.memberCachePolicy = memberCachePolicy;
        return this;
    }

    /**
     * Sets the builder to use the {@link ShardManager} instead of a single {@link JDA} instance.
     * @param enableSharding Whether to enable sharding.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     */
    @NonNull
    public CoffeeCoreBuilder<?> enableSharding(boolean enableSharding) {
        this.enableSharding = enableSharding;
        return this;
    }

    /**
     * Sets the amount of shards to build. You must enable sharding first.
     * @param shardsTotal The amount of shards to build.
     * @return {@link CoffeeCoreBuilder} for method chaining.
     * @see DefaultShardManagerBuilder#setShardsTotal(int)
     */
    @NonNull
    public CoffeeCoreBuilder<?> setShardsTotal(int shardsTotal) {
        this.shardsTotal = shardsTotal;
        return this;
    }

    /**
     * Creates and configures a {@link JDABuilder} based upon this class's configuration.
     * @param token The Discord bot token.
     * @return {@link T} which can be either {@link JDABuilder} or {@link DefaultShardManagerBuilder}.
     * @see JDABuilder#create(String, Collection)
     * @see JDABuilder#createLight(String)
     * @see JDABuilder#createDefault(String)
     * @see DefaultShardManagerBuilder#create(String, Collection)
     * @see DefaultShardManagerBuilder#createLight(String)
     * @see DefaultShardManagerBuilder#createDefault(String)
     */
    @NonNull
    @SuppressWarnings("unchecked")
    private T createBuilderInstance(@NonNull String token) {
        if(enableSharding) {
            DefaultShardManagerBuilder shardManagerBuilder;

            switch(builderConfiguration) {
                case NONE -> shardManagerBuilder = DefaultShardManagerBuilder.create(token, enabledGatewayIntents);
                case LIGHT -> shardManagerBuilder = DefaultShardManagerBuilder.createLight(token);
                case DEFAULT -> shardManagerBuilder = DefaultShardManagerBuilder.createDefault(token);
                default -> throw new IllegalStateException("Unexpected value: " + builderConfiguration);
            }

            return (T) shardManagerBuilder
                    .setChunkingFilter(chunkingFilter)
                    .enableCache(enabledCacheFlags)
                    .disableCache(disabledCacheFlags)
                    .enableIntents(enabledGatewayIntents)
                    .disableIntents(disabledGatewayIntents)
                    .setMemberCachePolicy(memberCachePolicy)
                    .setShardsTotal(shardsTotal);
        } else {
            JDABuilder jdaBuilder;

            switch(builderConfiguration) {
                case NONE -> jdaBuilder = JDABuilder.create(token, enabledGatewayIntents);
                case LIGHT -> jdaBuilder = JDABuilder.createLight(token);
                case DEFAULT -> jdaBuilder = JDABuilder.createDefault(token);
                default -> throw new IllegalStateException("Unexpected value: " + builderConfiguration);
            }

            return (T) jdaBuilder
                    .setChunkingFilter(chunkingFilter)
                    .enableCache(enabledCacheFlags)
                    .disableCache(disabledCacheFlags)
                    .enableIntents(enabledGatewayIntents)
                    .disableIntents(disabledGatewayIntents)
                    .setMemberCachePolicy(memberCachePolicy);
        }
    }
}
