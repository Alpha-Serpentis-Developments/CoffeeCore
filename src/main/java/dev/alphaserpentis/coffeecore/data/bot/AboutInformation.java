package dev.alphaserpentis.coffeecore.data.bot;

import dev.alphaserpentis.coffeecore.commands.defaultcommands.About;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;

/**
 * A record that provides information to the {@link About} command. Fields are nullable, and will be ignored if null.
 * @param description The description to display in the embed.
 * @param footer The footer to display in the embed.
 * @param color The color to display in the embed.
 * @param fields An optional list of fields to add to the embed.
 * @param displayShardingInfo Whether to display sharding information
 * @param displayServersInfo Whether to display server information
 */
public record AboutInformation(
        @Nullable String description,
        @Nullable String footer,
        @Nullable Color color,
        @Nullable ArrayList<MessageEmbed.Field> fields,
        boolean displayShardingInfo,
        boolean displayServersInfo
) {
    public AboutInformation(
            @Nullable String description,
            @Nullable String footer,
            @Nullable Color color,
            boolean displayShardingInfo,
            boolean displayServersInfo
    ) {
        this(description, footer, color, null, displayShardingInfo, displayServersInfo);
    }
    public AboutInformation(
            @Nullable String description,
            @Nullable String footer,
            @Nullable Color color,
            @Nullable ArrayList<MessageEmbed.Field> fields
    ) {
        this(description, footer, color, fields, true, true);
    }
    public AboutInformation(
            @Nullable String description,
            @Nullable String footer,
            @Nullable Color color
    ) {
        this(description, footer, color, null, true, true);
    }
    public AboutInformation(
            @Nullable String description,
            @Nullable String footer
    ) {
        this(description, footer, null, null, true, true);
    }
}