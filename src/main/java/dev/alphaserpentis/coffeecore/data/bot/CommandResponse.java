package dev.alphaserpentis.coffeecore.data.bot;

import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * A record class that holds the response to a command. If both {@code messageResponse} and {@code messageIsEphemeral}
 * are null, an {@link IllegalArgumentException} will be thrown.
 * @param messageResponse The response to the command which is var args of several {@link MessageEmbed} or a <b>SINGLE</b> {@link String}
 * @param messageIsEphemeral Whether the response should be ephemeral or not.
 * @param <T> The type of the response which must be either a {@link MessageEmbed} or a {@link String}.
 */
public record CommandResponse<T>(Boolean messageIsEphemeral, T... messageResponse) {
    @SafeVarargs
    public CommandResponse(
            @Nullable Boolean messageIsEphemeral,
            @Nullable T... messageResponse
    ) {
        if (messageResponse != null) {
            if (messageResponse instanceof String[]) {
                if (messageResponse.length > 1) {
                    throw new IllegalArgumentException("messageResponse cannot be a String[] with a length greater than 1");
                }
            } else {
                throw new IllegalArgumentException("messageResponse must be of type String[]");
            }
        } else {
            if (messageIsEphemeral != null) {
                throw new IllegalArgumentException("messageIsEphemeral cannot be true if messageResponse is null");
            }
        }

        this.messageResponse = messageResponse;
        this.messageIsEphemeral = messageIsEphemeral;
    }

    @SuppressWarnings("unchecked")
    public CommandResponse(
            @Nullable MessageEmbed messageResponse,
            @Nullable Boolean messageIsEphemeral
    ) {
        this(messageIsEphemeral, (T) messageResponse);
    }

    @SuppressWarnings("unchecked")
    public CommandResponse(
            @Nullable Boolean messageIsEphemeral,
            @Nullable MessageEmbed... messageResponse
    ) {
        this(messageIsEphemeral, (T) messageResponse);
    }

    @SuppressWarnings("unchecked")
    public CommandResponse(
            @Nullable Boolean messageIsEphemeral,
            @Nullable String messageResponse
    ) {
        this(messageIsEphemeral, (T) messageResponse);
    }
}
