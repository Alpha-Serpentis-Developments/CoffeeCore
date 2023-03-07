package space.alphaserpentis.coffeecore.handler.api.discord.commands;

import io.reactivex.rxjava3.annotations.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import space.alphaserpentis.coffeecore.commands.BotCommand;
import space.alphaserpentis.coffeecore.commands.ButtonCommand;
import space.alphaserpentis.coffeecore.commands.ModalCommand;
import space.alphaserpentis.coffeecore.core.CoffeeCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandsHandler extends ListenerAdapter {
    public static final HashMap<String, BotCommand<?>> mappingOfCommands = new HashMap<>();
    public static final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Provided a mapping of commands, this will check for any commands that are already registered and update them if
     * necessary. If the command is not registered, it will register it.
     * @param mappingOfCommands The mapping of commands to check and register
     * @param updateCommands Whether to update the commands if they are already registered
     */
    public static void checkAndSetSlashCommands(
            @NonNull HashMap<String, BotCommand<?>> mappingOfCommands,
            boolean updateCommands
    ) {
        JDA api = CoffeeCore.api;
        List<Command> listOfActiveCommands = api.retrieveCommands().complete();
        List<String> detectedCommandNames = new ArrayList<>();

        CommandsHandler.mappingOfCommands.putAll(mappingOfCommands);

        // Checks for the detected commands
        for (Iterator<Command> it = listOfActiveCommands.iterator(); it.hasNext(); ) {
            Command cmd = it.next();
            if(mappingOfCommands.containsKey(cmd.getName())) {
                BotCommand<?> botCmd = mappingOfCommands.get(cmd.getName());
                botCmd.setCommandId(cmd.getIdLong());
                if(updateCommands)
                    botCmd.updateCommand(api);

                detectedCommandNames.add(cmd.getName());

                it.remove();
            }
        }

        // Fills in any gaps or removes any commands
        for(Command cmd: listOfActiveCommands) { // Removes unused commands
            api.deleteCommandById(cmd.getId()).complete();
        }

        if(detectedCommandNames.size() < mappingOfCommands.size()) { // Adds new commands
            List<String> missingCommands = new ArrayList<>(mappingOfCommands.keySet());

            missingCommands.removeAll(detectedCommandNames);

            for(String cmdName: missingCommands) {
                BotCommand<?> cmd = mappingOfCommands.get(cmdName);
                cmd.updateCommand(api);
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        executor.submit(() -> {
            BotCommand<?> cmd = mappingOfCommands.get(event.getName());
            BotCommand.handleReply(event, cmd);
        });
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        executor.submit(() -> {
            BotCommand<?> cmd = mappingOfCommands.get(event.getButton().getId().substring(0, event.getButton().getId().indexOf("_")));

            ((ButtonCommand<?>) cmd).runButtonInteraction(event);
        });
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        executor.submit(() -> {
            BotCommand<?> cmd = mappingOfCommands.get(event.getModalId().substring(0, event.getModalId().indexOf("_")));

            ((ModalCommand) cmd).runModalInteraction(event);
        });
    }
}