package space.alphaserpentis.coffeecore.core;

import io.reactivex.rxjava3.annotations.NonNull;
import space.alphaserpentis.coffeecore.data.bot.BotSettings;
import space.alphaserpentis.coffeecore.handler.api.discord.servers.ServerDataHandler;

import java.io.IOException;
import java.nio.file.Path;

public class CoffeeCoreBuilder {
    /**
     * Builds a CoffeeCore instance.
     * @param token The Discord bot token.
     * @param settings The bot settings.
     * @return A CoffeeCore instance.
     * @throws InterruptedException If the bot fails to connect to Discord.
     * @throws IOException If the bot fails to read or write to the server data file.
     */
    public static CoffeeCore build(
            @NonNull String token,
            @NonNull BotSettings settings
    ) throws InterruptedException, IOException {
        CoffeeCore core = new CoffeeCore(token, settings);
        ServerDataHandler.init(core);

        return core;
    }
}
