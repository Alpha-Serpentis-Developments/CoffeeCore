package dev.alphaserpentis.coffeecore.data.bot;

import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * A record class that holds the response to a command. If both {@code messageResponse} and {@code messageIsEphemeral}
 * are null, an {@link IllegalArgumentException} will be thrown.
 * @param messageResponse The response to the command which is either a {@link MessageEmbed} or a {@link String}.
 *                        If it is neither, an {@link IllegalArgumentException} will be thrown.
 * @param messageIsEphemeral Whether the response should be ephemeral or not.
 * @param <T> The type of the response which must be either a {@link MessageEmbed} or a {@link String}.
 */
public record CommandResponse<T>(T messageResponse, Boolean messageIsEphemeral) {
    public CommandResponse(
            @Nullable T messageResponse,
            @Nullable Boolean messageIsEphemeral
    ) {
        if(messageResponse != null) {
            if (!(messageResponse instanceof MessageEmbed || messageResponse instanceof String)) {
                throw new IllegalArgumentException("messageResponse must be of type MessageEmbed or String");
            }
        } else if(messageIsEphemeral == null) {
            throw new IllegalArgumentException("messageResponse and messageIsEphemeral cannot both be null!");
        }

        this.messageResponse = messageResponse;
        this.messageIsEphemeral = messageIsEphemeral;
    }
}
