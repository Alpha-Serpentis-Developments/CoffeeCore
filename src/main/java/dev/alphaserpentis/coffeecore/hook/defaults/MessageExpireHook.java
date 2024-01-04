package dev.alphaserpentis.coffeecore.hook.defaults;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.hook.CommandHook;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation to make messages expire. Does not delete messages triggered by UI elements like buttons.
 */
public class MessageExpireHook extends CommandHook {
    public MessageExpireHook() {
        super(Type.POST_EXECUTION);
    }

    @Override
    @NonNull
    public Optional<Void> execute(
            @NonNull BotCommand<?, ?> cmd,
            @NonNull GenericCommandInteractionEvent event,
            @Nullable Message msg
    ) {
        if(msg != null) deleteMessage(cmd.getMessageExpirationLength(), msg);

        return Optional.empty();
    }

    private void deleteMessage(long timeToExpire, @NonNull Message msg) {
        msg
                .delete()
                .delay(timeToExpire, TimeUnit.SECONDS)
                .complete();
    }
}
