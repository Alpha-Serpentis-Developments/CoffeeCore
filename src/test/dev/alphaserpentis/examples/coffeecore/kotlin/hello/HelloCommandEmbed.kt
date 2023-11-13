package hello

import dev.alphaserpentis.coffeecore.commands.BotCommand
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class HelloCommandEmbed: BotCommand<MessageEmbed, SlashCommandInteractionEvent>(
    BotCommandOptions().apply {
        setName("helloembed")
        setDescription("Says hello to you but in an embed!")
        setOnlyEmbed(true)
        setDeferReplies(true)
    }
) {
    override fun runCommand(userId: Long, event: SlashCommandInteractionEvent): CommandResponse<MessageEmbed> {
        val eb = EmbedBuilder().setTitle("Hello!").setDescription("Hello, ${event.user.asMention}!").setColor(0x00FF00)

        return CommandResponse(isOnlyEphemeral, eb.build())
    }
}