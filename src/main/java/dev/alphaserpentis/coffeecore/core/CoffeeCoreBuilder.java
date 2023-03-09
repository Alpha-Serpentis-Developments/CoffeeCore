package dev.alphaserpentis.coffeecore.core;

import dev.alphaserpentis.coffeecore.data.bot.BotSettings;
import io.reactivex.rxjava3.annotations.NonNull;
import dev.alphaserpentis.coffeecore.handler.api.discord.servers.ServerDataHandler;

import java.io.IOException;

/**
 * Builder class for CoffeeCore
 */
public class CoffeeCoreBuilder {
    /**
     * Builds a {@link CoffeeCore} instance and automatically initializes {@link ServerDataHandler}.
     * <p>Coffee Core will shut down if the bot is misconfigured within Discord (e.g., invalid token, invalid permissions).
     * @param token The Discord bot token.
     * @param settings The bot settings.
     * @return {@link CoffeeCore}.
     * @throws IOException If the bot fails to read or write to the server data file.
     */
    public static CoffeeCore build(
            @NonNull String token,
            @NonNull BotSettings settings
    ) throws IOException {
        CoffeeCore core = new CoffeeCore(token, settings);
        ServerDataHandler.init(core);

        return core;
    }
}
