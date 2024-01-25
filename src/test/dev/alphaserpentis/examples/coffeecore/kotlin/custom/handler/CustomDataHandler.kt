package dev.alphaserpentis.examples.coffeecore.kotlin.custom.handler

import com.google.gson.reflect.TypeToken
import dev.alphaserpentis.coffeecore.data.entity.ServerData
import dev.alphaserpentis.coffeecore.handler.api.discord.entities.DataHandler
import dev.alphaserpentis.examples.coffeecore.kotlin.custom.deserializer.CustomEntityDataDeserializer
import java.nio.file.Path

class CustomDataHandler
/**
 * Initializes the server data handler.
 *
 * @param path             The path to the server data file.
 * @param typeToken        The [TypeToken] of the mapping of user IDs to [ServerData].
 * @param jsonDeserializer The [CustomEntityDataDeserializer] to deserialize the server data.
 * @throws java.io.IOException If the bot fails to read the server data file.
 */
    (
    path: Path,
    typeToken: TypeToken<Map<String, Map<Long?, CustomServerData?>?>>,
    jsonDeserializer: CustomEntityDataDeserializer
) : DataHandler<CustomServerData?>(path, typeToken, jsonDeserializer) {
    override fun createNewEntityData(ignored: String): CustomServerData {
        return CustomServerData()
    }
}
