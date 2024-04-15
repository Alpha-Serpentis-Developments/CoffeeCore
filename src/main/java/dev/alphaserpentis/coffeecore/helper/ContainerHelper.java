package dev.alphaserpentis.coffeecore.helper;

import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;

/**
 * A helper class for getting information from a {@link IGuildChannelContainer}.
 * @see JDA
 * @see ShardManager
 */
public class ContainerHelper {

    /**
     * Gets a list of {@link Guild}s from the container.
     * @param container The container to get the guilds from.
     * @return A list of {@link Guild}s from the container.
     * @see JDA#getGuilds()
     * @see ShardManager#getGuilds()
     */
    @NonNull
    public static List<Guild> getGuilds(@NonNull IGuildChannelContainer<?> container) {
        validate(container);

        if(container instanceof JDA j) {
            return j.getGuilds();
        } else {
            return ((ShardManager) container).getGuilds();
        }
    }

    /**
     * Sets the activity for the bot to display.
     * @param container The container to set the activity for.
     * @param activity {@link Activity} to set.
     * @see Presence#setActivity(Activity)
     * @see ShardManager#setActivity(Activity)
     */
    public static void setActivity(@NonNull IGuildChannelContainer<?> container, @NonNull Activity activity) {
        validate(container);

        if(container instanceof JDA j) {
            j.getPresence().setActivity(activity);
        } else {
            ((ShardManager) container).setActivity(activity);
        }
    }

    /**
     * Adds event listeners to either the {@link JDA} or {@link ShardManager}.
     * @param container The container to add the listeners to.
     * @param listeners The listeners to add.
     * @see JDA#addEventListener(Object...)
     * @see ShardManager#addEventListener(Object...)
     */
    public static void addEventListeners(@NonNull IGuildChannelContainer<?> container, @NonNull Object... listeners) {
        validate(container);

        if(container instanceof JDA jda) {
            jda.addEventListener(listeners);
        } else {
            ((ShardManager) container).addEventListener(listeners);
        }
    }

    /**
     * Validates that the container is an instance of {@link JDA} or {@link ShardManager}.
     * @param container The container to validate.
     * @throws IllegalArgumentException If the container is not an instance of {@link JDA} or {@link ShardManager}.
     * @see JDA
     * @see ShardManager
     */
    private static void validate(@NonNull IGuildChannelContainer<?> container) {
        if(!(container instanceof JDA) && !(container instanceof ShardManager)) {
            throw new IllegalArgumentException("Container must be an instance of JDA or ShardManager");
        }
    }
}
