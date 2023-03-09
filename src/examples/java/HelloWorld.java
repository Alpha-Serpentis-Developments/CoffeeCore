import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import space.alphaserpentis.coffeecore.commands.BotCommand;
import space.alphaserpentis.coffeecore.core.CoffeeCore;
import space.alphaserpentis.coffeecore.core.CoffeeCoreBuilder;
import space.alphaserpentis.coffeecore.data.bot.BotSettings;
import space.alphaserpentis.coffeecore.data.bot.CommandResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HelloWorld {

    public static class HelloCommand extends BotCommand<MessageEmbed> {

        public HelloCommand() {
            super(
                    new BotCommandOptions(
                            "hello",
                            "Says hello to you!",
                            true,
                            false,
                            TypeOfEphemeral.DEFAULT
                    )
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

    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
        Dotenv dotenv = Dotenv.load();
        HelloCommand helloCommand = new HelloCommand();
        CoffeeCore core = CoffeeCoreBuilder.build(
                dotenv.get("DISCORD_BOT_TOKEN"),
                new Gson().fromJson(
                        Files.newBufferedReader(
                                Path.of(
                                        Paths.get(
                                                HelloWorld.class.getClassLoader().getResource("settings.json").toURI()
                                        ).toString()
                                )
                        ),
                        BotSettings.class
                )
        );

        core.registerCommands(helloCommand);
    }
}
