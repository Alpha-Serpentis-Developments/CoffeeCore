package dev.alphaserpentis.coffeecore.core;

import com.google.gson.reflect.TypeToken;
import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.commands.defaultcommands.About;
import dev.alphaserpentis.coffeecore.commands.defaultcommands.Help;
import dev.alphaserpentis.coffeecore.commands.defaultcommands.Settings;
import dev.alphaserpentis.coffeecore.commands.defaultcommands.Shutdown;
import dev.alphaserpentis.coffeecore.data.bot.BotSettings;
import dev.alphaserpentis.coffeecore.handler.api.discord.commands.CommandsHandler;
import dev.alphaserpentis.coffeecore.handler.api.discord.servers.AbstractServerDataHandler;
import dev.alphaserpentis.coffeecore.handler.api.discord.servers.ServerDataHandler;
import dev.alphaserpentis.coffeecore.serialization.ServerDataDeserializer;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.JDA;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.Executors;

/**
 * The core of Coffee Core. This class is responsible for initializing the bot and handling commands.
 */
public class CoffeeCore {

    /**
     * The {@link JDA} instance.
     */
    protected final JDA jda;
    /**
     * The {@link ServerDataHandler} instance.
     */
    protected AbstractServerDataHandler<?> serverDataHandler;
    /**
     * The {@link CommandsHandler} instance.
     */
    protected final CommandsHandler commandsHandler;
    /**
     * The {@link BotSettings} instance.
     */
    protected final BotSettings settings;

    public CoffeeCore(
            @NonNull BotSettings settings,
            @NonNull JDA jda
    ) {
        this.settings = settings;
        this.jda = jda;

        try {
            jda.awaitReady();
            Path path = Path.of(settings.serverDataPath);
            this.serverDataHandler = new ServerDataHandler<>(
                    path,
                    new TypeToken<>() {},
                    new ServerDataDeserializer<>()
            );
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        commandsHandler = new CommandsHandler(this, Executors.newCachedThreadPool());

        this.jda.addEventListener(commandsHandler);
        this.jda.addEventListener(this.serverDataHandler);
    }

    public CoffeeCore(
            @NonNull BotSettings settings,
            @NonNull JDA jda,
            @Nullable Constructor<?> serverDataHandlerConstructor,
            @Nullable Object... serverDataHandlerParameters
    ) {
        this.settings = settings;
        this.jda = jda;

        try {
            jda.awaitReady();

            if(serverDataHandlerConstructor == null) {
                Path path = Path.of(settings.serverDataPath);
                this.serverDataHandler = new ServerDataHandler<>(
                        path,
                        new TypeToken<>() {
                        },
                        new ServerDataDeserializer<>()
                );
            } else {
                if(serverDataHandlerParameters == null) {
                    throw new IllegalArgumentException("serverDataHandlerParameters cannot be null if serverDataHandlerConstructor is not null");
                }

                this.serverDataHandler = (AbstractServerDataHandler<?>) serverDataHandlerConstructor.newInstance(
                        serverDataHandlerParameters
                );
            }

            serverDataHandler.init(getJda());
        } catch (IllegalStateException | InterruptedException | IOException | InvocationTargetException |
                 InstantiationException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
            System.exit(1);
        }
        commandsHandler = new CommandsHandler(this, Executors.newCachedThreadPool());

        jda.addEventListener(commandsHandler);
        jda.addEventListener(this.serverDataHandler);
    }

    /**
     * Get the {@link JDA} instance
     * @return {@link JDA}
     */
    @NonNull
    public JDA getJda() {
        return jda;
    }

    /**
     * Get the {@link AbstractServerDataHandler} instance
     * @return {@link AbstractServerDataHandler}
     */
    @NonNull
    public AbstractServerDataHandler<?> getServerDataHandler() {
        return serverDataHandler;
    }

    /**
     * Get the {@link CommandsHandler} instance
     * @return {@link CommandsHandler}
     */
    @NonNull
    public CommandsHandler getCommandsHandler() {
        return commandsHandler;
    }

    /**
     * Get the {@link BotSettings} instance
     * @return {@link BotSettings}
     */
    @NonNull
    public BotSettings getSettings() {
        return settings;
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
        jda.shutdown();
        jda.awaitShutdown(duration);
    }

    /**
     * Register a {@link BotCommand} or commands to the bot. This method will immediately push the commands to the bot, so it is best to
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
            commands.put("shutdown", new Shutdown());
        }

        commandsHandler.registerCommands(commands, settings.updateCommandsAtLaunch);
    }
}