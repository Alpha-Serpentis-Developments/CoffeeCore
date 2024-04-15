package dev.alphaserpentis.coffeecore.helper;

import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

/**
 * A helper class for building JDA instances.
 */
public class BuilderHelper {

    @NonNull
    public static <T> IGuildChannelContainer<?> build(@NonNull T builder) {
        if(builder instanceof JDABuilder jdaBuilder)
            return jdaBuilder.build();
        else if(builder instanceof DefaultShardManagerBuilder shardManagerBuilder)
            return (shardManagerBuilder).build();
        else
            throw new IllegalArgumentException("Builder must be either a JDABuilder or a DefaultShardManagerBuilder");
    }
}
