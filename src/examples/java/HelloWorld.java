import com.google.gson.Gson;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import space.alphaserpentis.coffeecore.commands.BotCommand;
import space.alphaserpentis.coffeecore.core.CoffeeCore;
import space.alphaserpentis.coffeecore.core.CoffeeCoreBuilder;
import space.alphaserpentis.coffeecore.data.bot.BotSettings;
import space.alphaserpentis.coffeecore.data.bot.CommandResponse;
import space.alphaserpentis.coffeecore.handler.api.discord.commands.CommandsHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HelloWorld {

    public static class HelloCommand extends BotCommand<MessageEmbed> {

        public HelloCommand() {
            super(
                    new BotCommandOptions("hello", "Says hello to you!")
            );
        }

        @Override
        public @NonNull CommandResponse<MessageEmbed> runCommand(
                long userId,
                @NonNull SlashCommandInteractionEvent event
        ) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("Hello!");
            eb.setDescription("Hello, " + event.getUser().getAsMention() + "!");
            eb.setColor(0x00FF00);

            return new CommandResponse<>(eb.build(), isOnlyEphemeral());
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        HelloCommand helloCommand = new HelloCommand();
        System.out.println(System.getenv());
        CoffeeCore core = CoffeeCoreBuilder.build(
                System.getenv("DISCORD_BOT_TOKEN"),
                new Gson().fromJson(
                        Files.newBufferedReader(Path.of(args[0])),
                        BotSettings.class
                )
        );

        core.registerCommands(helloCommand);
    }
}
