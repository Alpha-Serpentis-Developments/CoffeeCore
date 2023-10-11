package dev.alphaserpentis.coffeecore.handler.api.discord.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alphaserpentis.coffeecore.core.CoffeeCore;
import dev.alphaserpentis.coffeecore.data.server.ServerData;
import dev.alphaserpentis.coffeecore.helper.ContainerHelper;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The abstract class that handles {@link ServerData}.
 * @param <T> The type of {@link ServerData} to handle.
 */
public abstract class AbstractServerDataHandler<T extends ServerData> extends ListenerAdapter {
    /**
     * Path to the server data file.
     */
    private final Path pathToFile;
    /**
     * The {@link CoffeeCore} instance.
     */
    private CoffeeCore core;
    /**
     * The {@link Future} of the scheduled executor.
     */
    protected ScheduledFuture<?> scheduledFuture;
    /**
     * Executor to asynchronously update the server data file.
     */
    protected ScheduledExecutorService executor;
    /**
     * The last time the server data file was updated.
     */
    protected long lastUpdate = 0;
    /**
     * The mapping of server IDs to {@link ServerData}.
     */
    public Map<Long, T> serverDataHashMap = new HashMap<>();

    public AbstractServerDataHandler(
            @NonNull Path path
    ) {
        pathToFile = path;
    }

    /**
     * Initializes the server data handler.
     * <p>
     * This will assign a default executor of {@link Executors#newSingleThreadScheduledExecutor()}.
     * @param container The {@link ContainerHelper} instance to get the servers from.
     * @throws IOException If the bot fails to write to the server data file.
     */
    public void init(@NonNull ContainerHelper container, @NonNull CoffeeCore core) throws IOException {
        init(container, core, Executors.newSingleThreadScheduledExecutor());
    }

    /**
     * Initializes the server data handler.
     * @param container The {@link ContainerHelper} instance to get the servers from.
     * @param core The {@link CoffeeCore} instance.
     * @param executor The executor to asynchronously update the server data file.
     */
    public void init(
            @NonNull ContainerHelper container,
            @NonNull CoffeeCore core,
            @NonNull ScheduledExecutorService executor
    ) {
        this.executor = executor;
        this.core = core;

        // Check the current servers
        if(serverDataHashMap == null)
            serverDataHashMap = new HashMap<>();

        ArrayList<Long> serversActuallyJoined = new ArrayList<>();

        for(Guild g: container.getGuilds()) {
            if(!serverDataHashMap.containsKey(g.getIdLong())) {
                serverDataHashMap.put(g.getIdLong(), createNewServerData());
            }
            serversActuallyJoined.add(g.getIdLong());
        }

        // Check if the bot left a server but data wasn't cleared
        serverDataHashMap.keySet().removeIf(id -> !serversActuallyJoined.contains(id));

        updateServerData();
    }

    /**
     * Gets the {@link ServerData} for the specified server.
     * @param guildId The ID of the server.
     * @return The {@link ServerData} for the specified server or {@code null} if the server is not in the mapping.
     */
    @Nullable
    public T getServerData(long guildId) {
        return serverDataHashMap.get(guildId);
    }

    /**
     * Gets the {@link CoffeeCore} instance.
     * @return {@link CoffeeCore}
     */
    @NonNull
    public CoffeeCore getCore() {
        return core;
    }

    /**
     * Gets the {@link Path} to the server data file.
     * @return {@link Path}
     */
    @NonNull
    public Path getPathToFile() {
        return pathToFile;
    }

    @Nullable
    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    @NonNull
    protected ScheduledExecutorService getExecutor() {
        return executor;
    }

    /**
     * Gets the last time the server data file was updated.
     * @return UNIX time in seconds of the last update.
     */
    protected long getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Tells the executor to update the server data file after some time.
     */
    public void updateServerData() {
        long currentTime = System.currentTimeMillis() / 1000;
        long timeBetweenUpdate = currentTime - lastUpdate;

        if(timeBetweenUpdate < 60 && (scheduledFuture != null && !scheduledFuture.cancel(false))) {
            return;
        }

        scheduledFuture = executor.schedule(
                () -> {
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .create();

                    writeToJSON(gson, serverDataHashMap);
                },
                10,
                TimeUnit.SECONDS
        );
    }

    /**
     * Writes the specified data to the server data file.
     * @param gson The {@link Gson} instance to use to write the data.
     * @param data The data to write.
     */
    protected void writeToJSON(@NonNull Gson gson, @NonNull Object data) {
        try {
            Writer writer = Files.newBufferedWriter(pathToFile);

            gson.toJson(data, writer);
            writer.close();

            lastUpdate = System.currentTimeMillis() / 1000;
        } catch (IOException e) {
            handleServerDataException(e);
        }
    }

    /**
     * Creates a new instance of {@link ServerData}.
     * @return A new instance of {@link ServerData}.
     */
    protected abstract T createNewServerData();

    /**
     * Handle an exception thrown when updating the server data.
     * @param e The exception thrown.
     */
    protected abstract void handleServerDataException(@NonNull Exception e);
}
