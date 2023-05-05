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
import dev.alphaserpentis.coffeecore.helper.ContainerHelper;
import dev.alphaserpentis.coffeecore.serialization.ServerDataDeserializer;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executors;

/**
 * The core of Coffee Core. This class is responsible for initializing the bot and handling commands.
 */
public class CoffeeCore {

    /**
     * The {@link JDA} instance. Mutually exclusive with {@link #shardManager}.
     */
    protected JDA jda = null;
    /**
     * The {@link ShardManager} instance. Mutually exclusive with {@link #jda}.
     */
    protected ShardManager shardManager = null;
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
            @NonNull IGuildChannelContainer container
    ) {
        this.settings = settings;

        try {
            determineAndSetContainer(container);

            ContainerHelper containerHelper = new ContainerHelper(container);
            Path path = Path.of(settings.serverDataPath);
            serverDataHandler = new ServerDataHandler<>(
                    path,
                    new TypeToken<>() {},
                    new ServerDataDeserializer<>()
            );

            serverDataHandler.init(containerHelper);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        commandsHandler = new CommandsHandler(this, Executors.newCachedThreadPool());

        addEventListenersToContainer(this.commandsHandler, this.serverDataHandler);
    }

    public CoffeeCore(
            @NonNull BotSettings settings,
            @NonNull IGuildChannelContainer container,
            @Nullable AbstractServerDataHandler<?> serverDataHandler,
            @Nullable CommandsHandler commandsHandler
    ) {
        this.settings = settings;

        try {
            determineAndSetContainer(container);
            ContainerHelper containerHelper = new ContainerHelper(container);

            if(serverDataHandler == null) {
                Path path = Path.of(settings.serverDataPath);
                this.serverDataHandler = new ServerDataHandler<>(
                        path,
                        new TypeToken<>() {
                        },
                        new ServerDataDeserializer<>()
                );
            } else {
                this.serverDataHandler = serverDataHandler;
            }

            this.serverDataHandler.init(containerHelper);
        } catch (IllegalStateException | InterruptedException | IOException | IllegalArgumentException e) {
            e.printStackTrace();
            System.exit(1);
        }
        this.commandsHandler = Objects.requireNonNullElseGet(commandsHandler, () -> new CommandsHandler(this, Executors.newCachedThreadPool()));

        addEventListenersToContainer(this.commandsHandler, this.serverDataHandler);
    }

    /**
     * Get the {@link JDA} instance
     * @return {@link JDA} if bot is not sharded, {@code null} otherwise
     */
    @Nullable
    public JDA getJda() {
        return jda;
    }

    /**
     * Get the {@link ShardManager} instance
     * @return {@link ShardManager} if bot is sharded, {@code null} otherwise
     */
    @Nullable
    public ShardManager getShardManager() {
        return shardManager;
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
     * Get the active container. This method will return either {@link JDA} or {@link ShardManager}, whichever is active.
     * @return The active container.
     * @throws IllegalStateException If the container has not been determined yet. This may be caused by calling it too early.
     */
    @NonNull
    public IGuildChannelContainer getActiveContainer() {
        if(jda != null) {
            return jda;
        } else if(shardManager != null) {
            return shardManager;
        } else {
            throw new IllegalStateException("The container has not been determined yet.");
        }
    }

    /**
     * Check if the bot is sharded
     * @return {@code true} if the bot is sharded, {@code false} otherwise
     */
    public boolean isSharded() {
        return shardManager != null;
    }

    /**
     * Shutdown the bot
     * @param duration The duration to wait for the bot to shut down
     * @throws InterruptedException If the bot fails to shut down within the specified duration
     */
    public void shutdown(@NonNull Duration duration) throws InterruptedException {
        IGuildChannelContainer container = getActiveContainer();
        if(container instanceof JDA) {
            ((JDA) container).shutdown();
            ((JDA) container).awaitShutdown();
        } else if(container instanceof ShardManager) {
            ((ShardManager) getActiveContainer()).shutdown();
        }
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

        for(BotCommand<?> cmd: commands.values()) {
            cmd.setCore(this);
        }

        commandsHandler.registerCommands(commands, settings.updateCommandsAtLaunch);
    }

    /**
     * Determine the container to use for the bot. This method is used to determine whether to use a {@link JDA} instance or a
     * {@link ShardManager} instance. This method will also wait for the bot to be ready.
     * @param container The container to use for the bot.
     */
    public void determineAndSetContainer(@NonNull IGuildChannelContainer container) throws InterruptedException {
        if(jda != null || shardManager != null)
            throw new IllegalStateException("The container has already been determined.");

        if(container instanceof JDA) {
            jda = (JDA) container;
            jda.awaitReady();
        } else if(container instanceof ShardManager) {
            shardManager = (ShardManager) container;
            for(JDA jda: shardManager.getShardCache()) {
                jda.awaitReady();
            }
        } else {
            throw new IllegalArgumentException("The container must be either a JDA instance or a ShardManager instance.");
        }
    }

    /**
     * Add event listeners to the container. This method will add the specified listeners to the container. The container can be either
     * {@link JDA} or {@link ShardManager}.
     * @param listeners The listeners to add to the container.
     */
    public void addEventListenersToContainer(@NonNull Object... listeners) {
        IGuildChannelContainer container = getActiveContainer();
        if(container instanceof JDA) {
            ((JDA) container).addEventListener(listeners);
        } else if(container instanceof ShardManager) {
            ((ShardManager) container).addEventListener(listeners);
        }
    }
}