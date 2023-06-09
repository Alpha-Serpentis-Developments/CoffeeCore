package dev.alphaserpentis.coffeecore.commands.defaultcommands;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.AboutInformation;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import dev.alphaserpentis.coffeecore.helper.ContainerHelper;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class About extends BotCommand<MessageEmbed, SlashCommandInteractionEvent> {

    private static final String DEFAULT_DESCRIPTION = """
    A bot powered by Coffee Core!
    
    https://github.com/Alpha-Serpentis-Developments/CoffeeCore
    """;
    private static final String DEFAULT_FOOTER = "Powered by Coffee Core";

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
        AboutInformation info = core.getAboutInformation();
        EmbedBuilder eb = new EmbedBuilder();
        boolean isSharded = core.isSharded();

        eb.setTitle("About " + event.getJDA().getSelfUser().getName());

        eb.setDescription(info.description() != null ? info.description() : DEFAULT_DESCRIPTION);
        eb.setFooter(info.footer() != null ? info.footer() : DEFAULT_FOOTER, event.getUser().getAvatarUrl());
        if(info.color() != null) eb.setColor(info.color());
        if(info.fields() != null) info.fields().forEach(eb::addField);
        if(info.displayShardingInfo())
            eb.addField(
                "Sharding",
                "Active: " + (isSharded ? "Yes" : "No") + "\nTotal Shards: " +
                        (isSharded ? event.getJDA().getShardInfo().getShardTotal() : 1) +
                        "\nShard ID: " + (isSharded ? event.getJDA().getShardInfo().getShardId() : 0),
                false
            );
        if(info.displayServersInfo())
            eb.addField(
                "Servers",
                "Total: " + new ContainerHelper(core.getActiveContainer()).getGuilds().size(),
                false
            );

        return new CommandResponse<>(eb.build(), isOnlyEphemeral());
    }

}
