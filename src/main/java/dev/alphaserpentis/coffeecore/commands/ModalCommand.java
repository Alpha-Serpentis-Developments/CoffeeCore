package dev.alphaserpentis.coffeecore.commands;

import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import java.util.Optional;

public interface ModalCommand {
    /**
     * This method is called when a modal is pressed.
     * @param event The event that triggered the modal press.
     * @return An optional object after modal execution. This may be empty.
     */
    @NonNull
    Optional<?> runModalInteraction(@NonNull final ModalInteractionEvent event);
}
