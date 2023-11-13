package hello

import dev.alphaserpentis.coffeecore.commands.BotCommand
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class HelloCommandText: BotCommand<String, SlashCommandInteractionEvent>(
    BotCommandOptions().apply {
        setName("hello")
        setDescription("Says hello to you!")
        setOnlyEmbed(false)
        setOnlyEphemeral(false)
    }
) {
    override fun runCommand(userId: Long, event: SlashCommandInteractionEvent): CommandResponse<String> {
        return CommandResponse(isOnlyEphemeral, "Hello, ${event.user.asMention}!")
    }
}