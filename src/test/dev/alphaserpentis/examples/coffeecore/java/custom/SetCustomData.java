package dev.alphaserpentis.examples.coffeecore.java.custom;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import dev.alphaserpentis.examples.coffeecore.java.custom.handler.CustomDataHandler;
import dev.alphaserpentis.examples.coffeecore.java.custom.handler.CustomServerData;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class SetCustomData extends BotCommand<String, SlashCommandInteractionEvent> {

    public SetCustomData() {
        super(
                new BotCommandOptions()
                        .setName("set")
                        .setDescription("Sets the custom data of the server")
                        .setOnlyEmbed(false)
                        .setOnlyEphemeral(false)
        );
    }

    @Override
    @NonNull
    public CommandResponse<String> runCommand(long userId, @NonNull SlashCommandInteractionEvent event) {
        CustomDataHandler handler = (CustomDataHandler) core.getDataHandler();
        CustomServerData customServerData = handler.getEntityData("guild", event.getGuild().getIdLong());
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("view")) {
            return new CommandResponse<>(
                    isOnlyEphemeral(),
                    "The custom data of this server is: " + customServerData.getCustomData()
            );
        } else if(subcommand.equals("set")) {
            String data = event.getOption("data").getAsString();
            customServerData.setCustomData(data);
            core.getDataHandler().updateEntityData();

            return new CommandResponse<>(
                    isOnlyEphemeral(),
                    "The custom data of this server has been set to: " + data
            );
        } else {
            throw new RuntimeException("Unknown subcommand: " + subcommand);
        }
    }

    @Override
    public void updateCommand(@NonNull JDA jda) {
        SubcommandData view = new SubcommandData("view", "View the custom data of the server.");
        SubcommandData set = new SubcommandData("set", "Set the custom data of the server.")
                .addOption(OptionType.STRING, "data", "The data to set.", true);
        Command cmd = jda.upsertCommand(name, description)
                .addSubcommands(view, set).complete();

        setGlobalCommandId(cmd.getIdLong());
    }
}
