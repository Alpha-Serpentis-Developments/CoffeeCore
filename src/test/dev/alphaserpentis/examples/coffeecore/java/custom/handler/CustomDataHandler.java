package dev.alphaserpentis.examples.coffeecore.java.custom.handler;

import com.google.gson.reflect.TypeToken;
import dev.alphaserpentis.coffeecore.data.entity.ServerData;
import dev.alphaserpentis.coffeecore.handler.api.discord.entities.DataHandler;
import dev.alphaserpentis.coffeecore.serialization.EntityDataDeserializer;
import io.reactivex.rxjava3.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class CustomDataHandler extends DataHandler<CustomServerData> {
    /**
     * Initializes the server data handler.
     *
     * @param path             The path to the server data file.
     * @param typeToken        The {@link TypeToken} of the mapping of user IDs to {@link ServerData}.
     * @param jsonDeserializer The {@link EntityDataDeserializer} to deserialize the server data.
     * @throws IOException If the bot fails to read the server data file.
     */
    public CustomDataHandler(
            @NonNull Path path,
            @NonNull TypeToken<Map<String, Map<Long, CustomServerData>>> typeToken,
            @NonNull EntityDataDeserializer<CustomServerData> jsonDeserializer
    ) throws IOException {
        super(path, typeToken, jsonDeserializer);
    }

    @Override
    public CustomServerData createNewEntityData(@NonNull String entityId) {
        return new CustomServerData();
    }
}
