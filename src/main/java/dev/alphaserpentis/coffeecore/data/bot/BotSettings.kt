package dev.alphaserpentis.coffeecore.data.bot;

import dev.alphaserpentis.coffeecore.helper.Validate;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;

/**
 * Class used to contain settings for the bot.
 */
public class BotSettings {

    /**
     * The Discord user ID of the bot owner.
     */
    private final long botOwnerId;
    /**
     * The path to the server data file.
     */
    private final String serverDataPath;
    /**
     * Whether to update the commands at launch.
     */
    private final boolean updateCommandsAtLaunch;
    /**
     * Whether to register the default commands.
     */
    private final boolean registerDefaultCommands;
    /**
     * Optional information to highlight what the bot is about.
     */
    private AboutInformation aboutInformation = null;

    public BotSettings(
            boolean updateCommandsAtLaunch,
            boolean registerDefaultCommands
    ) {
        this.botOwnerId = 0;
        this.serverDataPath = null;
        this.updateCommandsAtLaunch = updateCommandsAtLaunch;
        this.registerDefaultCommands = registerDefaultCommands;
    }

    public BotSettings(
            long botOwnerId,
            boolean updateCommandsAtLaunch,
            boolean registerDefaultCommands
    ) {
        this.botOwnerId = botOwnerId;
        this.serverDataPath = null;
        this.updateCommandsAtLaunch = updateCommandsAtLaunch;
        this.registerDefaultCommands = registerDefaultCommands;
    }

    public BotSettings(
            long botOwnerId,
            String serverDataPath,
            boolean updateCommandsAtLaunch,
            boolean registerDefaultCommands
    ) {
        this.botOwnerId = botOwnerId;
        this.serverDataPath = serverDataPath;
        this.updateCommandsAtLaunch = updateCommandsAtLaunch;
        this.registerDefaultCommands = registerDefaultCommands;
    }

    public BotSettings(
            long botOwnerId,
            String serverDataPath,
            boolean updateCommandsAtLaunch,
            boolean registerDefaultCommands,
            @NonNull AboutInformation aboutInformation
    ) {
        Validate.throwOnNull(aboutInformation);

        this.botOwnerId = botOwnerId;
        this.serverDataPath = serverDataPath;
        this.updateCommandsAtLaunch = updateCommandsAtLaunch;
        this.registerDefaultCommands = registerDefaultCommands;
        this.aboutInformation = aboutInformation;
    }

    public long getBotOwnerId() {
        return botOwnerId;
    }

    @Nullable
    public String getServerDataPath() {
        return serverDataPath;
    }

    public boolean isUpdateCommandsAtLaunch() {
        return updateCommandsAtLaunch;
    }

    public boolean isRegisterDefaultCommands() {
        return registerDefaultCommands;
    }

    @NonNull
    public AboutInformation getAboutInformation() {
        return aboutInformation;
    }

    public void setAboutInformation(@NonNull AboutInformation aboutInformation) {
        this.aboutInformation = Validate.throwOnNull(aboutInformation);
    }
}
