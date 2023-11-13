package dev.alphaserpentis.examples.coffeecore.kotlin.custom

import com.google.gson.reflect.TypeToken
import dev.alphaserpentis.coffeecore.core.CoffeeCoreBuilder
import dev.alphaserpentis.coffeecore.data.bot.BotSettings
import dev.alphaserpentis.examples.coffeecore.kotlin.custom.deserializer.CustomServerDataDeserializer
import dev.alphaserpentis.examples.coffeecore.kotlin.custom.handler.CustomServerData
import dev.alphaserpentis.examples.coffeecore.kotlin.custom.handler.CustomServerDataHandler
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.JDABuilder
import java.nio.file.Path

fun main() {
    val dotenv = Dotenv.load()
    val setCustomData = SetCustomData()
    val botSettings = BotSettings(
        dotenv["BOT_OWNER_ID"].toLong(),
        dotenv["SERVER_DATA_PATH"],
        dotenv["UPDATE_COMMANDS_AT_LAUNCH"].toBoolean(),
        dotenv["REGISTER_DEFAULT_COMMANDS"].toBoolean()
    )
    val builder: CoffeeCoreBuilder<*> = CoffeeCoreBuilder<JDABuilder>().setSettings(botSettings).setServerDataHandler(
        CustomServerDataHandler(
            Path.of(dotenv["SERVER_DATA_PATH"]),
            object : TypeToken<Map<Long?, CustomServerData?>?>() {},
            CustomServerDataDeserializer()
        )
    )
    builder.build(dotenv["DISCORD_BOT_TOKEN"]).registerCommands(setCustomData)
}
