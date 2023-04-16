package dev.alphaserpentis.coffeecore.helper;

import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;

public class ContainerHelper {
    private final IGuildChannelContainer container;

    public ContainerHelper(@NonNull IGuildChannelContainer container) {
        this.container = container;
    }

    public List<Guild> getGuilds() {
        if(container instanceof JDA) {
            return ((JDA) container).getGuilds();
        } else {
            return ((ShardManager) container).getGuilds();
        }
    }

}
