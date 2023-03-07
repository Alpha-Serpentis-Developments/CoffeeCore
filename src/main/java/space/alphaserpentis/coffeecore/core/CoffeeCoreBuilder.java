package space.alphaserpentis.coffeecore.core;

import io.reactivex.rxjava3.annotations.NonNull;
import space.alphaserpentis.coffeecore.data.bot.BotSettings;

import java.nio.file.Path;

public class CoffeeCoreBuilder {
    /**
     * Builds a CoffeeCore instance.
     * @param token The Discord bot token.
     * @param serverDataPath The path to the server data file.
     * @return A CoffeeCore instance.
     * @throws InterruptedException If the bot fails to connect to Discord.
     */
    public static CoffeeCore build(
            @NonNull String token,
            @NonNull BotSettings settings
            ) throws InterruptedException {
        return new CoffeeCore(token, settings.serverDataPath, settings.botOwnerId);
    }
}
