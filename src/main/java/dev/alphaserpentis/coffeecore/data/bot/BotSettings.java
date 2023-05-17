package dev.alphaserpentis.coffeecore.data.bot;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * Class used to contain settings for the bot.
 */
public class BotSettings {
    private final long botOwnerId;
    private final String serverDataPath;
    private final boolean updateCommandsAtLaunch;
    private final boolean registerDefaultCommands;
    private AboutInformation aboutInformation = null;

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
            AboutInformation aboutInformation
    ) {
        this.botOwnerId = botOwnerId;
        this.serverDataPath = serverDataPath;
        this.updateCommandsAtLaunch = updateCommandsAtLaunch;
        this.registerDefaultCommands = registerDefaultCommands;
        this.aboutInformation = aboutInformation;
    }

    public long getBotOwnerId() {
        return botOwnerId;
    }

    @NonNull
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
        this.aboutInformation = aboutInformation;
    }
}
