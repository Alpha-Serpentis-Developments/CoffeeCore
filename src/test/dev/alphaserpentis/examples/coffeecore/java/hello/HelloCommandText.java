package dev.alphaserpentis.examples.coffeecore.java.hello;

import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;

public class HelloCommandText extends BotCommand<String, SlashCommandInteractionEvent> {

    public HelloCommandText() {
        super(
                new BotCommandOptions()
                        .setName("hello")
                        .setDescription("Says hello to you!")
                        .setOnlyEmbed(false)
                        .setOnlyEphemeral(false)
        );
    }

    @Override
    @NonNull
    public CommandResponse<String> runCommand(
            long userId,
            @NonNull SlashCommandInteractionEvent event
    ) {
        return new CommandResponse<>(
                isOnlyEphemeral(),
                "Hello, " + event.getUser().getAsMention() + "!"
        );
    }
}