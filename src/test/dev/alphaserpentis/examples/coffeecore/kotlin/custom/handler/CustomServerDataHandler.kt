package dev.alphaserpentis.examples.coffeecore.kotlin.custom.handler

import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import dev.alphaserpentis.coffeecore.data.server.ServerData
import dev.alphaserpentis.coffeecore.handler.api.discord.servers.ServerDataHandler
import java.nio.file.Path

class CustomServerDataHandler
/**
 * Initializes the server data handler.
 *
 * @param path             The path to the server data file.
 * @param typeToken        The [TypeToken] of the mapping of user IDs to [ServerData].
 * @param jsonDeserializer The [JsonDeserializer] to deserialize the server data.
 * @throws java.io.IOException If the bot fails to read the server data file.
 */
    (
    path: Path,
    typeToken: TypeToken<Map<Long?, CustomServerData?>?>,
    jsonDeserializer: JsonDeserializer<Map<Long?, CustomServerData?>?>
) : ServerDataHandler<CustomServerData?>(path, typeToken, jsonDeserializer) {
    override fun createNewServerData(): CustomServerData {
        return CustomServerData()
    }
}
