package dev.alphaserpentis.coffeecore.commands;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.Collection;
import java.util.HashMap;

/**
 * A command that can utilize buttons
 * @param <T> The type of object to return when the command is executed.
 * @param <E> The type of event that triggers the command.
 * @see dev.alphaserpentis.coffeecore.commands.BotCommand
 */
public abstract class ButtonCommand<T, E extends GenericCommandInteractionEvent> extends BotCommand<T, E> {
    /**
     * A {@link HashMap} of buttons that can be added to a message.
     */
    protected final HashMap<String, Button> buttonHashMap = new HashMap<>();
    public ButtonCommand() {
        super();
    }

    public ButtonCommand(@NonNull BotCommandOptions options) {
        super(options);
    }

    /**
     * This method is called when a button is pressed.
     * @param event The event that triggered the button press.
     */
    public abstract void runButtonInteraction(@NonNull final ButtonInteractionEvent event);

    /**
     * This method is called when the command is executed which may add buttons to a message.
     * @param event The event that triggered the command.
     * @return A collection of buttons to add to the message. The collection may be empty.
     */
    @NonNull
    public abstract Collection<ItemComponent> addButtonsToMessage(@NonNull final E event);

    /**
     * Get a {@link Button} by its key.
     * @param key The key of the button.
     * @return The button, or null if it does not exist.
     */
    @Nullable
    public Button getButton(@NonNull String key) {
        return buttonHashMap.get(key);
    }

    /**
     * Returns the {@link #buttonHashMap} field.
     * @return The button hash map.
     */
    @NonNull
    public HashMap<String, Button> getButtonHashMap() {
        return buttonHashMap;
    }

    /**
     * Easy way to add a {@link Button} to the {@link #buttonHashMap}.
     * @param key Unique key for the button. An {@link IllegalArgumentException} will be thrown if the key is already in use.
     * @param style The style of button.
     * @param label Text to display on the button. This cannot be null.
     * @param disabled Determines if the button is disabled.
     * @throws IllegalArgumentException If the key is already in use.
     */
    public void addButton(@NonNull String key, @NonNull ButtonStyle style, @NonNull String label, boolean disabled) {
        if(buttonHashMap.containsKey(key))
            throw new IllegalArgumentException("The key " + key + " is already in use!");

        buttonHashMap.put(key, Button.of(style, generateButtonId(key), label).withDisabled(disabled));
    }

    /**
     * Easy way to add a {@link Button} to the {@link #buttonHashMap} with a nullable {@link Emoji}.
     * @param key Unique key for the button. An {@link IllegalArgumentException} will be thrown if the key is already in use.
     * @param style The style of button.
     * @param label Text to display on the button. This may be null.
     * @param emoji {@link Emoji} to display on the button. This may be null.
     * @param disabled Determines if the button is disabled.
     * @throws IllegalArgumentException If the key is already in use.
     */
    public void addButton(@NonNull String key, @NonNull ButtonStyle style, @Nullable String label, @Nullable Emoji emoji, boolean disabled) {
        if(buttonHashMap.containsKey(key))
            throw new IllegalArgumentException("The key " + key + " is already in use!");

        buttonHashMap.put(key, Button.of(style, generateButtonId(key), label, emoji).withDisabled(disabled));
    }

    /**
     * Generate a button id. This id is used for identification purposes.
     * @param key Unique identifier for the button.
     * @return {@link String} The generated key.
     */
    @NonNull
    public String generateButtonId(@NonNull String key) {
        return String.format("%s_%s", getName(), key);
    }

    /**
     * Convert a component (button) id to a key.
     * @param componentId The component (button) id.
     * @return The key.
     */
    @NonNull
    public String convertComponentIdToKey(@NonNull String componentId) {
        return componentId.substring(getName().length() + 1);
    }

    @Override
    @NonNull
    public Message handleReply(
            @NonNull final E event,
            @NonNull final BotCommand<?, E> cmd
    ) {
        Collection<ItemComponent> buttons;

        if(cmd.isDeferReplies()) {
            WebhookMessageCreateAction<?> action = cmd.processDeferredCommand(event);
            buttons = addButtonsToMessage(event);

            if(buttons.isEmpty())
                return (Message) action.complete();
            else
                return (Message) action.addActionRow(buttons).complete();
        } else {
            ReplyCallbackAction action = cmd.processNonDeferredCommand(event);
            buttons = addButtonsToMessage(event);

            if(buttons.isEmpty())
                return action.complete().retrieveOriginal().complete();
            else
                return action.addActionRow(buttons).complete().retrieveOriginal().complete();
        }
    }
}
