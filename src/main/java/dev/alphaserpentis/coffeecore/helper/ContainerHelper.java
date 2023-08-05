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

    private final IGuildChannelContainer container;

    public ContainerHelper(@NonNull IGuildChannelContainer container) {
        this.container = container;
    }

    /**
     * Gets a list of {@link Guild}s from the container.
     * @return A list of {@link Guild}s from the container.
     * @see JDA#getGuilds()
     * @see ShardManager#getGuilds()
     */
    @NonNull
    public List<Guild> getGuilds() {
        if(container instanceof JDA j) {
            return j.getGuilds();
        } else {
            return ((ShardManager) container).getGuilds();
        }
    }

    /**
     * Sets the activity for the bot to display.
     * @param activity {@link Activity} to set.
     * @see Presence#setActivity(Activity)
     * @see ShardManager#setActivity(Activity)
     */
    public void setActivity(@NonNull Activity activity) {
        if(container instanceof JDA j) {
            j.getPresence().setActivity(activity);
        } else {
            ((ShardManager) container).setActivity(activity);
        }
    }

    /**
     * Adds event listeners to either the {@link JDA} or {@link ShardManager}.
     * @param listeners The listeners to add.
     * @see JDA#addEventListener(Object...)
     * @see ShardManager#addEventListener(Object...)
     */
    public void addEventListeners(@NonNull Object... listeners) {
        if(container instanceof JDA jda) {
            jda.addEventListener(listeners);
        } else {
            ((ShardManager) container).addEventListener(listeners);
        }
    }

}
