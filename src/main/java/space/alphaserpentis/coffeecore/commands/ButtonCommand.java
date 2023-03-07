package space.alphaserpentis.coffeecore.commands;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Collection;
import java.util.HashMap;

public abstract class ButtonCommand<T> extends BotCommand<T> {
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
    abstract public void runButtonInteraction(@NonNull ButtonInteractionEvent event);

    /**
     * This method is called when the command is executed which may add buttons to a message.
     * @param event The event that triggered the command.
     * @return A collection of buttons to add to the message. The collection may be empty.
     */
    @NonNull
    abstract public Collection<ItemComponent> addButtons(@NonNull GenericCommandInteractionEvent event);

    /**
     * Get a button by its key.
     * @param key The key of the button.
     * @return The button, or null if it does not exist.
     */
    @Nullable
    public Button getButton(String key) {
        return buttonHashMap.get(key);
    }

    /**
     * Get the button hash map.
     * @return The button hash map.
     */
    @NonNull
    public HashMap<String, Button> getButtonHashMap() {
        return buttonHashMap;
    }
}
