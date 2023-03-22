package dev.alphaserpentis.coffeecore.handler.api.discord.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alphaserpentis.coffeecore.data.server.ServerData;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The abstract class that handles {@link ServerData}.
 * @param <T> The type of {@link ServerData} to handle.
 */
public abstract class AbstractServerDataHandler<T extends ServerData> extends ListenerAdapter {
    /**
     * Path to the server data file.
     */
    public final Path serverJson;
    /**
     * The mapping of server IDs to {@link ServerData}.
     */
    public Map<Long, T> serverDataHashMap = new HashMap<>();

    public AbstractServerDataHandler(
            @NonNull Path path
    ) {
        serverJson = path;
    }

    /**
     * Initializes the server data handler.
     * @param api The {@link JDA} instance.
     * @throws IOException If the bot fails to write to the server data file.
     */
    public void init(@NonNull JDA api) throws IOException {
        // Check the current servers
        if(serverDataHashMap == null)
            serverDataHashMap = new HashMap<>();

        ArrayList<Long> serversActuallyJoined = new ArrayList<>();

        for(Guild g: api.getGuilds()) {
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
     * Updates the server data file.
     * @throws IOException If the bot fails to write to the server data file.
     */
    public void updateServerData() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        writeToJSON(gson, serverDataHashMap);
    }

    protected void writeToJSON(@NonNull Gson gson, @NonNull Object data) throws IOException {
        Writer writer = Files.newBufferedWriter(serverJson);
        gson.toJson(data, writer);
        writer.close();
    }

    /**
     * Creates a new instance of {@link ServerData}.
     * @return A new instance of {@link ServerData}.
     */
    protected abstract T createNewServerData();
}
