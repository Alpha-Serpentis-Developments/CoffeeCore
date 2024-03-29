package dev.alphaserpentis.coffeecore.commands.defaultcommands;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import dev.alphaserpentis.coffeecore.data.entity.ServerData;
import dev.alphaserpentis.coffeecore.data.entity.UserData;
import dev.alphaserpentis.coffeecore.handler.api.discord.entities.DataHandler;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

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
        String subcommandGroup = Objects.requireNonNull(event.getSubcommandGroup());

        if(subcommandGroup.equals("user")) {
            eb.setTitle("User Settings");

            if(Objects.equals(event.getSubcommandName(), "fullerror")) {
                setUserFullError(userId, eb);
            } else {
                throw new IllegalStateException("Unexpected value: " + event.getSubcommandName());
            }
        } else if(subcommandGroup.equals("server")) {
            eb.setTitle("Server Settings");

            if(event.getGuild() == null) {
                eb.setDescription("You must run this command in a server.");
                eb.setColor(0xff0000);
                return new CommandResponse<>(isOnlyEphemeral(), eb.build());
            }

            if(isUserPermissioned(Objects.requireNonNull(event.getMember()))) {
                if(Objects.equals(event.getSubcommandName(), "ephemeral")) {
                    setServerEphemeral(event.getGuild().getIdLong(), eb);
                }
            } else {
                eb.setDescription(
                        """
                                You do not have permission to change the server settings.

                                You must have the `Manage Server` or `Administrator` permission to change the server settings."""
                );
                eb.setColor(0xff0000);
            }
        } else {
            throw new IllegalStateException("Unexpected value: " + subcommandGroup);
        }

        return new CommandResponse<>(isOnlyEphemeral(), eb.build());
    }

    @Override
    public void updateCommand(@NonNull JDA jda) {
        SubcommandGroupData userSettings = new SubcommandGroupData(
                "user",
                "Configure your personal settings with the bot"
        )
                .addSubcommands(
                        new SubcommandData(
                                "fullerror",
                                "Toggle whether or not the bot will show the full stack trace"
                        )
                );
        SubcommandGroupData serverSettings = new SubcommandGroupData(
                "server",
                "Configure the bot's settings for this server"
        )
                .addSubcommands(
                        new SubcommandData(
                                "ephemeral",
                                "Toggle whether the bot's responses are ephemeral"
                        )
                );

        jda.upsertCommand(name, description).addSubcommandGroups(userSettings, serverSettings).queue(
                (cmd) -> setGlobalCommandId(cmd.getIdLong())
        );
    }

    public boolean isUserPermissioned(@NonNull Member member) {
        return member.hasPermission(Permission.MANAGE_SERVER) || member.hasPermission(Permission.ADMINISTRATOR);
    }

    private void setUserFullError(long userId, EmbedBuilder eb) {
        DataHandler<?> dh = (DataHandler<?>) core.getDataHandler();
        UserData ud = (UserData) dh.getEntityData("user", userId);

        if(ud.getShowFullStackTrace()) {
            ud.setShowFullStackTrace(false);
            eb.setDescription("The bot will no longer show the full stack trace.");
        } else {
            ud.setShowFullStackTrace(true);
            eb.setDescription("The bot will now show the full stack trace.");
        }

        dh.updateEntityData();
    }

    private void setServerEphemeral(long guildId, EmbedBuilder eb) {
        DataHandler<?> dh = (DataHandler<?>) core.getDataHandler();
        ServerData sd = (ServerData) dh.getEntityData("guild", guildId);

        if(sd.getOnlyEphemeral()) {
            sd.setOnlyEphemeral(false);
            eb.setDescription("The bot's responses are no longer ephemeral.");
        } else {
            sd.setOnlyEphemeral(true);
            eb.setDescription("The bot's responses are now ephemeral.");
        }

        dh.updateEntityData();
    }
}
