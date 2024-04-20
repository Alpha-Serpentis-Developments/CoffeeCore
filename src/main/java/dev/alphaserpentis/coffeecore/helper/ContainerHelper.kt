package dev.alphaserpentis.coffeecore.helper;

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer
import net.dv8tion.jda.api.managers.Presence
import net.dv8tion.jda.api.sharding.ShardManager

/**
 * A helper class for getting information from a [IGuildChannelContainer].
 * @see JDA
 * @see ShardManager
 */
object ContainerHelper {
    /**
     * Gets a list of [Guild]s from the container.
     * @param container The container to get the guilds from.
     * @return A list of [Guild]s from the container.
     * @see JDA.getGuilds
     * @see ShardManager.getGuilds
     */
    @JvmStatic
    fun getGuilds(container: IGuildChannelContainer<*>): List<Guild> {
        return when (container) {
            is JDA -> container.guilds
            is ShardManager -> container.guilds
            else -> throw IllegalStateException("Container must be an instance of JDA or ShardManager")
        }
    }

    /**
     * Sets the activity for the bot to display.
     * @param container The container to set the activity for.
     * @param activity [Activity] to set.
     * @see Presence.setActivity
     * @see ShardManager.setActivity
     */
    @JvmStatic
    fun setActivity(container: IGuildChannelContainer<*>, activity: Activity) {
        when (container) {
            is JDA -> container.presence.activity = activity
            is ShardManager -> container.setActivity(activity)
            else -> throw IllegalStateException("Container must be an instance of JDA or ShardManager")
        }
    }

    /**
     * Adds event listeners to either the [JDA] or [ShardManager].
     * @param container The container to add the listeners to.
     * @param listeners The listeners to add.
     * @see JDA.addEventListener
     * @see ShardManager.addEventListener
     */
    @JvmStatic
    fun addEventListeners(container: IGuildChannelContainer<*>, vararg listeners: Any) {
        return when (container) {
            is JDA -> container.addEventListener(listeners)
            is ShardManager -> container.addEventListener(listeners)
            else -> throw IllegalStateException("Container must be an instance of JDA or ShardManager")
        }
    }
}
