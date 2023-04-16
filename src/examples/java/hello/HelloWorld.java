package hello;

import dev.alphaserpentis.coffeecore.core.CoffeeCore;
import dev.alphaserpentis.coffeecore.core.CoffeeCoreBuilder;
import dev.alphaserpentis.coffeecore.data.bot.BotSettings;
import io.github.cdimascio.dotenv.Dotenv;

public class HelloWorld {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        HelloCommandText helloCommandText = new HelloCommandText();
        HelloCommandEmbed helloCommandEmbed = new HelloCommandEmbed();
        HelloCommandButton helloCommandButton = new HelloCommandButton();

        CoffeeCoreBuilder<?> builder = new CoffeeCoreBuilder<>();
        builder.setSettings(
                new BotSettings(
                        Long.parseLong(dotenv.get("BOT_OWNER_ID")),
                        dotenv.get("SERVER_DATA_PATH"),
                        Boolean.parseBoolean(dotenv.get("UPDATE_COMMANDS_AT_LAUNCH")),
                        Boolean.parseBoolean(dotenv.get("REGISTER_DEFAULT_COMMANDS"))
                )
        );

        CoffeeCore core = builder.build(dotenv.get("DISCORD_BOT_TOKEN"));

        core.registerCommands(helloCommandText, helloCommandEmbed, helloCommandButton);
    }
}
