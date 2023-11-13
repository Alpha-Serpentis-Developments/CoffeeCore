package dev.alphaserpentis.coffeecore.commands.defaultcommands;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * Default command that restarts the specified shard. This will not work on non-sharded bots!
 */
public class Restart extends BotCommand<MessageEmbed, SlashCommandInteractionEvent> {

    public Restart() {
        super(
                new BotCommandOptions()
                        .setName("restart")
                        .setDescription("Restarts a shard. This may or may not respond.")
                        .setOnlyEmbed(true)
                        .setDeferReplies(true)
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

            return new CommandResponse<>(isOnlyEphemeral(), eb.build());
        }

        // Verify the bot is sharded
        if(core.isSharded()) {
            eb.setTitle("Restarting Shard");
            eb.setDescription("The bot is restarting the shard.");
            eb.setColor(0xff0000);
            core.restart(event.getOptions().get(0).getAsInt());
        } else {
            eb.setTitle("Unable to Restart");
            eb.setDescription("This bot is not sharded.");
            eb.setColor(0xff0000);
        }

        return new CommandResponse<>(isOnlyEphemeral(), eb.build());
    }

    @Override
    public void updateCommand(@NonNull JDA jda) {
        Command cmd = jda.upsertCommand(name, description)
                .addOption(OptionType.INTEGER, "shard", "The shard to restart.", true)
                .complete();

        setGlobalCommandId(cmd.getIdLong());
    }
}
