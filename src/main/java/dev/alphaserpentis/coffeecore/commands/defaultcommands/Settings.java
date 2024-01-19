package dev.alphaserpentis.coffeecore.commands.defaultcommands;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import dev.alphaserpentis.coffeecore.data.entity.ServerData;
import dev.alphaserpentis.coffeecore.handler.api.discord.entities.DataHandler;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.io.IOException;
import java.util.Objects;

public class Settings extends BotCommand<MessageEmbed, SlashCommandInteractionEvent> {

    public Settings() {
        super(
                new BotCommandOptions()
                        .setName("settings")
                        .setDescription("Configure the bot's settings")
                        .setOnlyEmbed(true)
        );
    }

    public Settings(@NonNull BotCommandOptions options) {
        super(options);
    }

    @Override
    @NonNull
    public CommandResponse<MessageEmbed> runCommand(long userId, @NonNull SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Server Settings");

        if(event.getGuild() == null) {
            eb.setDescription("This command can only be used in a server.");
        } else {
            if(isUserPermissioned(Objects.requireNonNull(event.getMember()))) {
                // This will be swapped for a switch statement when more settings are added.
                if(Objects.equals(event.getSubcommandName(), "ephemeral")) {
                    try {
                        setServerEphemeral(event.getGuild().getIdLong(), eb);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                eb.setDescription(
                        """
                                You do not have permission to change the server settings.

                                You must have the `Manage Server` or `Administrator` permission to change the server settings."""
                );
                eb.setColor(0xff0000);
            }
        }

        return new CommandResponse<>(isOnlyEphemeral(), eb.build());
    }

    @Override
    public void updateCommand(@NonNull JDA jda) {
        SubcommandData ephemeral = new SubcommandData(
                "ephemeral",
                "Toggle whether the bot's responses are ephemeral"
        );

        jda.upsertCommand(name, description).addSubcommands(ephemeral).queue(
                (cmd) -> setGlobalCommandId(cmd.getIdLong())
        );
    }

    public boolean isUserPermissioned(@NonNull Member member) {
        return member.hasPermission(Permission.MANAGE_SERVER) || member.hasPermission(Permission.ADMINISTRATOR);
    }

    private void setServerEphemeral(long guildId, EmbedBuilder eb) throws IOException {
        DataHandler<?> sdh = (DataHandler<?>) core.getDataHandler();
        ServerData sd = (ServerData) sdh.getEntityData("guild", guildId);

        if(sd.getOnlyEphemeral()) {
            sd.setOnlyEphemeral(false);
            eb.setDescription("The bot's responses are no longer ephemeral.");
        } else {
            sd.setOnlyEphemeral(true);
            eb.setDescription("The bot's responses are now ephemeral.");
        }

        sdh.updateEntityData();
    }
}
