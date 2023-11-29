package dev.alphaserpentis.examples.coffeecore.kotlin.hello

import dev.alphaserpentis.coffeecore.commands.ButtonCommand
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.util.*

class HelloCommandButton: ButtonCommand<MessageEmbed, SlashCommandInteractionEvent>(
    BotCommandOptions().apply {
        setName("hellobutton")
        setDescription("Says hello to you using a button!")
        setOnlyEmbed(true)
        setDeferReplies(true)
    }
) {
    init {
        addButton("hello", ButtonStyle.PRIMARY, "Hello!", false)
        addButton("goodbye", ButtonStyle.DANGER, "Goodbye!", Emoji.fromUnicode("U+1F641"), false)
        addButton("mystery", ButtonStyle.SECONDARY, "Mystery!", Emoji.fromUnicode("U+1F914"), true)
    }

    override fun runCommand(userId: Long, event: SlashCommandInteractionEvent): CommandResponse<MessageEmbed> {
        val eb = EmbedBuilder().setTitle("Hello!").setDescription("Hello, ${event.user.asMention}!").setColor(0x00FF00)

        return CommandResponse(isOnlyEphemeral, eb.build())
    }

    override fun runButtonInteraction(event: ButtonInteractionEvent): Optional<Any> {
        when (val key = convertComponentIdToKey(event.componentId)) {
            "hello" -> event.reply("Hello, ${event.user.asMention}!").queue()
            "goodbye" -> event.reply("Goodbye, ${event.user.asMention}!").queue()
            "mystery" -> event.reply("How did you click on this?").queue()
            else -> throw IllegalStateException("Unexpected value: $key")
        }

        return Optional.empty()
    }

    override fun addButtonsToMessage(event: SlashCommandInteractionEvent): Collection<ItemComponent> {
        return listOf(getButton("hello")!!, getButton("goodbye")!!, getButton("mystery")!!)
    }
}