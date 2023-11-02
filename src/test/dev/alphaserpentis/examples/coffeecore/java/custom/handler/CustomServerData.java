package dev.alphaserpentis.examples.coffeecore.java.custom.handler;

import dev.alphaserpentis.coffeecore.data.server.ServerData;

public class CustomServerData extends ServerData {
    private String customData = "This is custom data!";

    public CustomServerData() {}

    public CustomServerData(boolean onlyEphemeral) {
        super(onlyEphemeral);
    }

    public CustomServerData(boolean onlyEphemeral, String customData) {
        super(onlyEphemeral);
        this.customData = customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public String getCustomData() {
        return customData;
    }
}
