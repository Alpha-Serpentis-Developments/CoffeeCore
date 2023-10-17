package dev.alphaserpentis.coffeecore.handler.api.discord.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.server.ServerData;
import dev.alphaserpentis.coffeecore.handler.api.discord.commands.CommandsHandler;
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
 * A default implementation of {@link AbstractServerDataHandler} that handles {@link ServerData}.
 * @param <T> The type of {@link ServerData} to handle.
 */
public class ServerDataHandler<T extends ServerData> extends AbstractServerDataHandler<T> {
    /**
     * Cached guild commands. <b>This expects that the commands do not change after caching</b>
     */
    private List<BotCommand<?, ?>> cachedGuildCommands = null;

    /**
     * Initializes the server data handler.
     * @param path The path to the server data file.
     * @param typeToken The {@link TypeToken} of the mapping of user IDs to {@link ServerData}.
     * @param jsonDeserializer The {@link JsonDeserializer} to deserialize the server data.
     * @throws IOException If the bot fails to read the server data file.
     */
    public ServerDataHandler(
            @NonNull Path path,
            @NonNull TypeToken<Map<Long, T>> typeToken,
            @NonNull JsonDeserializer<Map<Long, T>> jsonDeserializer
    ) throws IOException {
        super(path);
        Gson gson;
        Reader reader;

        gson = new GsonBuilder()
                .registerTypeAdapter(serverDataHashMap.getClass(), jsonDeserializer)
                .create();
        reader = Files.newBufferedReader(path);
        serverDataHashMap = Objects.requireNonNullElse(gson.fromJson(reader, typeToken.getType()), new HashMap<>());
    }

    /**
     * Creates a new instance of {@link ServerData}.
     * <p>
     * <b>This method should be overridden if you're extending this class!</b>
     * @return A new instance of {@link ServerData}.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected T createNewServerData() {
        return (T) new ServerData();
    }

    @Override
    protected void handleServerDataException(@NonNull Exception e) {
        // Write your implementation, by default this won't do anything
    }

    @Override
    public void onGuildJoin(@NonNull GuildJoinEvent event) {
        serverDataHashMap.put(event.getGuild().getIdLong(), createNewServerData());
        getCore().getCommandsHandler().upsertGuildCommandsToGuild(getCachedGuildCommands(), event.getGuild());
        updateServerData();
    }

    @Override
    public void onGuildLeave(@NonNull GuildLeaveEvent event) {
        serverDataHashMap.remove(event.getGuild().getIdLong());
        getCore().getCommandsHandler().deregisterCommands(event.getGuild().getIdLong());
        updateServerData();
    }

    @NonNull
    protected List<BotCommand<?, ?>> getCachedGuildCommands() {
        return Objects.requireNonNullElseGet(cachedGuildCommands, () -> {
            cachedGuildCommands = getCore().getCommandsHandler().getGuildCommands();
            return cachedGuildCommands;
        });
    }
}
