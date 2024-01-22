package dev.alphaserpentis.coffeecore.handler.api.discord.entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alphaserpentis.coffeecore.core.CoffeeCore;
import dev.alphaserpentis.coffeecore.data.entity.EntityData;
import dev.alphaserpentis.coffeecore.data.entity.EntityType;
import dev.alphaserpentis.coffeecore.data.entity.ServerData;
import dev.alphaserpentis.coffeecore.data.entity.UserData;
import dev.alphaserpentis.coffeecore.helper.ContainerHelper;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The abstract class that handles {@link EntityData}.
 * @param <T> The subclass of {@link EntityData} to handle.
 */
public abstract class AbstractDataHandler<T extends EntityData> extends ListenerAdapter {
    /**
     * The {@link Gson} instance to use to write the data.
     */
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    /**
     * Path to the entity data file.
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
     * Executor to asynchronously update the entity data file.
     */
    protected ScheduledExecutorService executor;
    /**
     * The last time the entity data file was updated.
     */
    protected long lastUpdate = 0;
    /**
     * List of valid entity types that the data handler can handle.
     * <p>
     * Unless a different list was passed during instantiation, the list will contain {@link ServerData} ("guild") and
     * {@link UserData} ("user") by default
     */
    protected List<EntityType> entityTypes;
    /**
     * The mapping of entity IDs to {@link EntityData}.
     * <p>
     * Mapping is structured based on a string-based identifier from the entity type to allow for different types of
     * entities to be stored.
     */
    public Map<String, Map<Long, T>> entityDataHashMap = new HashMap<>();

    public AbstractDataHandler(@NonNull Path path) {
        this(path, List.of(new EntityType("guild", ServerData.class), new EntityType("user", UserData.class)));
    }

    public AbstractDataHandler(@NonNull Path path, @NonNull List<EntityType> entityTypes) {
        pathToFile = path;
        this.entityTypes = entityTypes;
    }

    /**
     * Initializes the data handler.
     * <p>
     * This will assign a default executor of {@link Executors#newSingleThreadScheduledExecutor()}.
     * @param container The {@link ContainerHelper} instance to get the entities from.
     * @throws IOException If the bot fails to write to the entity data file.
     */
    public void init(@NonNull ContainerHelper container, @NonNull CoffeeCore core) throws IOException {
        init(container, core, Executors.newSingleThreadScheduledExecutor());
    }

    /**
     * Initializes the data handler.
     * @param container The {@link ContainerHelper} instance to get the entities from.
     * @param core The {@link CoffeeCore} instance.
     * @param executor The executor to asynchronously update the entity data file.
     */
    public void init(
            @NonNull ContainerHelper container,
            @NonNull CoffeeCore core,
            @NonNull ScheduledExecutorService executor
    ) {
        this.executor = executor;
        this.core = core;
        this.entityDataHashMap = Objects.requireNonNullElse(entityDataHashMap, new HashMap<>());

        // Check if the entity structures have been added
        for(EntityType entityType: entityTypes) {
            entityDataHashMap.computeIfAbsent(entityType.getId(), id -> new HashMap<>());
        }

        List<Guild> guilds = container.getGuilds();
        ArrayList<Long> serversActuallyJoined = new ArrayList<>(guilds.size());
        Map<Long, T> guildData = entityDataHashMap.get("guild");

        for(Guild g: guilds) {
            guildData.computeIfAbsent(g.getIdLong(), id -> createNewEntityData("guild"));
            serversActuallyJoined.add(g.getIdLong());
        }

        // Check if the bot left a server but data wasn't cleared
        guildData.keySet().removeIf(id -> !serversActuallyJoined.contains(id));

        updateEntityData();
    }

    /**
     * Gets the {@link EntityData} for the specified entity ID if mapped.
     * @param mapId The identifier to check which mapping to use.
     * @param id The ID of the entity (server/user).
     * @return The {@link EntityData} for the specified entity or {@code null} if the entity is not in the mapping.
     */
    @Nullable
    public T getEntityData(@NonNull String mapId, long id) {
        return entityDataHashMap.get(mapId).get(id);
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
     * Gets the {@link Path} to the entity data file.
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
     * Gets the last time the entity data file was updated.
     * @return UNIX time in seconds of the last update.
     */
    protected long getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Gets the list of valid entity types that the data handler can handle.
     * @return List of {@link EntityType}
     */
    @NonNull
    public List<EntityType> getEntityTypes() {
        return entityTypes;
    }

    /**
     * Tells the executor to update the entity data file after some time.
     */
    public void updateEntityData() {
        long timeBetweenUpdate = (System.currentTimeMillis() / 1000) - lastUpdate;
        ScheduledFuture<?> future = getScheduledFuture();

        if(timeBetweenUpdate < 60 && (future != null && !future.cancel(false)))
            return;

        scheduledFuture = executor.schedule(
                () -> writeToJSON(entityDataHashMap),
                10,
                TimeUnit.SECONDS
        );
    }

    /**
     * Writes the specified data to the entity data file.
     * @param data The data to write.
     */
    protected void writeToJSON(@NonNull Object data) {
        try(BufferedWriter writer = Files.newBufferedWriter(pathToFile)) {
            gson.toJson(data, writer);

            lastUpdate = System.currentTimeMillis() / 1000;
        } catch (IOException e) {
            handleEntityDataException(e);
        }
    }

    /**
     * Creates a new instance of {@link EntityData}.
     * @param type The name of the entity type.
     * @return A new instance of {@link EntityData}.
     */
    protected abstract T createNewEntityData(@NonNull String type);

    /**
     * Handle an exception thrown when updating the entity data.
     * @param e The exception thrown.
     */
    protected abstract void handleEntityDataException(@NonNull Exception e);
}
