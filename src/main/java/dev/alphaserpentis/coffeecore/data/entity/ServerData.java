package dev.alphaserpentis.coffeecore.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Stores a server's data.
 * <p>
 *     This class can be extended to add more data to a server.
 * </p>
 */
public class ServerData extends EntityData {
    @SerializedName("onlyEphemeral")
    private boolean onlyEphemeral = true;

    public ServerData() {}

    public ServerData(boolean onlyEphemeral) {
        this.onlyEphemeral = onlyEphemeral;
    }

    public void setOnlyEphemeral(boolean onlyEphemeral) {
        this.onlyEphemeral = onlyEphemeral;
    }

    public boolean getOnlyEphemeral() {
        return onlyEphemeral;
    }
}
