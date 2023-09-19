package dev.alphaserpentis.coffeecore.commands.defaultcommands;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

public class Help extends BotCommand<MessageEmbed, SlashCommandInteractionEvent> {

    public Help() {
        super(
                new BotCommandOptions()
                        .setName("help")
                        .setDescription("Lists all the commands and their descriptions")
                        .setOnlyEmbed(true)
        );
    }

    public Help(@NonNull BotCommandOptions options) {
        super(options);
    }

    @Override
    @NonNull
    public CommandResponse<MessageEmbed> runCommand(long userId, @NonNull SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Help");
        eb.setDescription("List of all the commands provided by " + event.getJDA().getSelfUser().getName() + "!");
        eb.setFooter("Built using Coffee Core");
        for(BotCommand<?, ?> command: core.getCommandsHandler().getCommands()) {
            eb.addField(
                    command.getName(),
                    Objects.requireNonNullElse(command.getDescription(), "No description provided"),
                    false
            );
        }

        return new CommandResponse<>(isOnlyEphemeral(), eb.build());
    }
}
