package dev.alphaserpentis.coffeecore.handler.api.discord.commands;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.commands.ButtonCommand;
import dev.alphaserpentis.coffeecore.commands.ModalCommand;
import dev.alphaserpentis.coffeecore.core.CoffeeCore;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

/**
 * The handler for all the commands to be registered with the bot. This handles registration and execution of the commands.
 */
public class CommandsHandler extends ListenerAdapter {
    /**
     * The mapping of {@link BotCommand} that have been registered to the bot. This is used to check for commands that are already
     * registered and update them if necessary.
     */
    protected final HashMap<String, BotCommand<?, ?>> mappingOfCommands = new HashMap<>();
    /**
     * The {@link ExecutorService} that will be used to run the commands.
     */
    protected final ExecutorService executor;
    /**
     * The {@link CoffeeCore} instance that this handler is attached to.
     */
    protected CoffeeCore core;

    public CommandsHandler(@NonNull ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Sets the {@link CoffeeCore} instance that this handler is attached to. This should only be called once.
     * @param core The {@link CoffeeCore} instance
     */
    public void setCore(@NonNull CoffeeCore core) {
        if(this.core == null) {
            this.core = core;
        }
    }

    /**
     * Provided a mapping of {@link BotCommand}, this will check for any commands that are already registered and update them if
     * necessary. If the command is not registered, it will register it. If the command is registered, but not in the
     * mapping, it will remove it.
     * @param mappingOfCommands The mapping of commands to check and register
     * @param updateCommands Whether to update the commands if they are already registered
     */
    public void registerCommands(
            @NonNull HashMap<String, BotCommand<?, ?>> mappingOfCommands,
            boolean updateCommands
    ) {
        HashMap<String, BotCommand<?, ?>> mappingOfGlobalCommands = new HashMap<>();
        HashMap<String, BotCommand<?, ?>> mappingOfGuildCommands = new HashMap<>();
        List<JDA> shards = core.isSharded() ? core.getShardManager().getShards() : List.of(core.getJda());

        this.mappingOfCommands.putAll(mappingOfCommands);

        // Separate the global and guild commands
        for(Map.Entry<String, BotCommand<?, ?>> entry: mappingOfCommands.entrySet()) {
            if(entry.getValue().getCommandVisibility() == BotCommand.CommandVisibility.GLOBAL) {
                mappingOfGlobalCommands.put(entry.getKey(), entry.getValue());
            } else {
                mappingOfGuildCommands.put(entry.getKey(), entry.getValue());
            }
        }

        for(JDA shard: shards) {
            registerGlobalCommands(shard, mappingOfGlobalCommands, updateCommands);

            for(Guild guild: shard.getGuilds()) {
                registerGuildCommands(guild, mappingOfGuildCommands, updateCommands);
            }
        }
    }

    /**
     * Provided a list of {@link BotCommand}, this will add the commands to the specified {@link Guild}
     * @param cmds The list of commands to add
     * @param guild The guild to add the commands to
     */
    public void upsertGuildCommandsToGuild(@NonNull List<BotCommand<?,?>> cmds, @NonNull Guild guild) {
        cmds.stream().filter(
                cmd -> cmd.getCommandVisibility() == BotCommand.CommandVisibility.GUILD).forEach(
                        cmd -> cmd.updateCommand(guild)
        );
    }

    /**
     * Gets a {@link BotCommand} from the {@link #mappingOfCommands}.
     * @param name The name of the command to get
     * @return BotCommand or {@code null} if the command is not found
     */
    @Nullable
    public BotCommand<?, ? extends GenericCommandInteractionEvent> getCommand(@NonNull String name) {
        return mappingOfCommands.get(name);
    }

    /**
     * Gets a list of all the commands that are registered.
     * <br>
     * <b>Recommended to cache the result of this method.</b>
     * @return An {@link ArrayList} of {@link BotCommand} that are registered
     */
    @NonNull
    public ArrayList<BotCommand<?, ? extends GenericCommandInteractionEvent>> getCommands() {
        return new ArrayList<>(mappingOfCommands.values());
    }

    /**
     * Gets a list of the guild commands that are registered. May return a non-null empty list.
     * <br>
     * <b>Recommended to cache the result of this method.</b>
     * @return An {@link ArrayList} of {@link BotCommand} that are registered as guild commands
     */
    @NonNull
    public ArrayList<BotCommand<?, ? extends GenericCommandInteractionEvent>> getGuildCommands() {
        return new ArrayList<>(
                mappingOfCommands.values().stream().filter(
                        cmd -> cmd.getCommandVisibility() == BotCommand.CommandVisibility.GUILD
                ).toList()
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {
        executor.submit(() -> {
            BotCommand<?, SlashCommandInteractionEvent> cmd = Objects.requireNonNull(
                    (BotCommand<?, SlashCommandInteractionEvent>) getCommand(event.getName())
            );
            Message msg = cmd.handleReply(event, cmd);

            BotCommand.letMessageExpire(cmd, msg);
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onUserContextInteraction(@NonNull UserContextInteractionEvent event) {
        executor.submit(() -> {
            BotCommand<?, UserContextInteractionEvent> cmd = Objects.requireNonNull(
                    (BotCommand<?, UserContextInteractionEvent>) getCommand(event.getName())
            );
            Message msg = cmd.handleReply(event, cmd);

            BotCommand.letMessageExpire(cmd, msg);
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMessageContextInteraction(@NonNull MessageContextInteractionEvent event) {
        executor.submit(() -> {
            BotCommand<?, MessageContextInteractionEvent> cmd = Objects.requireNonNull(
                    (BotCommand<?, MessageContextInteractionEvent>) getCommand(event.getName())
            );
            Message msg = cmd.handleReply(event, cmd);

            BotCommand.letMessageExpire(cmd, msg);
        });
    }

    @Override
    public void onButtonInteraction(@NonNull ButtonInteractionEvent event) {
        executor.submit(() -> {
            ButtonCommand<?, ?> cmd = Objects.requireNonNull(
                    (ButtonCommand<?, ?>) getCommand(
                            event.getButton().getId().substring(0, event.getButton().getId().indexOf("_"))
                    )
            );

            cmd.runButtonInteraction(event);
        });
    }

    @Override
    public void onModalInteraction(@NonNull ModalInteractionEvent event) {
        executor.submit(() -> {
            ModalCommand cmd = Objects.requireNonNull(
                    (ModalCommand) getCommand(event.getModalId().substring(0, event.getModalId().indexOf("_")))
            );

            cmd.runModalInteraction(event);
        });
    }

    private void registerGlobalCommands(
            @NonNull JDA shard,
            @NonNull HashMap<String, BotCommand<?, ?>> mappingOfGlobalCommands,
            boolean updateCommands
    ) {
        List<Command> listOfActiveGlobalCommands = shard.retrieveCommands().complete();
        List<String> detectedGlobalCommandNames = new ArrayList<>();

        // Checks for the detected global commands
        for(Iterator<Command> it = listOfActiveGlobalCommands.iterator(); it.hasNext(); ) {
            Command cmd = it.next();

            if(mappingOfCommands.containsKey(cmd.getName())) {
                BotCommand<?, ?> botCmd = mappingOfGlobalCommands.get(cmd.getName());

                if(botCmd == null) {
                    continue;
                }

                botCmd.setGlobalCommandId(cmd.getIdLong());
                if(updateCommands) {
                    botCmd.updateCommand(shard);
                }

                detectedGlobalCommandNames.add(cmd.getName());

                it.remove();
            }
        }

        // Fills in any gaps or removes any global commands
        for(Command cmd: listOfActiveGlobalCommands) { // Removes unused global commands
            shard.deleteCommandById(cmd.getId()).complete();
        }

        if(detectedGlobalCommandNames.size() < mappingOfGlobalCommands.size()) { // Adds new global commands
            List<String> missingCommands = new ArrayList<>(mappingOfGlobalCommands.keySet());

            missingCommands.removeAll(detectedGlobalCommandNames);

            for(String cmdName: missingCommands) {
                BotCommand<?, ?> cmd = mappingOfCommands.get(cmdName);
                cmd.updateCommand(shard);
            }
        }

        detectedGlobalCommandNames.clear();
    }

    private void registerGuildCommands(
            @NonNull Guild guild,
            @NonNull HashMap<String, BotCommand<?, ?>> mappingOfGuildCommands,
            boolean updateCommands
    ) {
        List<Command> listOfActiveGuildCommands = guild.retrieveCommands().complete();
        List<String> detectedGuildCommandNames = new ArrayList<>();

        for (Iterator<Command> it = listOfActiveGuildCommands.iterator(); it.hasNext(); ) {
            Command cmd = it.next();

            if(mappingOfCommands.containsKey(cmd.getName())) {
                BotCommand<?, ?> botCmd = mappingOfGuildCommands.get(cmd.getName());

                if(botCmd == null) {
                    continue;
                }

                if (
                        botCmd.getGuildsToRegisterIn().isEmpty() ||
                                botCmd.getGuildsToRegisterIn().contains(guild.getIdLong())
                ) {
                    if(updateCommands) {
                        botCmd.updateCommand(guild);
                    }

                    detectedGuildCommandNames.add(cmd.getName());

                    it.remove();
                }
            }
        }

        // Fills in any gaps or removes any guild commands
        for(Command cmd: listOfActiveGuildCommands) { // Removes unused guild commands
            guild.deleteCommandById(cmd.getId()).complete();
        }

        if(detectedGuildCommandNames.size() < mappingOfGuildCommands.size()) { // Adds new guild commands
            List<String> missingCommands = new ArrayList<>(mappingOfGuildCommands.keySet());

            missingCommands.removeAll(detectedGuildCommandNames);

            for(String cmdName: missingCommands) {
                BotCommand<?, ?> cmd = mappingOfGuildCommands.get(cmdName);
                cmd.updateCommand(guild);
            }
        }

        detectedGuildCommandNames.clear();
    }
}