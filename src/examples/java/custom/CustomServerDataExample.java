package custom;

import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import custom.deserializer.CustomServerDataDeserializer;
import custom.handler.CustomServerData;
import custom.handler.CustomServerDataHandler;
import dev.alphaserpentis.coffeecore.core.CoffeeCoreBuilder;
import dev.alphaserpentis.coffeecore.data.bot.BotSettings;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Map;

public class CustomServerDataExample {
    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Dotenv dotenv = Dotenv.load();
        SetCustomData setCustomData = new SetCustomData();

        CoffeeCoreBuilder builder = new CoffeeCoreBuilder();
        builder.setSettings(
                new BotSettings(
                        Long.parseLong(dotenv.get("BOT_OWNER_ID")),
                        dotenv.get("SERVER_DATA_PATH"),
                        Boolean.parseBoolean(dotenv.get("UPDATE_COMMANDS_AT_LAUNCH")),
                        Boolean.parseBoolean(dotenv.get("REGISTER_DEFAULT_COMMANDS"))
                )
        );

        builder.setServerDataHandler(CustomServerDataHandler.class.getDeclaredConstructor(Path.class, TypeToken.class, JsonDeserializer.class));
        builder.setServerDataHandlerParameters(Path.of(dotenv.get("SERVER_DATA_PATH")), new TypeToken<Map<Long, CustomServerData>>() {}, new CustomServerDataDeserializer());

        builder.build(dotenv.get("DISCORD_BOT_TOKEN")).registerCommands(setCustomData);
    }
}
