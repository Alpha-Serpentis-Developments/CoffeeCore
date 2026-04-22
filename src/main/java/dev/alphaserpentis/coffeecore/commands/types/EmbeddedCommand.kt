package dev.alphaserpentis.coffeecore.commands.types

import dev.alphaserpentis.coffeecore.data.bot.CommandResponse
import dev.alphaserpentis.coffeecore.hook.CommandHook
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction
import net.dv8tion.jda.api.utils.FileUpload

/**
 * Abstract class for commands that strictly return [MessageEmbed]s.
 */
abstract class EmbeddedCommand<E : GenericCommandInteractionEvent>(
    options: BotCommandOptions
) : BotCommand<MessageEmbed, E>(options.setOnlyEmbed(true)) {
    abstract override fun runCommand(userId: Long, event: E): CommandResponse<MessageEmbed>

    override fun processDeferredCommand(event: E) = deferredCommandLogic(event, this)

    override fun processNonDeferredCommand(event: E) = nonDeferredCommandLogic(event, this)

    companion object {
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <E : GenericCommandInteractionEvent> deferredCommandLogic(
            event: E,
            cmd: BotCommand<MessageEmbed, E>
        ): WebhookMessageCreateAction<*> {
            val userId = event.user.idLong
            val interactionHook = event.hook

            try {
                val preExecHooks = cmd.commandHooks.stream()
                    .filter { it.typeOfHook == CommandHook.Type.PRE_EXECUTION}
                    .toList()

                event.deferReply(cmd.determineEphemeralStatus(event)).complete()

                if (preExecHooks.isNotEmpty()) {
                    val embeds: ArrayList<MessageEmbed> = ArrayList()
                    val files: ArrayList<FileUpload> = ArrayList()

                    preExecHooks.forEach { cmdHook ->
                        cmdHook
                            .execute(cmd, event, null)
                            .ifPresent { rawResponse ->
                                if (rawResponse is CommandResponse<*>) {
                                    val msgResponse = rawResponse.messageResponse

                                    if (msgResponse[0] is String)
                                        throw IllegalStateException("Message response cannot be a String for EmbeddedCommand")

                                    embeds.addAll((rawResponse as CommandResponse<MessageEmbed>).messageResponse)
                                }

                                (rawResponse as CommandResponse<MessageEmbed>).fileUpload.use {
                                    if (it != null) files.add(it)
                                }
                            }
                    }

                    if (embeds.isNotEmpty()) {
                        return if (files.isNotEmpty())
                            interactionHook.sendMessageEmbeds(embeds).addFiles(files)
                        else
                            interactionHook.sendMessageEmbeds(embeds)
                    }
                }

                val response = cmd.retrieveAndProcessResponse(userId, event)

                // Ensures that the response is a valid MessageEmbed
                // TODO: Try to find a way to prevent this at compile time instead of runtime
                try {
                    response.key.first()
                } catch (_: ClassCastException) {
                    throw IllegalStateException("Message response cannot be a String for EmbeddedCommand")
                }

                return if (response.value == null)
                    interactionHook.sendMessageEmbeds(response.key.toList())
                else
                    interactionHook.sendMessageEmbeds(response.key.toList()).addFiles(response.value)
            } catch(e: Exception) {
                if (cmd.isForgivingRatelimitOnError)
                    cmd.ratelimitMap.remove(userId)

                return interactionHook.sendMessageEmbeds(cmd.handleError(e, userId))
            }
        }

        @JvmStatic
        fun <E : GenericCommandInteractionEvent> nonDeferredCommandLogic(
            event: E,
            cmd: BotCommand<MessageEmbed, E>
        ): ReplyCallbackAction {
            val userId = event.user.idLong

            try {
                val response = cmd.retrieveAndProcessResponse(userId, event)
                val msgIsEphemeral = cmd.determineEphemeralStatus(event)

                return if (response.value == null)
                    event
                        .replyEmbeds(response.key.toList())
                        .setEphemeral(msgIsEphemeral)
                else
                    event
                        .replyEmbeds(response.key.toList())
                        .setEphemeral(msgIsEphemeral)
                        .addFiles(response.value)
            } catch(e: Exception) {
                if (cmd.isForgivingRatelimitOnError)
                    cmd.ratelimitMap.remove(userId)

                return event.replyEmbeds(cmd.handleError(e, userId))
            }
        }
    }
}