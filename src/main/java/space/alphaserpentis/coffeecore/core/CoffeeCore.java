package space.alphaserpentis.coffeecore.core;

import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import space.alphaserpentis.coffeecore.commands.BotCommand;
import space.alphaserpentis.coffeecore.data.bot.BotSettings;
import space.alphaserpentis.coffeecore.handler.api.discord.commands.CommandsHandler;

import java.util.Collection;
import java.util.HashMap;

public class CoffeeCore {

    public static JDA api;
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

        CommandsHandler.registerCommands(commands, settings.updateCommandsAtLaunch);
    }

    public long getBotOwnerId() {
        return settings.botOwnerId;
    }
}