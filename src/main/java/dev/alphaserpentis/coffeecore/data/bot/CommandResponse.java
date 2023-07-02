package dev.alphaserpentis.coffeecore.data.bot;

import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * A record class that holds the response to a command. If both {@code messageResponse} and {@code messageIsEphemeral}
 * are null, an {@link IllegalArgumentException} will be thrown.
 * @param messageResponse The response to the command which is either an array of {@link MessageEmbed} or a {@link String}.
 * @param messageIsEphemeral Whether the response should be ephemeral or not.
 * @param <T> The type of the response which must be either a {@link MessageEmbed} or a {@link String}.
 */
public record CommandResponse<T>(T[] messageResponse, Boolean messageIsEphemeral) {
    public CommandResponse(
            @Nullable T[] messageResponse,
            @Nullable Boolean messageIsEphemeral
    ) {
        if(messageResponse instanceof String[] && messageResponse.length > 1) {
            throw new IllegalArgumentException("messageResponse cannot be a String[] with a length greater than 1");
        }
        if(messageResponse == null && messageIsEphemeral == null) {
            throw new IllegalArgumentException("Both messageResponse and messageIsEphemeral cannot be null");
        }

        this.messageResponse = messageResponse;
        this.messageIsEphemeral = messageIsEphemeral;
    }

    @SuppressWarnings("unchecked")
    public CommandResponse(
            @Nullable MessageEmbed messageResponse,
            @Nullable Boolean messageIsEphemeral
    ) {
        this((T[]) new MessageEmbed[] {messageResponse}, messageIsEphemeral);
    }

    @SuppressWarnings("unchecked")
    public CommandResponse(
            @Nullable MessageEmbed[] messageResponse,
            @Nullable Boolean messageIsEphemeral
    ) {
        this((T[]) messageResponse, messageIsEphemeral);
    }

    @SuppressWarnings("unchecked")
    public CommandResponse(
            @Nullable String messageResponse,
            @Nullable Boolean messageIsEphemeral
    ) {
        this((T[]) new String[] {messageResponse}, messageIsEphemeral);
    }
}
