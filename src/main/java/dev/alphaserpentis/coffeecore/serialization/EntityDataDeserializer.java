package dev.alphaserpentis.coffeecore.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.alphaserpentis.coffeecore.data.entity.EntityData;
import dev.alphaserpentis.coffeecore.data.entity.EntityType;
import dev.alphaserpentis.coffeecore.handler.api.discord.entities.AbstractDataHandler;
import io.reactivex.rxjava3.annotations.NonNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A deserializer for {@link EntityData} objects.
 * @param <T> The type of {@link EntityData} to deserialize.
 */
public class EntityDataDeserializer<T extends EntityData> implements JsonDeserializer<Map<String, Map<Long, T>>> {
    private AbstractDataHandler<T> dataHandler;

    public void setDataHandler(@NonNull AbstractDataHandler<T> dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Map<Long, T>> deserialize(
            JsonElement jsonElement,
            Type type,
            JsonDeserializationContext jsonDeserializationContext
    ) throws JsonParseException {
        Map<String, Map<Long, T>> entityDataMap = new HashMap<>();
        Gson gson = new Gson();
        JsonObject object = jsonElement.getAsJsonObject();

        for(Map.Entry<String, JsonElement> entry: object.entrySet()) {
            // Determine the entity data class
            EntityType entityType = dataHandler
                    .getEntityTypes()
                    .stream()
                    .filter(type1 -> type1.getId().equals(entry.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new JsonParseException("Invalid entity type: " + entry.getKey()));
            Map<String, JsonElement> innerObject = entry.getValue().getAsJsonObject().asMap();

            // Initialize the entity data map
            entityDataMap.put(
                    entityType.id(),
                    Objects.requireNonNullElse(
                            entityDataMap.get(entityType.id()),
                            new HashMap<>()
                    )
            );

            // Deserialize the entity data
            innerObject
                    .forEach((key, value) -> entityDataMap
                                .get(entityType.id())
                                .put(
                                        Long.parseLong(key),
                                        (T) gson.fromJson(value, entityType.getEntityDataClass())
                                )
                    );
        }

        return entityDataMap;
    }
}
