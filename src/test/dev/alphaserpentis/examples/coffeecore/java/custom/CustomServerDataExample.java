package dev.alphaserpentis.examples.coffeecore.java.custom;

import dev.alphaserpentis.examples.coffeecore.java.custom.deserializer.CustomServerDataDeserializer;
import dev.alphaserpentis.examples.coffeecore.java.custom.handler.CustomServerDataHandler;
import com.google.gson.reflect.TypeToken;
import dev.alphaserpentis.coffeecore.core.CoffeeCoreBuilder;
import dev.alphaserpentis.coffeecore.data.bot.BotSettings;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.nio.file.Path;

public class CustomServerDataExample {
    public static void main(String[] args) throws IOException {
        Dotenv dotenv = Dotenv.load();
        SetCustomData setCustomData = new SetCustomData();
        BotSettings botSettings = new BotSettings(
                Long.parseLong(dotenv.get("BOT_OWNER_ID")),
                dotenv.get("SERVER_DATA_PATH"),
                Boolean.parseBoolean(dotenv.get("UPDATE_COMMANDS_AT_LAUNCH")),
                Boolean.parseBoolean(dotenv.get("REGISTER_DEFAULT_COMMANDS"))
        );
        CoffeeCoreBuilder<?> builder = new CoffeeCoreBuilder<>().setSettings(botSettings).setServerDataHandler(
                new CustomServerDataHandler(
                        Path.of(dotenv.get("SERVER_DATA_PATH")),
                        new TypeToken<>() {},
                        new CustomServerDataDeserializer()
                )
        );

        builder.build(dotenv.get("DISCORD_BOT_TOKEN")).registerCommands(setCustomData);
    }
}
