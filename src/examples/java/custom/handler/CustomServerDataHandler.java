package custom.handler;

import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import dev.alphaserpentis.coffeecore.data.server.ServerData;
import dev.alphaserpentis.coffeecore.handler.api.discord.servers.ServerDataHandler;
import io.reactivex.rxjava3.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class CustomServerDataHandler extends ServerDataHandler<CustomServerData> {
    /**
     * Initializes the server data handler.
     *
     * @param path             The path to the server data file.
     * @param typeToken        The {@link TypeToken} of the mapping of user IDs to {@link ServerData}.
     * @param jsonDeserializer The {@link JsonDeserializer} to deserialize the server data.
     * @throws IOException If the bot fails to read the server data file.
     */
    public CustomServerDataHandler(
            @NonNull Path path,
            @NonNull TypeToken<Map<Long, CustomServerData>> typeToken,
            @NonNull JsonDeserializer<Map<Long, CustomServerData>> jsonDeserializer
    ) throws IOException {
        super(path, typeToken, jsonDeserializer);
    }

    @Override
    protected CustomServerData createNewServerData() {
        return new CustomServerData();
    }
}
