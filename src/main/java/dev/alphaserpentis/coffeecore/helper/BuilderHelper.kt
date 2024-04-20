package dev.alphaserpentis.coffeecore.helper

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder

/**
 * A helper class for building JDA instances.
 */
object BuilderHelper {
    @JvmStatic
    fun <T> build(builder: T): IGuildChannelContainer<*> {
        return when (builder) {
            is JDABuilder -> builder.build()
            is DefaultShardManagerBuilder -> builder.build()
            else -> throw IllegalArgumentException("Builder must be either a JDABuilder or a DefaultShardManagerBuilder")
        }
    }
}
