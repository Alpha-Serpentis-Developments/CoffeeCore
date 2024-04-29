package dev.alphaserpentis.coffeecore.commands.defaultcommands

import dev.alphaserpentis.coffeecore.commands.BotCommand
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.requests.RestAction

/**
 * A command to force sync commands with the server
 */
open class Sync(options: BotCommandOptions) : BotCommand<MessageEmbed, SlashCommandInteractionEvent>(options) {

    constructor() : this(
        BotCommandOptions("sync", "Force sync commands with the server")
            .setOnlyEmbed(true)
            .setDeferReplies(true)
            .setUseRatelimits(true)
            .setRatelimitLength(600)
    )

    override fun runCommand(userId: Long, event: SlashCommandInteractionEvent): CommandResponse<MessageEmbed> {
        if (event.guild == null) {
            return CommandResponse(true, "This command can only be used in a server!")
        } else if (event.member?.hasPermission(Permission.ADMINISTRATOR) == true) {
            return CommandResponse(true, "You must be an administrator to use this command!")
        }

        val restActions: ArrayList<RestAction<*>> = ArrayList()

        core.commandsHandler.commands
            .filter { it.commandVisibility == CommandVisibility.GUILD }
            .forEach {
                restActions.add(
                    event.guild!!.jda.upsertCommand(getJDACommandData(it.commandType, it.name, it.description))
                )
            }

        when (restActions.size) {
            0 -> return CommandResponse(true, "No commands to sync!")
            1 -> restActions[0].queue()
            2 -> restActions[0].and(restActions[1]).queue()
            else -> {
                var combinedAction = restActions[0].and(restActions[1])
                for (i in 2 until restActions.size) {
                    combinedAction = combinedAction.and(restActions[i])
                }
                combinedAction.queue()
            }
        }

        return CommandResponse(true, "Commands have been synced with the server!")
    }
}