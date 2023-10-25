package dev.alphaserpentis.coffeecore.commands.defaultcommands;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Help extends BotCommand<MessageEmbed, SlashCommandInteractionEvent> {
    private static List<BotCommand<?, ?>> cachedSlashCommands = null;
    private static List<BotCommand<?, ?>> cachedUserContextCommands = null;
    private static List<BotCommand<?, ?>> cachedMessageContextCommands = null;

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
        StringBuilder sb = new StringBuilder();

        if(cachedSlashCommands == null) // This assumes everything else is null
            cacheCommands(getCore().getCommandsHandler().getCommands());
        if(!cachedSlashCommands.isEmpty())
            addCommandsToHelp(sb, cachedSlashCommands, "Slash Commands");
        if(!cachedUserContextCommands.isEmpty())
            addCommandsToHelp(sb, cachedUserContextCommands, "User Context Commands");
        if(!cachedMessageContextCommands.isEmpty())
            addCommandsToHelp(sb, cachedMessageContextCommands, "Message Context Commands");

        eb.setTitle("Help");
        eb.setFooter("Built using Coffee Core");
        eb.setDescription(
                "List of all the commands provided by " + event.getJDA().getSelfUser().getName() + "!\n" + sb
        );

        return new CommandResponse<>(isOnlyEphemeral(), eb.build());
    }

    private static void addCommandsToHelp(
            @NonNull StringBuilder sb,
            @NonNull List<BotCommand<?, ?>> botCmds,
            @NonNull String name
    ) {
        String desc;

        sb.append("### ").append(name).append("\n\n");

        for(BotCommand<?, ?> command: botCmds) {
            desc = Objects.requireNonNullElse(
                    command.getHelpDescription(),
                    Objects.requireNonNullElse(command.getDescription(), "Description not provided")
            );
            sb.append("**").append(command.getName()).append("**\n").append(desc).append('\n');
        }
    }

    private static void cacheCommands(@NonNull List<BotCommand<?, ?>> listOfCommands) {
        cachedSlashCommands = new ArrayList<>();
        cachedUserContextCommands = new ArrayList<>();
        cachedMessageContextCommands = new ArrayList<>();

        listOfCommands.forEach(cmd -> {
            switch(cmd.getCommandType()) {
                case SLASH, UNKNOWN -> cachedSlashCommands.add(cmd);
                case USER -> cachedUserContextCommands.add(cmd);
                case MESSAGE -> cachedMessageContextCommands.add(cmd);
                default -> throw new IllegalStateException("Unexpected value: " + cmd.getCommandType());
            }
        });
    }
}
