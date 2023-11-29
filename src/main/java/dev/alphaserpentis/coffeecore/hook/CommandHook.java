package dev.alphaserpentis.coffeecore.hook;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import io.reactivex.rxjava3.annotations.Experimental;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

import java.util.Optional;

/**
 * A hook that can be run before or after a <b>DEFERRED</b> command is executed.
 */
@Experimental
public abstract class CommandHook {
    private final TypeOfHook typeOfHook;

    public enum TypeOfHook {
        PRE_EXECUTION,
        POST_EXECUTION
    }

    public CommandHook(@NonNull TypeOfHook typeOfHook) {
        this.typeOfHook = typeOfHook;
    }

    /**
     * Executes the hook and optionally returns a command response
     * @param cmd The command that was triggered
     * @param event The event that triggered the command
     * @param msg The message that was sent
     * @return Optional of CommandResponse to override the command response AND skip command execution
     */
    @NonNull
    public Optional<?> execute(
            @NonNull BotCommand<?, ?> cmd,
            @NonNull GenericCommandInteractionEvent event,
            @Nullable Message msg
    ) {
        return Optional.empty();
    }

    /**
     * Executes the hook and optionally returns a command response
     * @param cmd The command that was triggered
     * @param event The (button/modal) event that triggered the command
     * @param data The optional data that was returned from the command
     * @return Optional of CommandResponse to override the command response AND skip command execution
     */
    @NonNull
    @SuppressWarnings({"UnusedReturnValue"})
    public Optional<?> execute(
            @NonNull BotCommand<?, ?> cmd,
            @NonNull GenericInteractionCreateEvent event,
            @Nullable Object data
    ) {
        return Optional.empty();
    }

    @NonNull
    public TypeOfHook getTypeOfHook() {
        return typeOfHook;
    }
}
