package dev.alphaserpentis.coffeecore.commands;

import dev.alphaserpentis.coffeecore.core.CoffeeCore;
import dev.alphaserpentis.coffeecore.data.bot.CommandResponse;
import dev.alphaserpentis.coffeecore.handler.api.discord.servers.AbstractServerDataHandler;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.awt.*;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public abstract class BotCommand<T> {

    public enum TypeOfEphemeral {
        DEFAULT,
        DYNAMIC
    }

    public static class BotCommandOptions {
        protected String name;
        protected String description;
        protected long ratelimitLength = 0;
        protected long messageExpirationLength = 0;
        protected boolean onlyEmbed = false;
        protected boolean onlyEphemeral = false;
        protected boolean isActive = true;
        protected boolean deferReplies = false;
        protected boolean useRatelimits = false;
        protected boolean messagesExpire = false;
        protected TypeOfEphemeral typeOfEphemeral = TypeOfEphemeral.DEFAULT;

        public BotCommandOptions() {}

        public BotCommandOptions(
                @NonNull String name,
                @NonNull String description
        ) {
            this.name = name;
            this.description = description;
        }

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

        @NonNull
        public BotCommandOptions setName(@NonNull String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public BotCommandOptions setDescription(@NonNull String description) {
            this.description = description;
            return this;
        }

        @NonNull
        public BotCommandOptions setRatelimitLength(long ratelimitLength) {
            this.ratelimitLength = ratelimitLength;
            return this;
        }

        @NonNull
        public BotCommandOptions setMessageExpirationLength(long messageExpirationLength) {
            this.messageExpirationLength = messageExpirationLength;
            return this;
        }

        @NonNull
        public BotCommandOptions setOnlyEmbed(boolean onlyEmbed) {
            this.onlyEmbed = onlyEmbed;
            return this;
        }

        @NonNull
        public BotCommandOptions setOnlyEphemeral(boolean onlyEphemeral) {
            this.onlyEphemeral = onlyEphemeral;
            return this;
        }

        @NonNull
        public BotCommandOptions setActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        @NonNull
        public BotCommandOptions setDeferReplies(boolean deferReplies) {
            this.deferReplies = deferReplies;
            return this;
        }

        @NonNull
        public BotCommandOptions setUseRatelimits(boolean useRatelimits) {
            this.useRatelimits = useRatelimits;
            return this;
        }

        @NonNull
        public BotCommandOptions setMessagesExpire(boolean messagesExpire) {
            this.messagesExpire = messagesExpire;
            return this;
        }

        @NonNull
        public BotCommandOptions setTypeOfEphemeral(@NonNull TypeOfEphemeral typeOfEphemeral) {
            this.typeOfEphemeral = typeOfEphemeral;
            return this;
        }

        public boolean validate() {
            return name != null && description != null;
        }
    }

    protected final HashMap<Long, Long> ratelimitMap = new HashMap<>();
    protected final String name;
    protected final String description;
    protected final long ratelimitLength;
    protected final long messageExpirationLength;
    protected final boolean onlyEmbed;
    protected final boolean onlyEphemeral;
    protected final boolean isActive;
    protected final boolean deferReplies;
    protected final boolean useRatelimits;
    protected final boolean messagesExpire;
    protected final TypeOfEphemeral ephemeralType;
    protected long commandId;
    protected CoffeeCore core;

    public BotCommand() {
        throw new UnsupportedOperationException("Unsupported constructor");
    }

    public BotCommand(@NonNull BotCommandOptions options) {
        if(!options.validate())
            throw new IllegalArgumentException("Name and description weren't set!");

        name = options.name;
        description = options.description;
        ratelimitLength = options.ratelimitLength;
        messageExpirationLength = options.messageExpirationLength;
        onlyEmbed = options.onlyEmbed;
        onlyEphemeral = options.onlyEphemeral;
        isActive = options.isActive;
        deferReplies = options.deferReplies;
        useRatelimits = options.useRatelimits;
        messagesExpire = options.messagesExpire;
        ephemeralType = options.typeOfEphemeral;
    }

    /**
     * Method used to execute the command. Should contain the main logic of the command.
     * @param userId is the ID of the user who called the command
     * @param event is the SlashCommandInteractionEvent that contains the interaction
     * @return a nonnull CommandResponse containing either a MessageEmbed or Message
     */
    @NonNull
    abstract public CommandResponse<T> runCommand(final long userId, @NonNull final SlashCommandInteractionEvent event);

    /**
     * Method used to update the command.
     * This method is called when the bot is started and when the command is updated.
     * <p>
     * <b>This method should be overridden if the command uses subcommands.</b>
     * @param jda {@link JDA} instance
     */
    public void updateCommand(@NonNull JDA jda) {
        jda.upsertCommand(name, description)
                .queue(command -> commandId = command.getIdLong());
    }

    /**
     * A method that REQUIRES to be overridden if to be used for any {@link BotCommand} with an ephemeralType of {@link TypeOfEphemeral#DYNAMIC}
     * <p>
     * This method is currently only called if the command is DEFERRED.
     * <p>
     * Operations inside must NOT exceed the time it requires to ACKNOWLEDGE the API!
     * @param userId is a long ID provided by Discord for the user calling the command
     * @param event is a SlashCommandInteractionEvent that contains the interaction
     * @return a nonnull {@link CommandResponse} containing either a MessageEmbed or String
     */
    @NonNull
    public CommandResponse<T> beforeRunCommand(long userId, @NonNull SlashCommandInteractionEvent event) {
        throw new UnsupportedOperationException("beforeRunCommand needs to be overridden!");
    }

    /**
     * A method that checks and handles a potentially rate-limited user.
     * <p>
     * Commands not using embeds must override this method to return a {@link CommandResponse} containing String.
     * @param userId is a long ID provided by Discord for the user calling the command
     * @return a nullable CommandResponse that by default returns a MessageEmbed
     */
    @Nullable
    public CommandResponse<?> checkAndHandleRateLimitedUser(long userId) {
        if(isUserRatelimited(userId)) {
            return new CommandResponse<>(
                    new EmbedBuilder().setDescription(
                            "You are still rate limited. Expires in " + (ratelimitMap.get(userId) - Instant.now().getEpochSecond()) + " seconds."
                    ).build(),
                    onlyEphemeral
            );
        } else {
            return null;
        }
    }

    public void setCommandId(long id) {
        commandId = id;
    }
    public void setCore(@NonNull CoffeeCore core) {
        this.core = core;
    }
    @NonNull
    public String getName() {
        return name;
    }
    @NonNull
    public String getDescription() {
        return description;
    }
    public long getCommandId() {
        return commandId;
    }
    public long getRatelimitLength() {
        return ratelimitLength;
    }
    public long getMessageExpirationLength() {
        return messageExpirationLength;
    }
    public boolean isOnlyEmbed() { return onlyEmbed; }
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
    public boolean isUserRatelimited(long userId) {
        long ratelimit = ratelimitMap.getOrDefault(userId, 0L);

        if(ratelimit != 0) {
            return ratelimit > Instant.now().getEpochSecond();
        } else {
            return false;
        }
    }
    public boolean doMessagesExpire() {
        return messagesExpire;
    }
    public TypeOfEphemeral getEphemeralType() {
        return ephemeralType;
    }

    /**
     * Handles the reply of a command.
     * @param event {@link SlashCommandInteractionEvent} that contains the interaction
     * @param cmd {@link BotCommand} that contains the command used
     * @return {@link Message} that is the reply of the command
     */
    @NonNull
    public static Message handleReply(@NonNull final SlashCommandInteractionEvent event, @NonNull final BotCommand<?> cmd) {
        boolean sendAsEphemeral = cmd.isOnlyEphemeral();
        CommandResponse<?> responseFromCommand;
        AbstractServerDataHandler<?> sdh = cmd.core.getServerDataHandler();
        Object response;
        ReplyCallbackAction reply;

        if (cmd.isDeferReplies()) {
            InteractionHook hook = event.getHook();
            try {
                if (!sendAsEphemeral && event.getGuild() != null) {
                    sendAsEphemeral = sdh.serverDataHashMap.get(event.getGuild().getIdLong()).getOnlyEphemeral();
                }

                if (cmd.isOnlyEmbed()) {
                    if(cmd.ephemeralType == TypeOfEphemeral.DEFAULT) {
                        if (!sendAsEphemeral && event.getGuild() != null) {
                            event.deferReply(false).complete();
                        } else {
                            event.deferReply(sendAsEphemeral).complete();
                        }
                    } else {
                        CommandResponse<?> responseBeforeRunning = cmd.beforeRunCommand(event.getUser().getIdLong(), event);

                        event.deferReply(responseBeforeRunning.messageIsEphemeral()).complete();
                        if(responseBeforeRunning.messageResponse() != null) {
                            MessageEmbed message = (MessageEmbed) responseBeforeRunning.messageResponse();

                            event.replyEmbeds(message).complete();
                        }
                    }

                    responseFromCommand = cmd.isActive() ? cmd.runCommand(event.getUser().getIdLong(), event) : inactiveCommandResponse();
                    response = responseFromCommand.messageResponse();

                    if (cmd instanceof ButtonCommand) {
                        Collection<ItemComponent> buttons = ((ButtonCommand<?>) cmd).addButtonsToMessage(event);

                        if (cmd.isUsingRatelimits() && !cmd.isUserRatelimited(event.getUser().getIdLong())) {
                            cmd.ratelimitMap.put(event.getUser().getIdLong(), Instant.now().getEpochSecond() + cmd.getRatelimitLength());
                        }

                        if(!buttons.isEmpty())
                            return hook.sendMessageEmbeds((MessageEmbed) response).addActionRow(buttons).complete();
                    }

                    if (cmd.isUsingRatelimits() && !cmd.isUserRatelimited(event.getUser().getIdLong())) {
                        cmd.ratelimitMap.put(event.getUser().getIdLong(), Instant.now().getEpochSecond() + cmd.getRatelimitLength());
                    }

                    return hook.sendMessageEmbeds((MessageEmbed) response).setEphemeral(responseFromCommand.messageIsEphemeral()).complete();
                } else {
                    if(cmd.getEphemeralType() == TypeOfEphemeral.DEFAULT) {
                        if (!sendAsEphemeral && event.getGuild() != null) {
                            hook.setEphemeral(false);
                        } else {
                            hook.setEphemeral(sendAsEphemeral);
                        }
                    } else {
                        CommandResponse<?> responseBeforeRunning = cmd.beforeRunCommand(event.getUser().getIdLong(), event);

                        event.deferReply(responseBeforeRunning.messageIsEphemeral()).complete();
                        if(responseBeforeRunning.messageResponse() != null) {
                            event.reply((String) responseBeforeRunning.messageResponse()).complete();
                        }
                    }

                    responseFromCommand = cmd.isActive() ? cmd.runCommand(event.getUser().getIdLong(), event) : inactiveCommandResponse();
                    response = responseFromCommand.messageResponse();

                    return hook.sendMessage((String) response).complete();
                }
            } catch(Exception e) {
                return hook.sendMessageEmbeds(handleError(e)).complete();
            }
        }

        try {
            responseFromCommand = cmd.isActive() ? cmd.runCommand(event.getUser().getIdLong(), event) : inactiveCommandResponse();
            response = responseFromCommand.messageResponse();

            if (!sendAsEphemeral && event.getGuild() != null)
                sendAsEphemeral = sdh.serverDataHashMap.get(event.getGuild().getIdLong()).getOnlyEphemeral();

            if (cmd.isOnlyEmbed()) {
                if (!sendAsEphemeral && event.getGuild() != null) {
                    reply = event.replyEmbeds((MessageEmbed) response).setEphemeral(false);
                } else {
                    reply = event.replyEmbeds((MessageEmbed) response).setEphemeral(sendAsEphemeral);
                }
            } else {
                if (!sendAsEphemeral && event.getGuild() != null) {
                    reply = event.reply((String) response).setEphemeral(false);
                } else {
                    reply = event.reply((String) response).setEphemeral(sendAsEphemeral);
                }
            }

            if (cmd instanceof ButtonCommand) {
                Collection<ItemComponent> buttons = ((ButtonCommand<?>) cmd).addButtonsToMessage(event);

                if(!buttons.isEmpty())
                    reply = reply.addActionRow(buttons);
            }

            return reply.complete().retrieveOriginal().complete();
        } catch(Exception e) {
            return event.replyEmbeds(handleError(e)).setEphemeral(true).complete().retrieveOriginal().complete();
        }
    }

    /**
     * Deletes a message after a certain amount of time
     * @param command The command that is being executed
     * @param message The message to delete
     */
    protected static void letMessageExpire(@NonNull BotCommand<?> command, @NonNull Message message) {
        if(command.doMessagesExpire()) {
            message.delete().queueAfter(command.getMessageExpirationLength(), TimeUnit.SECONDS,
                    (ignored) -> {},
                    (fail) -> {
                        throw new RuntimeException(fail);
                    }
            );
        }
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
                eb.addField("Error Message", e.getMessage().substring(0, MessageEmbed.TEXT_MAX_LENGTH), false);
            } else {
                eb.addField("Error Message", e.getMessage(), false);
            }
        } else {
            eb.addField("Error Message", "Error message unable to be generated? Cause of error: " + e.getCause(), false);
        }
        for(int i = 0; i < e.getStackTrace().length; i++) {
            eb.addField("Error Stack " + i, e.getStackTrace()[i].toString(), false);
        }
        eb.setColor(Color.RED);

        return eb.build();
    }

    /**
     * Generates a command response for when a command is inactive
     * @return The command response
     */
    @NonNull
    private static CommandResponse<MessageEmbed> inactiveCommandResponse() {
        return new CommandResponse<>(new EmbedBuilder().setDescription("This command is currently not active").build(), null);
    }
}

