package dev.alphaserpentis.coffeecore.helper;

import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

/**
 * A helper class for building JDA instances.
 * @param <T> The type of builder to use. Must be either a {@link JDABuilder} or a {@link DefaultShardManagerBuilder}.
 */
public class BuilderHelper<T> {
    private final T builder;

    public BuilderHelper(@NonNull T builder) {
        if(!(builder instanceof JDABuilder) && !(builder instanceof DefaultShardManagerBuilder))
            throw new IllegalArgumentException("The builder must be either a JDABuilder or a DefaultShardManagerBuilder.");

        this.builder = builder;
    }

    @NonNull
    public IGuildChannelContainer build() {
        if(builder instanceof JDABuilder) {
            return ((JDABuilder) builder).build();
        } else {
            return ((DefaultShardManagerBuilder) builder).build();
        }
    }
}
