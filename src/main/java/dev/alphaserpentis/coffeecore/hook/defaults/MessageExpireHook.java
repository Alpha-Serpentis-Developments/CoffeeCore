package dev.alphaserpentis.coffeecore.hook.defaults;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import dev.alphaserpentis.coffeecore.hook.CommandHook;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

import java.util.Optional;

public class MessageExpireHook extends CommandHook {
    public MessageExpireHook() {
        super(TypeOfHook.POST_EXECUTION);
    }

    @Override
    @NonNull
    public Optional<CommandResponse<?>> execute(
            @NonNull BotCommand<?, ?> cmd,
            @NonNull GenericCommandInteractionEvent event,
            @Nullable Message msg
            ) {
        return Optional.empty();
    }
}
