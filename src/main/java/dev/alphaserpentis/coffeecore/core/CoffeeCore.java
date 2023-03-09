package dev.alphaserpentis.coffeecore.core;

import dev.alphaserpentis.coffeecore.commands.defaultcommands.About;
import dev.alphaserpentis.coffeecore.commands.defaultcommands.Help;
import dev.alphaserpentis.coffeecore.commands.defaultcommands.Settings;
import dev.alphaserpentis.coffeecore.data.bot.BotSettings;
import dev.alphaserpentis.coffeecore.handler.api.discord.commands.CommandsHandler;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import dev.alphaserpentis.coffeecore.commands.BotCommand;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;

/**
 * The core of Coffee Core. This class is responsible for initializing the bot and handling commands.
 */
public class CoffeeCore {

    /**
     * The {@link JDA} instance.
     */
    public static JDA api;
    /**
     * The {@link BotSettings} instance.
     */
    public final BotSettings settings;

    public CoffeeCore(
            @NonNull String token,
            @NonNull BotSettings settings
    ) {
        this.settings = settings;
        api = JDABuilder.createDefault(token)
                .setChunkingFilter(ChunkingFilter.ALL)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI, CacheFlag.VOICE_STATE)
                .disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
                .build();

        try {
            api.awaitReady();
        } catch (IllegalStateException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        api.addEventListener(new CommandsHandler());
    }

    public CoffeeCore(
            @NonNull String token,
            @NonNull BotSettings settings,
            @NonNull ChunkingFilter chunkingFilter,
            @NonNull Collection<CacheFlag> disabledCache,
            @NonNull Collection<CacheFlag> enabledCache,
            @NonNull Collection<GatewayIntent> enabledIntents,
            @NonNull Collection<GatewayIntent> disabledIntents
    ) {
        this.settings = settings;
        api = JDABuilder.createDefault(token)
                .setChunkingFilter(chunkingFilter)
                .disableCache(disabledCache)
                .enableCache(enabledCache)
                .disableIntents(disabledIntents)
                .enableIntents(enabledIntents)
                .build();

        try {
            api.awaitReady();
        } catch (IllegalStateException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        api.addEventListener(new CommandsHandler());
    }

    /**
     * Register a command or commands to the bot. This method will immediately push the commands to the bot, so it is best to
     * register all commands instead of registering them one by one.
     * @param command The command or commands to register.
     */
    public void registerCommands(@NonNull BotCommand<?>... command) {
        HashMap<String, BotCommand<?>> commands = new HashMap<>();
        for(BotCommand<?> cmd: command) {
            commands.put(cmd.getName(), cmd);
        }

        if(settings.registerDefaultCommands) {
            commands.put("settings", new Settings());
            commands.put("help", new Help());
            commands.put("about", new About());
        }

        CommandsHandler.registerCommands(commands, settings.updateCommandsAtLaunch);
    }

    /**
     * Get the bot owner's Discord ID
     * @return The bot owner's Discord ID
     */
    public long getBotOwnerId() {
        return settings.botOwnerId;
    }

    /**
     * Shutdown the bot
     * @param duration The duration to wait for the bot to shut down
     * @throws InterruptedException If the bot fails to shut down within the specified duration
     */
    public void shutdown(@NonNull Duration duration) throws InterruptedException {
        api.shutdown();
        api.awaitShutdown(duration);
    }
}