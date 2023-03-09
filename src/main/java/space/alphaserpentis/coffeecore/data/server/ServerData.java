package space.alphaserpentis.coffeecore.data.server;

/**
 * Stores a server's data.
 * <p>
 *     This class can be extended to add more data to a server.
 * </p>
 */
public class ServerData {
    private boolean onlyEphemeral = true;
    public void setOnlyEphemeral(boolean onlyEphemeral) {
        this.onlyEphemeral = onlyEphemeral;
    }
    public boolean getOnlyEphemeral() {
        return onlyEphemeral;
    }
}
