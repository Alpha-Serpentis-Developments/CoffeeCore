package hello;

import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import dev.alphaserpentis.coffeecore.commands.ButtonCommand;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;

import java.util.Arrays;
import java.util.Collection;

public class HelloCommandButton extends ButtonCommand<MessageEmbed> {

    public HelloCommandButton() {
        super(
                new BotCommandOptions(
                        "hellobutton",
                        "Says hello to you using a button!",
                        true,
                        false,
                        TypeOfEphemeral.DEFAULT
                )
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

        return new CommandResponse<>(eb.build(), isOnlyEphemeral());
    }

    @Override
    public void runButtonInteraction(@NonNull ButtonInteractionEvent event) {
        String componentId = event.getComponentId().substring(getName().length() + 1);

        if (componentId.equals("hello")) {
            event.reply("Hello, " + event.getUser().getAsMention() + "!").queue();
        } else if (componentId.equals("goodbye")) {
            event.reply("Goodbye, " + event.getUser().getAsMention() + "!").queue();
        } else if (componentId.equals("mystery")) {
            event.reply("How did you click on this?").queue();
        }
    }

    @Override
    @NonNull
    public Collection<ItemComponent> addButtons(@NonNull GenericCommandInteractionEvent event) {
        return Arrays.asList(new ItemComponent[] {
                getButton("hello"),
                getButton("goodbye"),
                getButton("mystery")
        });
    }
}