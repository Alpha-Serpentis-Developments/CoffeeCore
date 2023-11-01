package dev.alphaserpentis.examples.coffeecore.kotlin.custom.deserializer

import dev.alphaserpentis.coffeecore.serialization.ServerDataDeserializer
import dev.alphaserpentis.examples.coffeecore.kotlin.custom.handler.CustomServerData

class CustomServerDataDeserializer : ServerDataDeserializer<CustomServerData?>()
