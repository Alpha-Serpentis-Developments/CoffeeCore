import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import space.alphaserpentis.coffeecore.commands.BotCommand;
import space.alphaserpentis.coffeecore.core.CoffeeCore;
import space.alphaserpentis.coffeecore.core.CoffeeCoreBuilder;
import space.alphaserpentis.coffeecore.data.bot.BotSettings;
import space.alphaserpentis.coffeecore.data.bot.CommandResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HelloWorld {

    public static class HelloCommand extends BotCommand<MessageEmbed> {

        @Override
        public CommandResponse<MessageEmbed> runCommand(long userId, SlashCommandInteractionEvent event) {
            return null;
        }
    }

    public static CoffeeCore core;

    public static void main(String[] args) throws InterruptedException, IOException {
        core = CoffeeCoreBuilder.build(
                System.getenv("DISCORD_BOT_TOKEN"),
                new Gson().fromJson(
                        Files.newBufferedReader(Path.of(args[0])),
                        BotSettings.class
                )
        );


    }
}
