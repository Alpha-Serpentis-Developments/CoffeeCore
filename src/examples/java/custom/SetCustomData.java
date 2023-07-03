package custom;

import custom.handler.CustomServerData;
import custom.handler.CustomServerDataHandler;
import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.io.IOException;

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
        CustomServerDataHandler handler = (CustomServerDataHandler) core.getServerDataHandler();
        CustomServerData customServerData = handler.getServerData(event.getGuild().getIdLong());
        String subcommand = event.getSubcommandName();

        if(subcommand.equals("view")) {
            return new CommandResponse<>(isOnlyEphemeral(), "The custom data of this server is: " + customServerData.getCustomData());
        } else if(subcommand.equals("set")) {
            String data = event.getOption("data").getAsString();
            customServerData.setCustomData(data);
            try {
                core.getServerDataHandler().updateServerData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new CommandResponse<>(isOnlyEphemeral(), "The custom data of this server has been set to: " + data);
        } else {
            throw new RuntimeException("Unknown subcommand: " + subcommand);
        }
    }

    @Override
    public void updateCommand(@NonNull JDA jda) {
        SubcommandData view = new SubcommandData("view", "View the custom data of the server.");
        SubcommandData set = new SubcommandData("set", "Set the custom data of the server.").addOption(OptionType.STRING, "data", "The data to set.", true);

        Command cmd = jda.upsertCommand(name, description)
                .addSubcommands(view, set).complete();

        setCommandId(cmd.getIdLong());
    }
}
