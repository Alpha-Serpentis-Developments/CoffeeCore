package dev.alphaserpentis.coffeecore.serialization;

import com.google.gson.*;
import dev.alphaserpentis.coffeecore.data.server.ServerData;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A deserializer for {@link ServerData} objects.
 * @param <T> The type of {@link ServerData} to deserialize.
 */
public class ServerDataDeserializer<T extends ServerData> implements JsonDeserializer<Map<Long, T>> {

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, T> deserialize(
            JsonElement jsonElement,
            Type type,
            JsonDeserializationContext jsonDeserializationContext
    ) throws JsonParseException {
        Map<Long, T> serverDataMap = new HashMap<>();
        Gson gson = new Gson();
        JsonObject object = jsonElement.getAsJsonObject();

        for(Map.Entry<String, JsonElement> entry: object.entrySet()) {
            serverDataMap.put(Long.valueOf(entry.getKey()), (T) gson.fromJson(entry.getValue(), ServerData.class));
        }

        return serverDataMap;
    }
}
