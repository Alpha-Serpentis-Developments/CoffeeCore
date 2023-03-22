package dev.alphaserpentis.coffeecore.commands.defaultcommands;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Duration;

public class Shutdown extends BotCommand<MessageEmbed> {

    public Shutdown() {
        super(
                new BotCommandOptions(
                        "shutdown",
                        "Shuts down the bot. This may or may not respond.",
                        true,
                        false,
                        TypeOfEphemeral.DEFAULT
                )
        );
    }

    @Override
    @NonNull
    public CommandResponse<MessageEmbed> runCommand(long userId, @NonNull SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();

        if(core.getBotOwnerId() != userId) {
            eb.setTitle("Denied");
            eb.setDescription("You are not authorized to use this command.");
            eb.setColor(0xff0000);
            return new CommandResponse<>(eb.build(), isOnlyEphemeral());
        }

        eb.setTitle("Shutting down...");
        eb.setDescription("The bot is shutting down.");
        eb.setColor(0xff0000);

        try {
            core.shutdown(Duration.ofSeconds(5));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new CommandResponse<>(eb.build(), isOnlyEphemeral());
    }
}
