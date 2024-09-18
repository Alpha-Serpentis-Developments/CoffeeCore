package dev.alphaserpentis.coffeecore.data.bot

/**
 * Class used to contain settings for the bot.
 */
class BotSettings(
    /**
     * The Discord user ID of the bot owner
     */
    val botOwnerId: Long,
    /**
     * The path to the server data file.
     */
    val serverDataPath: String?,
    /**
     * Whether to update the commands at launch.
     */
    val isUpdateCommandsAtLaunch: Boolean,
    /**
     * Whether to register the default commands.
     */
    val isRegisterDefaultCommands: Boolean,
    /**
     * Whether to default to show the full stack trace of errors to users
     */
    val defaultShowFullStackTrace: Boolean = false,
    /**
     * Optional information to highlight what the bot is about.
     */
    var aboutInformation: AboutInformation? = null
)
