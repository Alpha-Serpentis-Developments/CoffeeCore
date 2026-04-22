package dev.alphaserpentis.examples.coffeecore.kotlin.dev.alphaserpentis.examples.coffeecore.custom

import dev.alphaserpentis.coffeecore.commands.types.BotCommand
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse
import dev.alphaserpentis.examples.coffeecore.kotlin.dev.alphaserpentis.examples.coffeecore.custom.handler.CustomDataHandler
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

class SetCustomData : BotCommand<String, SlashCommandInteractionEvent>(
    BotCommandOptions().apply {
        setName("set")
        setDescription("Sets the custom data of the server")
        setOnlyEmbed(false)
        setOnlyEphemeral(false)
    }
) {
    override fun runCommand(userId: Long, event: SlashCommandInteractionEvent): CommandResponse<String> {
        val handler = core.dataHandler as CustomDataHandler
        val customServerData = handler.getEntityData("guild", event.guild!!.idLong)!!
        val dataHandler = core.dataHandler as CustomDataHandler?

        if (dataHandler != null) {
            return when (val subcommand = event.subcommandName!!) {
                "view" -> CommandResponse(
                    isOnlyEphemeral,
                    "The custom data of this server is: " + customServerData.customData
                )
                "set" -> {
                    val data = event.getOption("data")!!.asString
                    customServerData.customData = data
                    dataHandler.updateEntityData()
                    CommandResponse(isOnlyEphemeral, "The custom data of this server has been set to: $data")
                }
                else -> throw RuntimeException("Unknown subcommand: $subcommand")
            }
        } else {
            return CommandResponse(isOnlyEphemeral, "Bot is running without a data handler!")
        }
    }

    override fun updateCommand(jda: JDA) {
        val view = SubcommandData("view", "View the custom data of the server.")
        val set = SubcommandData("set", "Set the custom data of the server.")
            .addOption(OptionType.STRING, "data", "The data to set.", true)
        val cmd = jda.upsertCommand(name, description).addSubcommands(view, set).complete()

        setGlobalCommandId(cmd.idLong)
    }
}
