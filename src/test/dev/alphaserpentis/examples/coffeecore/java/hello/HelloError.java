package dev.alphaserpentis.examples.coffeecore.java.hello;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class HelloError extends BotCommand<String, SlashCommandInteractionEvent> {
    public HelloError() {
        super(
                new BotCommandOptions()
                        .setName("helloerror")
                        .setDescription("Says hello to you! (It doesn't)")
                        .setOnlyEmbed(false)
                        .setOnlyEphemeral(false)
        );
    }

    @Override
    @NonNull
    public CommandResponse<String> runCommand(long userId, @NonNull SlashCommandInteractionEvent event) {
        throw new IllegalStateException("Oops! All errors!");
    }
}
