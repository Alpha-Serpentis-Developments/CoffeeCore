package dev.alphaserpentis.coffeecore.commands;

import dev.alphaserpentis.coffeecore.core.CoffeeCore;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import dev.alphaserpentis.coffeecore.hook.CommandHook;
import dev.alphaserpentis.coffeecore.data.server.ServerData;
import dev.alphaserpentis.coffeecore.hook.defaults.MessageExpireHook;
import dev.alphaserpentis.coffeecore.hook.defaults.RatelimitHook;
import io.reactivex.rxjava3.annotations.Experimental;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Abstract class for bot commands
 * @param <T> The return type for responses. This should only be {@link MessageEmbed} or {@link String}.
 * @param <E> Type of {@link GenericCommandInteractionEvent} that will be used to pass events to the command.
 */
public abstract class BotCommand<T, E extends GenericCommandInteractionEvent> {

    protected final HashMap<Long, Long> guildCommandIds = new HashMap<>();
    protected final HashMap<Long, Long> ratelimitMap = new HashMap<>();
    protected final Collection<Long> guildsToRegisterIn;
    protected final Collection<CommandHook> commandHooks;
    protected final String name;
    protected final String description;
    protected final String helpDescription;
    protected final long ratelimitLength;
    protected final long messageExpirationLength;
    protected final boolean onlyEmbed;
    protected final boolean onlyEphemeral;
    protected final boolean isActive;
    protected final boolean deferReplies;
    protected final boolean useRatelimits;
    protected final boolean forgiveRatelimitOnError;
    protected final boolean messagesExpire;
    protected final CommandVisibility commandVisibility;
    protected final Command.Type commandType;
    protected final TypeOfEphemeral ephemeralType;
    protected long globalCommandId;
    protected CoffeeCore core;

    public enum CommandVisibility {
        /**
         * A slash command that will be registered to the bot. Can be accessed globally
         * @see JDA#upsertCommand(String, String)
         */
        GLOBAL,
        /**
         * A slash command that will be registered on a per-guild basis. Can only be accessed in the guild it is registered in.
         * @see Guild#upsertCommand(String, String)
         */
        GUILD
    }

    /**
     * Types of ephemeral available for commands.
     */
    @Experimental
    @Deprecated(forRemoval = true)
    public enum TypeOfEphemeral {
        /**
         * The default ephemeral type. This will be set to the server's default ephemeral type.
         */
        DEFAULT,
        /**
         * Ephemeral type that enables the usage of {@link #beforeRunCommand(long, GenericCommandInteractionEvent)}
         */
        DYNAMIC
    }

    /**
     * Static class builder helper for {@link BotCommand}
     */
    public static class BotCommandOptions {
        protected String name;
        protected String description;
        protected String helpDescription = null;
        protected long ratelimitLength = 0;
        protected long messageExpirationLength = 0;
        protected boolean onlyEmbed = false;
        protected boolean onlyEphemeral = false;
        protected boolean isActive = true;
        protected boolean deferReplies = false;
        protected boolean useRatelimits = false;
        protected boolean forgiveRatelimitOnError = false;
        protected boolean messagesExpire = false;
        protected boolean useDefaultHooks = true;
        protected CommandVisibility commandVisibility = CommandVisibility.GLOBAL;
        protected Command.Type commandType = Command.Type.SLASH;
        protected TypeOfEphemeral typeOfEphemeral = TypeOfEphemeral.DEFAULT;
        protected Collection<Long> guildsToRegisterIn = List.of();
        protected Collection<CommandHook> commandHooks = new ArrayList<>();

        public BotCommandOptions() {}

        public BotCommandOptions(
                @NonNull String name,
                @NonNull String description
        ) {
            this.name = name;
            this.description = description;
        }

        @Deprecated(forRemoval = true)
        public BotCommandOptions(
                @NonNull String name,
                @NonNull String description,
                boolean onlyEmbed,
                boolean onlyEphemeral,
                @NonNull TypeOfEphemeral typeOfEphemeral
        ) {
            this.name = name;
            this.description = description;
            this.onlyEmbed = onlyEmbed;
            this.onlyEphemeral = onlyEphemeral;
            this.typeOfEphemeral = typeOfEphemeral;
        }

        @Deprecated(forRemoval = true)
        public BotCommandOptions(
                @NonNull String name,
                @NonNull String description,
                boolean onlyEmbed,
                boolean onlyEphemeral,
                @NonNull TypeOfEphemeral typeOfEphemeral,
                boolean deferReplies
        ) {
            this.name = name;
            this.description = description;
            this.onlyEmbed = onlyEmbed;
            this.onlyEphemeral = onlyEphemeral;
            this.typeOfEphemeral = typeOfEphemeral;
            this.deferReplies = deferReplies;
        }

        @Deprecated(forRemoval = true)
        public BotCommandOptions(
                @NonNull String name,
                @NonNull String description,
                long ratelimitLength,
                long messageExpirationLength,
                boolean onlyEmbed,
                boolean onlyEphemeral,
                @NonNull TypeOfEphemeral typeOfEphemeral,
                boolean isActive,
                boolean deferReplies,
                boolean useRatelimits,
                boolean messagesExpire
        ) {
            this.name = name;
            this.description = description;
            this.ratelimitLength = ratelimitLength;
            this.messageExpirationLength = messageExpirationLength;
            this.onlyEmbed = onlyEmbed;
            this.onlyEphemeral = onlyEphemeral;
            this.typeOfEphemeral = typeOfEphemeral;
            this.isActive = isActive;
            this.deferReplies = deferReplies;
            this.useRatelimits = useRatelimits;
            this.messagesExpire = messagesExpire;
        }

        /**
         * Sets the name of the command
         * @param name The name of the command
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setName(@NonNull String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the description of the command
         * @param description The description of the command
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setDescription(@NonNull String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the help description of the command
         * @param helpDescription The help description of the command
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setHelpDescription(@NonNull String helpDescription) {
            this.helpDescription = helpDescription;
            return this;
        }

        /**
         * Sets the ratelimit length for the command
         * @param ratelimitLength The length of the ratelimit in seconds
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setRatelimitLength(long ratelimitLength) {
            this.ratelimitLength = ratelimitLength;
            return this;
        }

        /**
         * Sets the message expiration length for the command
         * @param messageExpirationLength The length of the message expiration in seconds
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setMessageExpirationLength(long messageExpirationLength) {
            this.messageExpirationLength = messageExpirationLength;
            return this;
        }

        /**
         * Sets the command to determine if it'll only ever embed messages
         * @param onlyEmbed Whether the command will only ever embed messages
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setOnlyEmbed(boolean onlyEmbed) {
            this.onlyEmbed = onlyEmbed;
            return this;
        }

        /**
         * Sets the command to determine if its messages will be ephemeral
         * @param onlyEphemeral Whether the command will only ever send ephemeral messages
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setOnlyEphemeral(boolean onlyEphemeral) {
            this.onlyEphemeral = onlyEphemeral;
            return this;
        }

        /**
         * Sets the command to be active or not for use
         * @param isActive Whether the command is active or not
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        /**
         * Sets the command to defer replies
         * @param deferReplies Whether the command will defer replies
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setDeferReplies(boolean deferReplies) {
            this.deferReplies = deferReplies;
            return this;
        }

        /**
         * Sets the command to use ratelimits
         * @param useRatelimits Whether the command will use ratelimits
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setUseRatelimits(boolean useRatelimits) {
            this.useRatelimits = useRatelimits;
            return this;
        }

        /**
         * Sets the command to forgive ratelimits on an error
         * @param forgiveRatelimitOnError Whether the command will forgive ratelimits on an error
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setForgiveRatelimitOnError(boolean forgiveRatelimitOnError) {
            this.forgiveRatelimitOnError = forgiveRatelimitOnError;
            return this;
        }

        /**
         * Sets the command to make messages expire
         * @param messagesExpire Whether the command will make messages expire
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setMessagesExpire(boolean messagesExpire) {
            this.messagesExpire = messagesExpire;
            return this;
        }

        /**
         * Sets the command to use default hooks
         * @param useDefaultHooks Whether the command will use default hooks
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setUseDefaultHooks(boolean useDefaultHooks) {
            this.useDefaultHooks = useDefaultHooks;
            return this;
        }

        /**
         * Sets the command's visibility
         * @param commandVisibility The command's visibility
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setCommandVisibility(@NonNull CommandVisibility commandVisibility) {
            this.commandVisibility = commandVisibility;
            return this;
        }

        /**
         * Sets the command's type
         * @param commandType The command's type
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setCommandType(@NonNull Command.Type commandType) {
            this.commandType = commandType;
            return this;
        }

        /**
         * Sets the command's type of ephemeral
         * @param typeOfEphemeral The command's type of ephemeral
         * @return {@link BotCommandOptions}
         */
        @NonNull
        @Deprecated(forRemoval = true)
        public BotCommandOptions setTypeOfEphemeral(@NonNull TypeOfEphemeral typeOfEphemeral) {
            this.typeOfEphemeral = typeOfEphemeral;
            return this;
        }

        /**
         * Sets the command's guilds to register in
         * <p>
         * This is only used if the command's visibility is {@link CommandVisibility#GUILD}. To apply to all guilds, leave the array empty!
         * @param guildsToRegisterIn The command's guilds to register in
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setGuildsToRegisterIn(@NonNull List<Long> guildsToRegisterIn) {
            this.guildsToRegisterIn = guildsToRegisterIn;
            return this;
        }

        /**
         * Sets the command's hooks
         * @param commandHooks The command's hooks
         * @return {@link BotCommandOptions}
         */
        @NonNull
        public BotCommandOptions setHooks(@NonNull Collection<CommandHook> commandHooks) {
            this.commandHooks.addAll(commandHooks);
            return this;
        }

        /**
         * Validates the command options.
         * A command is considered valid if: <br>
         *    - The name is not null <br>
         *    - If the command is a slash command, the description is not {@code null} <br>
         *    - If using ratelimits, the ratelimit length is greater than 0 <br>
         *    - If messages expire, the message expiration length is greater than 0 <br>
         */
        public void validate() throws IllegalArgumentException {
            if(name == null)
                throw new IllegalArgumentException("Name cannot be null!");
            if(description == null && commandType == Command.Type.SLASH)
                throw new IllegalArgumentException("Description cannot be null for slash commands!");
            if(useRatelimits && ratelimitLength <= 0)
                throw new IllegalArgumentException("Ratelimit length must be greater than 0!");
            if(messagesExpire && messageExpirationLength <= 0)
                throw new IllegalArgumentException("Message expiration length must be greater than 0!");
        }
    }

    public BotCommand() {
        throw new UnsupportedOperationException("Unsupported constructor");
    }

    public BotCommand(@NonNull BotCommandOptions options) {
        options.validate();

        name = options.name;
        description = options.description;
        helpDescription = options.helpDescription;
        ratelimitLength = options.ratelimitLength;
        messageExpirationLength = options.messageExpirationLength;
        onlyEmbed = options.onlyEmbed;
        onlyEphemeral = options.onlyEphemeral;
        isActive = options.isActive;
        deferReplies = options.deferReplies;
        useRatelimits = options.useRatelimits;
        forgiveRatelimitOnError = options.forgiveRatelimitOnError;
        messagesExpire = options.messagesExpire;
        commandVisibility = options.commandVisibility;
        commandType = options.commandType;
        ephemeralType = options.typeOfEphemeral;
        guildsToRegisterIn = options.guildsToRegisterIn;
        commandHooks = options.commandHooks;

        if(isUsingRatelimits() && options.useDefaultHooks) {
            commandHooks.add(new RatelimitHook());
        }
        if(doMessagesExpire() && options.useDefaultHooks) {
            commandHooks.add(new MessageExpireHook());
        }
    }

    /**
     * Method used to execute the command. Should contain the main logic of the command.
     * @param userId is the ID of the user who called the command
     * @param event is the {@link SlashCommandInteractionEvent} that contains the interaction
     * @return a nonnull {@link CommandResponse} containing either a {@link MessageEmbed} or {@link String}
     */
    @NonNull
    public abstract CommandResponse<T> runCommand(final long userId, @NonNull final E event);

    /**
     * Method used to update the command.
     * This method is called when the bot is started and when the command is updated.
     * <p>
     * <b>This method should be overridden if the command uses subcommands.</b>
     * @param jda {@link JDA} instance
     */
    public void updateCommand(@NonNull JDA jda) {
        jda
                .upsertCommand(getJDACommandData(getCommandType(), getName(), getDescription()))
                .queue(command -> globalCommandId = command.getIdLong());
    }

    /**
     * Guild-specific method used to update the command. This method is called when the bot is started and when the command is updated.
     * <p><b>This method should be overridden if the command uses subcommands.</b></p>
     * @param guild {@link Guild} to update the command in
     */
    public void updateCommand(@NonNull Guild guild) {
        guild
                .upsertCommand(getJDACommandData(getCommandType(), getName(), getDescription()))
                .queue();
    }

    /**
     * A method that <b>REQUIRES</b> to be overridden if to be used for any {@link BotCommand} with an ephemeralType of
     * {@link TypeOfEphemeral#DYNAMIC}
     * <p>
     * This method is currently only called if the command is <b>DEFERRED</b>.
     * <p>
     * Operations inside must NOT exceed the time it requires to <b>ACKNOWLEDGE</b> the API!
     * @param userId is a long ID provided by Discord for the user calling the command
     * @param event is a {@link E} that contains the interaction
     * @return a nonnull {@link CommandResponse} containing either a {@link MessageEmbed} or String
     */
    @NonNull
    @Deprecated(forRemoval = true)
    public CommandResponse<T> beforeRunCommand(long userId, @NonNull E event) {
        throw new UnsupportedOperationException("beforeRunCommand needs to be overridden!");
    }

    /**
     * A method that checks and handles a potentially rate-limited user.
     * <p>
     * Commands not using embeds must override this method to return a {@link CommandResponse} containing String.
     * @param userId is a long ID provided by Discord for the user calling the command
     * @return a nullable {@link CommandResponse} that by default returns a {@link MessageEmbed}
     */
    @Nullable
    public CommandResponse<?> checkAndHandleRateLimitedUser(long userId) {
        if(isUserRatelimited(userId)) {
            return new CommandResponse<>(
                    onlyEphemeral,
                    new EmbedBuilder().setDescription(
                            "You are still rate limited. Expires in " + (
                                    getRatelimitMap().get(userId) - Instant.now().getEpochSecond()
                            ) + " seconds."
                    ).build()
            );
        } else {
            return null;
        }
    }

    /**
     * Removes the guild command ID for the provided guild ID
     * @param guildId is the ID of the guild to remove the command ID for
     */
    public void removeGuildCommandId(long guildId) {
        getGuildCommandIds().remove(guildId);
    }

    /**
     * Sets the global command ID for the command
     * @param id is the ID of the global command
     * @throws UnsupportedOperationException if the command is a guild command
     */
    public void setGlobalCommandId(long id) {
        if(getCommandVisibility() == CommandVisibility.GUILD)
            throw new UnsupportedOperationException("Cannot set global command ID for guild command");

        globalCommandId = id;
    }

    public void setCore(@NonNull CoffeeCore core) {
        this.core = core;
    }

    @NonNull
    public HashMap<Long, Long> getGuildCommandIds() {
        return guildCommandIds;
    }

    @NonNull
    public HashMap<Long, Long> getRatelimitMap() {
        return ratelimitMap;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getHelpDescription() {
        return helpDescription;
    }

    public long getGlobalCommandId() {
        return globalCommandId;
    }

    /**
     * Gets this command's ID associated with this guild
     * @param guild is the {@link Guild} to get the command ID for
     * @return a long ID of the command. If the command is not registered in the guild, it will return -1.
     */
    public long getGuildCommandId(@NonNull Guild guild) {
        return getGuildCommandIds().computeIfAbsent(guild.getIdLong(), id ->
                guild.retrieveCommands().complete().stream()
                        .filter(command -> command.getName().equals(getName()))
                        .findFirst()
                        .map(Command::getIdLong)
                        .orElse(-1L)
        );
    }

    public long getRatelimitLength() {
        return ratelimitLength;
    }

    public long getMessageExpirationLength() {
        return messageExpirationLength;
    }

    public boolean isOnlyEmbed() {
        return onlyEmbed;
    }

    public boolean isOnlyEphemeral() {
        return onlyEphemeral || !isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isDeferReplies() {
        return deferReplies;
    }

    public boolean isUsingRatelimits() {
        return useRatelimits;
    }

    public boolean isForgivingRatelimitOnError() {
        return forgiveRatelimitOnError;
    }

    public boolean isUserRatelimited(long userId) {
        final long ratelimit = getRatelimitMap().getOrDefault(userId, 0L);

        if(ratelimit != 0) {
            return ratelimit > Instant.now().getEpochSecond();
        } else {
            getRatelimitMap().remove(userId);
            return false;
        }
    }

    public boolean doMessagesExpire() {
        return messagesExpire;
    }

    @NonNull
    @Deprecated(forRemoval = true)
    public TypeOfEphemeral getEphemeralType() {
        return ephemeralType;
    }

    @NonNull
    public CommandVisibility getCommandVisibility() {
        return commandVisibility;
    }

    @NonNull
    public Command.Type getCommandType() {
        return commandType;
    }

    @NonNull
    public CoffeeCore getCore() {
        return core;
    }

    @NonNull
    public Collection<Long> getGuildsToRegisterIn() {
        return guildsToRegisterIn;
    }

    @NonNull
    public Collection<CommandHook> getCommandHooks() {
        return commandHooks;
    }

    /**
     * Handles the reply of a command.
     * @param event {@link E} that contains the interaction
     * @param cmd {@link BotCommand} that contains the command used
     * @return {@link Message} that is the reply of the command
     */
    @NonNull
    public Message handleReply(@NonNull final E event, @NonNull final BotCommand<?, E> cmd) {
        if(cmd.isDeferReplies()) {
            return (Message) cmd.processDeferredCommand(event).complete();
        } else {
            return cmd.processNonDeferredCommand(event).complete().retrieveOriginal().complete();
        }
    }

    /**
     * Runs and processes a deferred command
     * @param event {@link E} that contains the interaction
     * @return {@link WebhookMessageCreateAction}
     */
    @NonNull
    protected WebhookMessageCreateAction<?> processDeferredCommand(@NonNull final E event) {
        final long userId = event.getUser().getIdLong();
        final InteractionHook interactHook = event.getHook();
        final boolean msgIsEphemeral = determineEphemeralStatus(event);
        final List<CommandHook> preExecHooks = commandHooks.stream()
                .filter(hook -> hook.getTypeOfHook() == CommandHook.TypeOfHook.PRE_EXECUTION)
                .toList();

        try {
            event.deferReply(msgIsEphemeral).complete();

            if(isOnlyEmbed()) {
                if(!preExecHooks.isEmpty()) {
                    ArrayList<MessageEmbed> embeds = new ArrayList<>();

                    preExecHooks.forEach(
                            hook -> hook.execute(this, event, null).ifPresent(rawResponse -> {
                                    if(rawResponse instanceof CommandResponse<?> cmdResponse) {
                                        embeds.addAll(
                                                List.of((MessageEmbed[]) cmdResponse.messageResponse())
                                        );
                                    }
                            })
                    );

                    if(!embeds.isEmpty())
                        return interactHook.sendMessageEmbeds(embeds);
                }

                return interactHook.sendMessageEmbeds(
                        Arrays.asList((MessageEmbed[]) retrieveAndProcessResponse(userId, event))
                );
            } else {
                if(!preExecHooks.isEmpty()) {
                    ArrayList<MessageEmbed> embeds = new ArrayList<>();
                    final String[] lastResponse = new String[1];

                    preExecHooks.forEach(
                            hook -> hook.execute(this, event, null).ifPresent(rawResponse -> {
                                if(rawResponse instanceof CommandResponse<?> cmdResponse) {
                                    if(cmdResponse.messageResponse() instanceof MessageEmbed[])
                                        embeds.addAll(List.of((MessageEmbed[]) cmdResponse.messageResponse()));
                                    else
                                        lastResponse[0] = (String) cmdResponse.messageResponse()[0];
                                }
                            })
                    );

                    if(lastResponse[0] != null)
                        return interactHook.sendMessage(lastResponse[0]);
                    else if(!embeds.isEmpty())
                        return interactHook.sendMessageEmbeds(embeds);
                }

                T[] response = retrieveAndProcessResponse(userId, event);

                if(response instanceof MessageEmbed[])
                    return interactHook.sendMessageEmbeds(Arrays.asList((MessageEmbed[]) response));
                else
                    return interactHook.sendMessage((String) response[0]);
            }
        } catch(Exception e) {
            if(isForgivingRatelimitOnError())
                getRatelimitMap().remove(userId);

            return interactHook.sendMessageEmbeds(handleError(e));
        }
    }

    /**
     * Runs and processes a non-deferred command
     * @param event {@link E} that contains the interaction
     * @return {@link ReplyCallbackAction}
     */
    @NonNull
    protected ReplyCallbackAction processNonDeferredCommand(@NonNull final E event) {
        final long userId = event.getUser().getIdLong();
        final boolean msgIsEphemeral = determineEphemeralStatus(event);
        T[] response;
        ReplyCallbackAction reply;

        try {
            response = retrieveAndProcessResponse(userId, event);

            if(isOnlyEmbed())
                reply = event.replyEmbeds(Arrays.asList((MessageEmbed[]) response)).setEphemeral(msgIsEphemeral);
            else
                reply = event.reply((String) response[0]).setEphemeral(msgIsEphemeral);

            return reply;
        } catch(Exception e) {
            if(isForgivingRatelimitOnError())
                getRatelimitMap().remove(userId);

            return event.replyEmbeds(handleError(e));
        }
    }

    /**
     * Retrieves the response of the command and processes it
     * @param userId The ID of the user who called the command
     * @param event {@link E} that contains the interaction
     * @return The response of the command
     */
    @SuppressWarnings("unchecked")
    @NonNull
    private T[] retrieveAndProcessResponse(long userId, @NonNull E event) {
        CommandResponse<?> responseFromCommand = isActive() ? runCommand(userId, event) : inactiveCommandResponse();

        determineRatelimit(userId, responseFromCommand);

        return (T[]) responseFromCommand.messageResponse();
    }

    private boolean determineEphemeralStatus(@NonNull E event) {
        if (event.getGuild() == null) {
            return isOnlyEphemeral();
        } else {
            ServerData serverData = getCore().getServerDataHandler().getServerData(event.getGuild().getIdLong());
            boolean guildEphemeralSetting = Objects.requireNonNull(serverData).getOnlyEphemeral();

            return isOnlyEphemeral() || guildEphemeralSetting;
        }
    }

    private void determineRatelimit(long userId, @NonNull CommandResponse<?> responseFromCommand) {
        if(responseFromCommand.forgiveRatelimit() != null && responseFromCommand.forgiveRatelimit()) {
            getRatelimitMap().remove(userId);
        } else if(
                isUsingRatelimits() && (getRatelimitMap().get(userId) == null || getRatelimitMap().get(userId) == 0)
        ) {
            getRatelimitMap().put(userId, Instant.now().getEpochSecond() + getRatelimitLength());
        }
    }

    /**
     * Deletes a message after a certain amount of time
     * @param command The command that is being executed
     * @param message The message to delete
     */
    @Deprecated
    public static void letMessageExpire(@NonNull BotCommand<?, ?> command, @NonNull Message message) {
        if(command.doMessagesExpire())
            message.delete().queueAfter(command.getMessageExpirationLength(), TimeUnit.SECONDS);
    }

    /**
     * If a command fails to execute, this method will be called to generate an error message
     * to send to the user
     * @param e The exception that was thrown
     * @return The error message to send to the user
     */
    @NonNull
    protected static MessageEmbed handleError(@NonNull Exception e) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Command Failed To Execute");
        eb.setDescription("The command failed to execute due to: " + e.getClass().getSimpleName());
        if(e.getMessage() != null) {
            if(e.getMessage().length() > MessageEmbed.TEXT_MAX_LENGTH) {
                eb.addField(
                        "Error Message", e.getMessage().substring(0, MessageEmbed.TEXT_MAX_LENGTH),
                        false
                );
            } else {
                eb.addField("Error Message", e.getMessage(), false);
            }
        } else {
            eb.addField(
                    "Error Message",
                    "Error message unable to be generated? Cause of error: " + e.getCause(),
                    false
            );
        }
        for(int i = 0; i < e.getStackTrace().length; i++) {
            eb.addField("Error Stack " + i, e.getStackTrace()[i].toString(), false);
        }
        eb.setColor(Color.RED);

        return eb.build();
    }

    /**
     * Provides the basic command data for a JDA command
     * @param type The type of command
     * @param name The name of the command
     * @param desc The description of the command
     * @return The command data
     * @throws IllegalArgumentException If the command type is invalid or if the command is a slash command and does not
     * have a description
     */
    @NonNull
    protected static CommandData getJDACommandData(
            @NonNull Command.Type type,
            @NonNull String name,
            @Nullable String desc
    ) {
        switch(type) {
            case SLASH -> {
                if(desc == null)
                    throw new IllegalArgumentException("Slash commands must have a description");

                return Commands.slash(name, desc);
            }
            case USER -> {
                return Commands.user(name);
            }
            case MESSAGE -> {
                return Commands.message(name);
            }
            default -> throw new IllegalArgumentException("Invalid command type");
        }
    }

    /**
     * Generates a command response for when a command is inactive
     * @return The command response
     */
    @NonNull
    private static CommandResponse<MessageEmbed> inactiveCommandResponse() {
        return new CommandResponse<>(
                null,
                true,
                new EmbedBuilder().setDescription("This command is currently not active").setColor(Color.RED).build()
        );
    }
}

