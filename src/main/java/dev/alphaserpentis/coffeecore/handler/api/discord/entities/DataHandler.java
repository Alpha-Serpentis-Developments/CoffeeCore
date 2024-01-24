package dev.alphaserpentis.coffeecore.handler.api.discord.entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.entity.EntityData;
import dev.alphaserpentis.coffeecore.data.entity.ServerData;
import dev.alphaserpentis.coffeecore.data.entity.UserData;
import dev.alphaserpentis.coffeecore.serialization.EntityDataDeserializer;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A default implementation of {@link AbstractDataHandler} that handles {@link EntityData}.
 * Additionally, it handles data pertaining to guilds when the bot is invited to them or kicked out of them
 * @param <T> The type of {@link EntityData} to handle.
 */
public class DataHandler<T extends EntityData> extends AbstractDataHandler<T> {
    /**
     * Cached guild commands. <b>This expects that the commands do not change after caching</b>
     */
    private List<BotCommand<?, ?>> cachedGuildCommands = null;

    /**
     * Initializes the data handler.
     * @param path The path to the entity data file.
     * @param typeToken The {@link TypeToken} of the mapping of entity IDs to {@link EntityData}.
     * @param jsonDeserializer The {@link EntityDataDeserializer} to deserialize the entity data.
     * @throws IOException If the bot fails to read the entity data file.
     */
    public DataHandler(
            @NonNull Path path,
            @NonNull TypeToken<Map<String, Map<Long, T>>> typeToken,
            @NonNull EntityDataDeserializer<T> jsonDeserializer
    ) throws IOException {
        super(path);
        Gson gson;
        Reader reader;

        jsonDeserializer.setDataHandler(this);
        gson = new GsonBuilder()
                .registerTypeAdapter(typeToken.getType(), jsonDeserializer)
                .create();
        reader = Files.newBufferedReader(path);
        entityDataHashMap = Objects.requireNonNullElse(
                gson.fromJson(reader, typeToken.getType()),
                new HashMap<>()
        );
    }

    /**
     * Gets the specified entity data.
     * <p>
     * <b>Implementation Note:</b> This method will create a new instance of the specified {@link EntityData} type if
     * the entity data does not exist.
     * @param entityType The identifier to check which mapping to use.
     * @param id The ID of the entity (server/user).
     * @return The entity data.
     */
    @Override
    @NonNull
    public T getEntityData(@NonNull String entityType, long id) {
        T data = entityDataHashMap.get(entityType).get(id);

        if(data == null) {
            data = createNewEntityData(entityType);
            entityDataHashMap.get(entityType).put(id, data);
        }

        return data;
    }

    /**
     * Creates a new instance of the specified {@link EntityData} type.
     * <p>
     * <b>This method should be overridden if you're extending this class or have additional entity types!</b>
     * @param entityType The type of {@link EntityData} to create.
     * @return A new instance of {@link EntityData}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T createNewEntityData(@NonNull String entityType) {
        String type = getEntityTypes()
                .stream()
                .filter(type1 -> type1.getId().equals(entityType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid entity type: " + entityType))
                .getId();

        if(type.equals("guild")) {
            return (T) new ServerData();
        } else if(type.equals("user")) {
            return (T) new UserData();
        } else {
            throw new IllegalArgumentException("Invalid entity type: " + entityType);
        }
    }

    @Override
    protected void handleEntityDataException(@NonNull Exception e) {
        // Write your implementation, by default this won't do anything
    }

    @Override
    public void onGuildJoin(@NonNull GuildJoinEvent event) {
        entityDataHashMap.get("guilds").put(event.getGuild().getIdLong(), createNewEntityData("guild"));
        getCore().getCommandsHandler().upsertGuildCommandsToGuild(getCachedGuildCommands(), event.getGuild());
        updateEntityData();
    }

    @Override
    public void onGuildLeave(@NonNull GuildLeaveEvent event) {
        entityDataHashMap.get("guilds").remove(event.getGuild().getIdLong());
        getCore().getCommandsHandler().deregisterCommands(event.getGuild().getIdLong());
        updateEntityData();
    }

    @NonNull
    protected List<BotCommand<?, ?>> getCachedGuildCommands() {
        return Objects.requireNonNullElseGet(cachedGuildCommands, () -> {
            cachedGuildCommands = getCore().getCommandsHandler().getGuildCommands();
            return cachedGuildCommands;
        });
    }
}
