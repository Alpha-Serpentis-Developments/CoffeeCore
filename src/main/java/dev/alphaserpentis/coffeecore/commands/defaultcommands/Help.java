package dev.alphaserpentis.coffeecore.commands.defaultcommands;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import dev.alphaserpentis.coffeecore.handler.api.discord.commands.CommandsHandler;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Help extends BotCommand<MessageEmbed> {

    public Help() {
        super(
                new BotCommandOptions(
                        "help",
                        "Lists all the commands and their descriptions",
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

        eb.setTitle("Help");
        eb.setDescription("List of all the commands provided by " + event.getJDA().getSelfUser().getName() + "!");
        eb.setFooter("Built using Coffee Core");
        for(BotCommand<?> command: CommandsHandler.mappingOfCommands.values()) {
            eb.addField(
                    command.getName(),
                    command.getDescription(),
                    false
            );
        }

        return new CommandResponse<>(eb.build(), isOnlyEphemeral());
    }
}
