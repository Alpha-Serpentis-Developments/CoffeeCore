package dev.alphaserpentis.coffeecore.hook.defaults;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import dev.alphaserpentis.coffeecore.hook.CommandHook;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

import java.time.Instant;
import java.util.Optional;

/**
 * Default implementation to handle ratelimits automatically
 */
public class RatelimitHook extends CommandHook {
    public RatelimitHook() {
        super(Type.PRE_EXECUTION);
    }

    @Override
    @NonNull
    public Optional<CommandResponse<?>> execute(
            @NonNull BotCommand<?, ?> cmd,
            @NonNull GenericCommandInteractionEvent event,
            @Nullable Message ignored
    ) {
        long userId = event.getUser().getIdLong();
        long ratelimitTime = cmd.getRatelimitMap().getOrDefault(userId, 0L);

        if(ratelimitTime > Instant.now().getEpochSecond()) {
            return Optional.of(new CommandResponse<>(cmd.isOnlyEphemeral(), generateEmbed(ratelimitTime).build()));
        }

        return Optional.empty();
    }

    @NonNull
    private EmbedBuilder generateEmbed(long ratelimitTime) {
        return new EmbedBuilder()
                .setTitle("Ratelimited")
                .setDescription("Ratelimit expires <t:" + ratelimitTime + ":R>.")
                .setColor(0xFF0000);
    }
}
