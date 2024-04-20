package dev.alphaserpentis.coffeecore.serialization

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import dev.alphaserpentis.coffeecore.data.entity.EntityData
import dev.alphaserpentis.coffeecore.handler.api.discord.entities.AbstractDataHandler
import java.lang.reflect.Type

open class EntityDataDeserializer<T : EntityData?> : JsonDeserializer<Map<String, Map<Long, T>>> {
    private lateinit var dataHandler: AbstractDataHandler<T>

    operator fun Map<String, Map<Long, T>>.get(key: String, key2: Long): T? = get(key)?.get(key2)

    operator fun MutableMap<String, MutableMap<Long, T>>.set(key: String, key2: Long, value: T) {
        computeIfAbsent(key) { mutableMapOf() }[key2] = value
    }

    fun setDataHandler(dataHandler: AbstractDataHandler<T>) {
        this.dataHandler = dataHandler
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(
        jsonElement: JsonElement,
        type: Type?,
        ignored: JsonDeserializationContext?
    ): Map<String, Map<Long, T>> {
        val map = mutableMapOf<String, MutableMap<Long, T>>()
        val gson = Gson()
        val jsonObject = jsonElement.asJsonObject

        for ((key, value) in jsonObject.entrySet()) {
            val entityType = dataHandler
                .entityTypes
                .stream()
                .filter { it.id == key }
                .findFirst()
                .orElseThrow { JsonParseException("Invalid entity type: $key") }

            value
                .asJsonObject
                .asMap()
                .forEach { (innerKey, innerValue) ->
                    map[key, innerKey.toLong()] = gson.fromJson(innerValue, entityType.entityDataClass) as T
                }
        }

        return map
    }

}