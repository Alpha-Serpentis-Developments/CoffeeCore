package dev.alphaserpentis.coffeecore.handler.api.discord.commands;

import dev.alphaserpentis.coffeecore.commands.BotCommand;
import dev.alphaserpentis.coffeecore.commands.ButtonCommand;
import dev.alphaserpentis.coffeecore.commands.ModalCommand;
import dev.alphaserpentis.coffeecore.core.CoffeeCore;
import dev.alphaserpentis.coffeecore.hook.CommandHook;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * The handler for all the commands to be registered with the bot. This handles registration and execution of the commands.
 */
public class CommandsHandler extends ListenerAdapter {
    /**
     * The mapping of {@link BotCommand} that have been registered to the bot. This is used to check for commands that are already
     * registered and update them if necessary.
     */
    protected final HashMap<String, BotCommand<?, ?>> mapOfCommands = new HashMap<>();
    /**
     * The {@link ExecutorService} that will be used to run the commands.
     */
    protected final ExecutorService executor;
    /**
     * The {@link CoffeeCore} instance that this handler is attached to.
     */
    protected CoffeeCore core;
    /**
     * The function that will be called when an error occurs in any of the following methods:
     * <ul>
     *     <li>{@link #onSlashCommandInteraction(SlashCommandInteractionEvent)}</li>
     *     <li>{@link #onUserContextInteraction(UserContextInteractionEvent)}</li>
     *     <li>{@link #onMessageContextInteraction(MessageContextInteractionEvent)}</li>
     *     <li>{@link #onButtonInteraction(ButtonInteractionEvent)}</li>
     *     <li>{@link #onModalInteraction(ModalInteractionEvent)}</li>
     * </ul>
     */
    protected Function<Throwable, ?> handleInteractionError;
    /**
     * The function that will be called when an error occurs in any of the following methods:
     * <ul>
     *     <li>{@link #registerGlobalCommands(JDA, HashMap, boolean)}</li>
     *     <li>{@link #registerGuildCommands(Guild, HashMap, boolean)}</li>
     *     <li>{@link #deregisterCommands(long)}</li>
     *     <li>{@link #upsertGuildCommandsToGuild(List, Guild)}</li>
     * </ul>
     */
    protected Function<Throwable, ?> handleRegistrationError;

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

    public void setHandleInteractionError(@NonNull Function<Throwable, ?> handleInteractionError) {
        this.handleInteractionError = handleInteractionError;
    }

    public void setHandleRegistrationError(@NonNull Function<Throwable, ?> handleRegistrationError) {
        this.handleRegistrationError = handleRegistrationError;
    }

    /**
     * Provided a mapping of {@link BotCommand}, this will check for any commands that are already registered and update them if
     * necessary. If the command is not registered, it will register it. If the command is registered, but not in the
     * mapping, it will remove it.
     * @param mapOfCommands The mapping of commands to check and register
     * @param updateCommands Whether to update the commands if they are already registered
     */
    public void registerCommands(
            @NonNull HashMap<String, BotCommand<?, ?>> mapOfCommands,
            boolean updateCommands
    ) {
        HashMap<String, BotCommand<?, ?>> mapOfGlobalCommands = new HashMap<>();
        HashMap<String, BotCommand<?, ?>> mapOfGuildCommands = new HashMap<>();
        List<JDA> shards = getShards();

        this.mapOfCommands.putAll(mapOfCommands);

        // Separate the global and guild commands
        for(Map.Entry<String, BotCommand<?, ?>> entry: mapOfCommands.entrySet()) {
            switch(entry.getValue().getCommandVisibility()) {
                case GLOBAL -> mapOfGlobalCommands.put(entry.getKey(), entry.getValue());
                case GUILD -> mapOfGuildCommands.put(entry.getKey(), entry.getValue());
                default -> throw new IllegalStateException(
                        "Unexpected value: " + entry.getValue().getCommandVisibility()
                );
            }
        }

        for(JDA shard: shards) {
            registerGlobalCommands(shard, mapOfGlobalCommands, updateCommands);

            for(Guild guild: shard.getGuilds()) {
                registerGuildCommands(guild, mapOfGuildCommands, updateCommands);
            }
        }
    }

    /**
     * Provided an ID for a guild, it will deregister the command IDs to that guild.
     * <p><b>This does not remove your guild commands from the guild!</b></p>
     * @param guildId The ID of the guild to deregister the commands from
     */
    public void deregisterCommands(long guildId) {
        getGuildCommands().forEach(cmd -> {
            try {
                cmd.removeGuildCommandId(guildId);
            } catch(ErrorResponseException e) {
                handleRegistrationError(e);
            }
        });
    }

    /**
     * Provided a list of {@link BotCommand}, this will add the commands to the specified {@link Guild}
     * @param cmds The list of commands to add
     * @param guild The guild to add the commands to
     */
    public void upsertGuildCommandsToGuild(@NonNull List<BotCommand<?, ?>> cmds, @NonNull Guild guild) {
        cmds.stream().filter(cmd -> isGuildEligibleForCommand(guild, cmd)).forEach(
                cmd -> {
                    try {
                        cmd.updateCommand(guild);
                    } catch(ErrorResponseException e) {
                        handleRegistrationError(e);
                    }
                }
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {
        executor.submit(() -> {
            try {
                var cmd = Objects.requireNonNull(
                        (BotCommand<?, SlashCommandInteractionEvent>) getCommand(event.getName())
                );
                Message msg = cmd.handleReply(event, cmd);

                executePostExecutionHook(cmd, event, msg);
            } catch(Exception e) {
                handleInteractionError(e);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onUserContextInteraction(@NonNull UserContextInteractionEvent event) {
        executor.submit(() -> {
            try {
                var cmd = Objects.requireNonNull(
                        (BotCommand<?, UserContextInteractionEvent>) getCommand(event.getName())
                );
                Message msg = cmd.handleReply(event, cmd);

                executePostExecutionHook(cmd, event, msg);
            } catch(Exception e) {
                handleInteractionError(e);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMessageContextInteraction(@NonNull MessageContextInteractionEvent event) {
        executor.submit(() -> {
            try {
                var cmd = Objects.requireNonNull(
                        (BotCommand<?, MessageContextInteractionEvent>) getCommand(event.getName())
                );
                Message msg = cmd.handleReply(event, cmd);

                executePostExecutionHook(cmd, event, msg);
            } catch(Exception e) {
                handleInteractionError(e);
            }
        });
    }

    @Override
    public void onButtonInteraction(@NonNull ButtonInteractionEvent event) {
        executor.submit(() -> {
            try {
                String buttonId = Objects.requireNonNull(event.getButton().getId());
                var cmd = Objects.requireNonNull(
                        (ButtonCommand<?, ?>) getCommand(buttonId.substring(0, buttonId.indexOf("_")))
                );
                var optional = cmd.runButtonInteraction(event).orElse(null);

                executePostExecutionHook(cmd, event, optional);
            } catch(Exception e) {
                handleInteractionError(e);
            }
        });
    }

    @Override
    public void onModalInteraction(@NonNull ModalInteractionEvent event) {
        executor.submit(() -> {
            try {
                String modalId = Objects.requireNonNull(event.getModalId());
                var cmd = Objects.requireNonNull(
                        (BotCommand<?, ?>) getCommand(modalId.substring(0, modalId.indexOf("_")))
                );
                var optional = ((ModalCommand) cmd).runModalInteraction(event);

                executePostExecutionHook(cmd, event, optional);
            } catch(Exception e) {
                handleInteractionError(e);
            }
        });
    }

    /**
     * Gets a {@link BotCommand} from the {@link #mapOfCommands}.
     * @param name The name of the command to get
     * @return BotCommand or {@code null} if the command is not found
     */
    @Nullable
    public BotCommand<?, ?> getCommand(@NonNull String name) {
        return mapOfCommands.get(name);
    }

    /**
     * Gets a list of all the commands that are registered.
     * <br>
     * <b>Recommended to cache the result of this method.</b>
     * @return An {@link ArrayList} of {@link BotCommand} that are registered
     */
    @NonNull
    public ArrayList<BotCommand<?, ?>> getCommands() {
        return new ArrayList<>(mapOfCommands.values());
    }

    /**
     * Gets a list of the guild commands that are registered. May return a non-null empty list.
     * <br>
     * <b>Recommended to cache the result of this method.</b>
     * @return An {@link ArrayList} of {@link BotCommand} that are registered as guild commands
     */
    @NonNull
    public ArrayList<BotCommand<?, ?>> getGuildCommands() {
        return new ArrayList<>(
                mapOfCommands.values().stream().filter(
                        cmd -> cmd.getCommandVisibility() == BotCommand.CommandVisibility.GUILD
                ).toList()
        );
    }

    /**
     * Returns a boolean if the specified guild is eligible for the specified command.
     * This will always return false if the visibility is not of {@code GUILD} visibility
     * @param guild The guild to check
     * @param cmd The command to check
     * @return {@code true} if the guild is eligible for the command, {@code false} otherwise
     */
    public boolean isGuildEligibleForCommand(@NonNull Guild guild, @NonNull BotCommand<?, ?> cmd) {
        return cmd.getCommandVisibility() == BotCommand.CommandVisibility.GUILD
                && (cmd.getGuildsToRegisterIn().isEmpty() || cmd.getGuildsToRegisterIn().contains(guild.getIdLong()));
    }

    protected void registerGlobalCommands(
            @NonNull JDA shard,
            @NonNull HashMap<String, BotCommand<?, ?>> mapOfGlobalCommands,
            boolean updateCommands
    ) {
        List<Command> listOfActiveGlobalCommands = shard.retrieveCommands().complete();
        List<String> detectedGlobalCommandNames = new ArrayList<>();

        // Checks for the detected global commands
        for(Iterator<Command> it = listOfActiveGlobalCommands.iterator(); it.hasNext(); ) {
            Command cmd = it.next();

            if(mapOfCommands.containsKey(cmd.getName())) {
                BotCommand<?, ?> botCmd = mapOfGlobalCommands.get(cmd.getName());

                if(botCmd == null)
                    continue;

                botCmd.setGlobalCommandId(cmd.getIdLong());
                if(updateCommands)
                    botCmd.updateCommand(shard);

                detectedGlobalCommandNames.add(cmd.getName());

                it.remove();
            }
        }

        // Fills in any gaps or removes any global commands
        // Removes unused global commands
        listOfActiveGlobalCommands.forEach(cmd -> {
            try{
                shard.deleteCommandById(cmd.getId()).complete();
            } catch(ErrorResponseException e) {
                handleRegistrationError(e);
            }
        });

        if(detectedGlobalCommandNames.size() < mapOfGlobalCommands.size()) { // Adds new global commands
            List<String> missingCommands = new ArrayList<>(mapOfGlobalCommands.keySet());

            missingCommands.removeAll(detectedGlobalCommandNames);

            for(String cmdName: missingCommands) {
                mapOfGlobalCommands.get(cmdName).updateCommand(shard);
            }
        }

        listOfActiveGlobalCommands.clear();
        detectedGlobalCommandNames.clear();
    }

    protected void registerGuildCommands(
            @NonNull Guild guild,
            @NonNull HashMap<String, BotCommand<?, ?>> mapOfGuildCommands,
            boolean updateCommands
    ) {
        List<Command> listOfActiveGuildCommands = guild.retrieveCommands().complete();
        List<String> detectedGuildCommandNames = new ArrayList<>();
        List<String> ignoredGuildCommandNames = new ArrayList<>();

        for(Iterator<Command> it = listOfActiveGuildCommands.iterator(); it.hasNext(); ) {
            Command cmd = it.next();

            if(mapOfCommands.containsKey(cmd.getName())) {
                BotCommand<?, ?> botCmd = mapOfGuildCommands.get(cmd.getName());

                if(botCmd == null)
                    continue;

                if(isGuildEligibleForCommand(guild, botCmd)) {
                    if(updateCommands)
                        botCmd.updateCommand(guild);

                    detectedGuildCommandNames.add(cmd.getName());

                    it.remove();
                } else if(!botCmd.getGuildsToRegisterIn().contains(guild.getIdLong())) {
                    ignoredGuildCommandNames.add(cmd.getName());

                    it.remove();
                }
            }
        }

        // Fills in any gaps or removes any guild commands
        // Removes unused guild commands
        listOfActiveGuildCommands.forEach(cmd -> {
            try{
                guild.deleteCommandById(mapOfGuildCommands.get(cmd.getName()).getGuildCommandId(guild)).complete();
            } catch(ErrorResponseException e) {
                handleRegistrationError(e);
            }
        });

        if(detectedGuildCommandNames.size() < mapOfGuildCommands.size()) { // Adds new guild commands
            List<String> missingCommands = new ArrayList<>(mapOfGuildCommands.keySet());

            missingCommands.removeAll(detectedGuildCommandNames);
            missingCommands.removeAll(ignoredGuildCommandNames);

            for(String cmdName: missingCommands) {
                mapOfGuildCommands.get(cmdName).updateCommand(guild);
            }
        }

        listOfActiveGuildCommands.clear();
        detectedGuildCommandNames.clear();
    }

    protected void executePostExecutionHook(
            @NonNull BotCommand<?, ?> cmd,
            @NonNull GenericCommandInteractionEvent event,
            @NonNull Message msg
    ) {
        var hooks = cmd.getCommandHooks()
                .stream()
                .filter(hook -> hook.getTypeOfHook() == CommandHook.Type.POST_EXECUTION)
                .toList();

        hooks.forEach(hook -> hook.execute(cmd, event, msg));
    }

    protected void executePostExecutionHook(
            @NonNull BotCommand<?, ?> cmd,
            @NonNull GenericInteractionCreateEvent event,
            @Nullable Object data
    ) {
        var hooks = cmd.getCommandHooks()
                .stream()
                .filter(hook -> hook.getTypeOfHook() == CommandHook.Type.POST_EXECUTION)
                .toList();

        hooks.forEach(hook -> hook.execute(cmd, event, data));
    }

    /**
     * Handles any uncaught interaction errors
     * @param e The error that occurred
     * @return An {@link Optional}
     */
    @SuppressWarnings("UnusedReturnValue, UnusedParameters")
    protected Optional<?> handleInteractionError(@NonNull Throwable e) {
        return Optional.ofNullable(handleInteractionError.apply(e));
    }

    /**
     * Handles any uncaught registration errors
     * @param e The error that occurred
     * @return An {@link Optional}
     */
    @SuppressWarnings("UnusedReturnValue, UnusedParameters")
    protected Optional<?> handleRegistrationError(@NonNull Throwable e) {
        return Optional.ofNullable(handleRegistrationError.apply(e));
    }

    /**
     * Returns a list of JDA instances that are being used by the bot.
     * @return An immutable list of JDA instances
     */
    @NonNull
    protected List<JDA> getShards() {
        return core.isSharded()
                ? Objects.requireNonNull(core.getShardManager()).getShards()
                : List.of(Objects.requireNonNull(core.getJda()));
    }
}