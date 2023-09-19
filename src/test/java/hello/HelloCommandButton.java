package hello;

import dev.alphaserpentis.coffeecore.commands.ButtonCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.Arrays;
import java.util.Collection;

public class HelloCommandButton extends ButtonCommand<MessageEmbed, SlashCommandInteractionEvent> {

    public HelloCommandButton() {
        super(
                new BotCommandOptions()
                        .setName("hellobutton")
                        .setDescription("Says hello to you using a button!")
                        .setOnlyEmbed(true)
                        .setDeferReplies(true)
        );

        addButton("hello", ButtonStyle.PRIMARY, "Hello!", false);
        addButton("goodbye", ButtonStyle.DANGER, "Goodbye!", Emoji.fromUnicode("U+1F641"), false);
        addButton("mystery", ButtonStyle.SECONDARY, "Mystery!", Emoji.fromUnicode("U+1F914"), true);
    }

    @Override
    @NonNull
    public CommandResponse<MessageEmbed> runCommand(long userId, @NonNull SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Hello!");
        eb.setDescription("Hello, " + event.getUser().getAsMention() + "!");
        eb.setColor(0x00FF00);

        return new CommandResponse<>(isOnlyEphemeral(), eb.build());
    }

    @Override
    public void runButtonInteraction(@NonNull ButtonInteractionEvent event) {
        String key = convertComponentIdToKey(event.getComponentId());

        switch (key) {
            case "hello" -> event.reply("Hello, " + event.getUser().getAsMention() + "!").queue();
            case "goodbye" -> event.reply("Goodbye, " + event.getUser().getAsMention() + "!").queue();
            case "mystery" -> event.reply("How did you click on this?").queue();
            default -> throw new IllegalStateException("Unexpected value: " + key);
        }
    }

    @Override
    @NonNull
    public Collection<ItemComponent> addButtonsToMessage(@NonNull SlashCommandInteractionEvent event) {
        return Arrays.asList(new ItemComponent[] {
                getButton("hello"),
                getButton("goodbye"),
                getButton("mystery")
        });
    }
}