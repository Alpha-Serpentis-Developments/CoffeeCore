package dev.alphaserpentis.examples.coffeecore.java.hello;

import dev.alphaserpentis.coffeecore.core.CoffeeCore;
import dev.alphaserpentis.coffeecore.core.CoffeeCoreBuilder;
import dev.alphaserpentis.coffeecore.data.bot.AboutInformation;
import dev.alphaserpentis.coffeecore.data.bot.BotSettings;
import io.github.cdimascio.dotenv.Dotenv;

public class HelloWorld {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        AboutInformation aboutInformation = new AboutInformation(
                "This is the Hello World example!",
                "Powered by Coffee Core!",
                null,
                null,
                false,
                false
        );
        BotSettings botSettings = new BotSettings(
                Long.parseLong(dotenv.get("BOT_OWNER_ID")),
                dotenv.get("SERVER_DATA_PATH"),
                Boolean.parseBoolean(dotenv.get("UPDATE_COMMANDS_AT_LAUNCH")),
                Boolean.parseBoolean(dotenv.get("REGISTER_DEFAULT_COMMANDS")),
                aboutInformation
        );
        HelloCommandText helloCommandText = new HelloCommandText();
        HelloCommandEmbed helloCommandEmbed = new HelloCommandEmbed();
        HelloCommandButton helloCommandButton = new HelloCommandButton();
        HelloError helloError = new HelloError();
        CoffeeCoreBuilder<?> builder = new CoffeeCoreBuilder<>().setSettings(botSettings);
        CoffeeCore core = builder.build(dotenv.get("DISCORD_BOT_TOKEN"));

        core.registerCommands(helloCommandText, helloCommandEmbed, helloCommandButton, helloError);
    }
}
