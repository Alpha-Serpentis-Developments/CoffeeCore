package hello;

import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;

public class HelloCommandText extends BotCommand<String, SlashCommandInteractionEvent> {

    public HelloCommandText() {
        super(
                new BotCommandOptions(
                        "hello",
                        "Says hello to you!",
                        false,
                        false,
                        TypeOfEphemeral.DEFAULT
                )
        );
    }

    @Override
    @NonNull
    public CommandResponse<String> runCommand(
            long userId,
            @NonNull SlashCommandInteractionEvent event
    ) {
        return new CommandResponse<>(
                "Hello, " + event.getUser().getAsMention() + "!", isOnlyEphemeral()
        );
    }
}