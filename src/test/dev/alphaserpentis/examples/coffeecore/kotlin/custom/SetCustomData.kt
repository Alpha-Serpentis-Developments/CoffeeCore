package dev.alphaserpentis.examples.coffeecore.kotlin.custom

import dev.alphaserpentis.coffeecore.commands.BotCommand
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse
import dev.alphaserpentis.examples.coffeecore.kotlin.custom.handler.CustomServerDataHandler
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
        val handler = core.serverDataHandler as CustomServerDataHandler
        val customServerData = handler.getServerData(event.guild!!.idLong)!!
        return when (val subcommand = event.subcommandName!!) {
            "view" -> CommandResponse(
                isOnlyEphemeral,
                "The custom data of this server is: " + customServerData.customData
            )
            "set" -> {
                val data = event.getOption("data")!!.asString
                customServerData.customData = data
                core.serverDataHandler.updateServerData()
                CommandResponse(isOnlyEphemeral, "The custom data of this server has been set to: $data")
            }
            else -> throw RuntimeException("Unknown subcommand: $subcommand")
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
