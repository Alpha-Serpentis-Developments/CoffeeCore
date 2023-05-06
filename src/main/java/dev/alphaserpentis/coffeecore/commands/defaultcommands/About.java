package dev.alphaserpentis.coffeecore.commands.defaultcommands;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class About extends BotCommand<MessageEmbed> {

    public About() {
        super(
                new BotCommandOptions()
                        .setName("about")
                        .setDescription("Shows information about the bot")
                        .setOnlyEmbed(true)
        );
    }

    @Override
    @NonNull
    public CommandResponse<MessageEmbed> runCommand(long userId, @NonNull SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        boolean isSharded = core.isSharded();

        eb.setTitle("About " + event.getJDA().getSelfUser().getName());
        eb.setDescription(core.getAboutDescription());
        eb.addField(
                "Sharding",
                "Active: " + (isSharded ? "Yes" : "No") + "\nTotal Shards: " +
                        (isSharded ? core.getShardManager().getShards().size() : 1) +
                        "\nShard ID: " + (isSharded ? event.getJDA().getShardInfo().getShardId() : 0),
                false
        );

        return new CommandResponse<>(eb.build(), isOnlyEphemeral());
    }

}
