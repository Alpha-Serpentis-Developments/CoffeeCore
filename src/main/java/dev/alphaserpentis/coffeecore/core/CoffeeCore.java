package dev.alphaserpentis.coffeecore.core;

import com.google.gson.reflect.TypeToken;
import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.commands.defaultcommands.About;
import dev.alphaserpentis.coffeecore.commands.defaultcommands.Help;
import dev.alphaserpentis.coffeecore.commands.defaultcommands.Restart;
import dev.alphaserpentis.coffeecore.commands.defaultcommands.Settings;
import dev.alphaserpentis.coffeecore.commands.defaultcommands.Shutdown;
import dev.alphaserpentis.coffeecore.data.bot.AboutInformation;
import dev.alphaserpentis.coffeecore.data.bot.BotSettings;
import dev.alphaserpentis.coffeecore.handler.api.discord.commands.CommandsHandler;
import dev.alphaserpentis.coffeecore.handler.api.discord.servers.AbstractServerDataHandler;
import dev.alphaserpentis.coffeecore.handler.api.discord.servers.ServerDataHandler;
import dev.alphaserpentis.coffeecore.helper.ContainerHelper;
import dev.alphaserpentis.coffeecore.serialization.ServerDataDeserializer;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executors;

/**
 * The core of Coffee Core. This class is responsible for initializing the bot, handling commands, and containing various components.
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
        this(settings, container, null, null);
    }

    public CoffeeCore(
            @NonNull BotSettings settings,
            @NonNull IGuildChannelContainer container,
            @Nullable AbstractServerDataHandler<?> serverDataHandler
    ) {
        this(settings, container, serverDataHandler, null);
    }

    public CoffeeCore(
            @NonNull BotSettings settings,
            @NonNull IGuildChannelContainer container,
            @Nullable CommandsHandler commandsHandler
    ) {
        this(settings, container, null, commandsHandler);
    }

    public CoffeeCore(
            @NonNull BotSettings settings,
            @NonNull IGuildChannelContainer container,
            @Nullable AbstractServerDataHandler<?> serverDataHandler,
            @Nullable CommandsHandler commandsHandler,
            @NonNull Object... additionalListeners
    ) {
        this.settings = settings;

        try {
            determineAndSetContainer(container);
            ContainerHelper containerHelper = new ContainerHelper(container);

            if(serverDataHandler == null) {
                Path path = Path.of(settings.getServerDataPath());

                this.serverDataHandler = new ServerDataHandler<>(
                        path,
                        new TypeToken<>() {
                        },
                        new ServerDataDeserializer<>()
                );
            } else {
                this.serverDataHandler = serverDataHandler;
            }

            this.serverDataHandler.init(containerHelper, this);
        } catch (IllegalStateException | InterruptedException | IOException | IllegalArgumentException e) {
            e.printStackTrace();
            System.exit(1);
        }
        this.commandsHandler = Objects.requireNonNullElseGet(
                commandsHandler, () -> new CommandsHandler(Executors.newCachedThreadPool())
        );
        this.commandsHandler.setCore(this);

        addEventListenersToContainer(this.commandsHandler, this.serverDataHandler, additionalListeners);
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
     * Get the bot's {@link dev.alphaserpentis.coffeecore.data.bot.AboutInformation} instance
     * @return {@link dev.alphaserpentis.coffeecore.data.bot.AboutInformation}
     */
    @Nullable
    public AboutInformation getAboutInformation() {
        return settings.getAboutInformation();
    }

    /**
     * Get the bot owner's Discord ID
     * @return The bot owner's Discord ID
     */
    public long getBotOwnerId() {
        return settings.getBotOwnerId();
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
     * Get the bot's {@link SelfUser} instance
     * @return {@link SelfUser}
     * @throws IllegalStateException If the container has not been determined yet.
     */
    @NonNull
    public SelfUser getSelfUser() {
        IGuildChannelContainer container = getActiveContainer();

        if(container instanceof JDA j)
            return j.getSelfUser();
        else
            return ((ShardManager) container).getShards().get(0).getSelfUser();
    }

    /**
     * Shutdown the bot. This blocks the thread.
     * @param duration The duration to wait for the bot to shut down
     * @throws InterruptedException If the bot fails to shut down within the specified duration
     */
    public void shutdown(@NonNull Duration duration) throws InterruptedException {
        IGuildChannelContainer container = getActiveContainer();

        if(container instanceof JDA j) {
            j.shutdown();

            if(j.awaitShutdown(duration)) {
                j.shutdownNow();
                j.awaitShutdown();
            }
        } else if(container instanceof ShardManager sm) {
            sm.shutdown();
        }
    }

    /**
     * Restarts the bot if it is sharded. This method will throw an {@link UnsupportedOperationException} if the bot is
     * not sharded.
     * @throws UnsupportedOperationException If the bot is not sharded.
     * @see ShardManager#restart()
     */
    public void restart() {
        if(isSharded() && getShardManager() != null) {
            getShardManager().restart();
        } else {
            throw new UnsupportedOperationException("Restarting is not supported for non-sharded bots.");
        }
    }

    /**
     * Restarts the specified shard if the bot is sharded.
     * This method will throw an {@link UnsupportedOperationException} if the bot is not sharded.
     * @param shardId The shard ID to restart
     * @throws UnsupportedOperationException If the bot is not sharded.
     * @see ShardManager#restart(int)
     */
    public void restart(int shardId) {
        if(isSharded() && getShardManager() != null) {
            getShardManager().restart(shardId);
        } else {
            throw new UnsupportedOperationException("Restarting is not supported for non-sharded bots.");
        }
    }

    /**
     * Register a {@link BotCommand} or commands to the bot. This method will immediately push the commands to the bot,
     * so it is best to register all commands instead of registering them one by one.
     * <p>
     * If a command with the same name is registered, an error will be printed out, but otherwise replace the command.
     * @param command The command or commands to register.
     */
    public void registerCommands(@NonNull BotCommand<?, ?>... command) {
        HashMap<String, BotCommand<?, ?>> commands = new HashMap<>();

        if(settings.isRegisterDefaultCommands()) {
            commands.put("settings", new Settings());
            commands.put("help", new Help());
            commands.put("about", new About());
            commands.put("shutdown", new Shutdown());
            commands.put("restart", new Restart());
        }

        for(BotCommand<?, ?> cmd: command) {
            if(commands.get(cmd.getName()) != null) {
                System.err.println("Duplicate command name: " + cmd.getName());
            }

            commands.put(cmd.getName(), cmd);
        }

        for(BotCommand<?, ?> cmd: commands.values()) {
            cmd.setCore(this);
        }

        commandsHandler.registerCommands(commands, settings.isUpdateCommandsAtLaunch());
    }

    /**
     * Determine the container to use for the bot. This method is used to determine whether to use a {@link JDA}
     * instance or a {@link ShardManager} instance. This method will also wait for the bot to be ready. Failure to
     * provide either class will throw an
     * {@link IllegalArgumentException}.
     * @param container The container to use for the bot.
     * @throws InterruptedException If the bot is interrupted while waiting for the container to be ready.
     * @throws IllegalStateException If the container has already been determined.
     * @throws IllegalArgumentException If the container is not a {@link JDA} instance or a {@link ShardManager}
     */
    public void determineAndSetContainer(@NonNull IGuildChannelContainer container) throws InterruptedException {
        if(jda != null || shardManager != null)
            throw new IllegalStateException("The container has already been determined.");

        if(container instanceof JDA j) {
            jda = j;
            jda.awaitReady();
        } else if(container instanceof ShardManager sm) {
            shardManager = sm;

            for(JDA jda: shardManager.getShards()) {
                jda.awaitReady();
            }
        } else {
            throw new IllegalArgumentException("The container must either be a JDA or ShardManager instance.");
        }
    }

    /**
     * Add event listeners to the container. This method will add the specified listeners to the container. The
     * container can be either {@link JDA} or {@link ShardManager}.
     * @param listeners The listeners to add to the container.
     */
    public void addEventListenersToContainer(@NonNull Object... listeners) {
        IGuildChannelContainer container = getActiveContainer();

        if(container instanceof JDA j) {
            j.addEventListener(listeners);
        } else if(container instanceof ShardManager sm) {
            sm.addEventListener(listeners);
        }
    }
}