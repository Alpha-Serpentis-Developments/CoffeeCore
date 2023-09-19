package hello;

import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;

public class HelloCommandEmbed extends BotCommand<MessageEmbed, SlashCommandInteractionEvent> {

    public HelloCommandEmbed() {
        super(
                new BotCommandOptions()
                        .setName("helloembed")
                        .setDescription("Says hello to you but in an embed!")
                        .setOnlyEmbed(true)
                        .setDeferReplies(true)
        );
    }

    @Override
    @NonNull
    public CommandResponse<MessageEmbed> runCommand(
            long userId,
            @NonNull SlashCommandInteractionEvent event
    ) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Hello!");
        eb.setDescription("Hello, " + event.getUser().getAsMention() + "!");
        eb.setColor(0x00FF00);

        return new CommandResponse<>(isOnlyEphemeral(), eb.build());
    }
}