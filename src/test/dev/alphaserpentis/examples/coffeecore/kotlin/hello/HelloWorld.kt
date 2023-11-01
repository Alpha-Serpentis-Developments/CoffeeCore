package hello

import dev.alphaserpentis.coffeecore.core.CoffeeCore
import dev.alphaserpentis.coffeecore.core.CoffeeCoreBuilder
import dev.alphaserpentis.coffeecore.data.bot.AboutInformation
import dev.alphaserpentis.coffeecore.data.bot.BotSettings
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.JDABuilder
import dev.alphaserpentis.examples.coffeecore.kotlin.hello.HelloCommandButton

fun main() {
    val dotenv: Dotenv = Dotenv.load()
    val aboutInfo = AboutInformation(
        "This is the Hello World example!",
        "Powered by Coffee Core!",
        null,
        null,
        false,
        false
    )
    val botSettings = BotSettings(
        dotenv["BOT_OWNER_ID"].toLong(),
        dotenv["SERVER_DATA_PATH"],
        dotenv["UPDATE_COMMANDS_AT_LAUNCH"].toBoolean(),
        dotenv["REGISTER_DEFAULT_COMMANDS"].toBoolean(),
        aboutInfo
    )
    val helloCmdText = HelloCommandText()
    val helloCmdEmbed = HelloCommandEmbed()
    val helloCmdButton = HelloCommandButton()
    val builder: CoffeeCoreBuilder<*> = CoffeeCoreBuilder<JDABuilder>().setSettings(botSettings)
    val core: CoffeeCore = builder.build(dotenv.get("DISCORD_BOT_TOKEN"))

    core.registerCommands(helloCmdText, helloCmdEmbed, helloCmdButton)
}