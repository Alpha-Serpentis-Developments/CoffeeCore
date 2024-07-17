package dev.alphaserpentis.coffeecore.data.entity;

import com.google.gson.annotations.SerializedName;

public class UserData extends EntityData {
    @SerializedName("showFullStackTrace")
    private boolean showFullStackTrace = false;

    @Override
    public UserData createNewEntityData() {
        return new UserData();
    }

    public void setShowFullStackTrace(boolean showFullStackTrace) {
        this.showFullStackTrace = showFullStackTrace;
    }

    public boolean getShowFullStackTrace() {
        return showFullStackTrace;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "showFullStackTrace=" + showFullStackTrace +
                '}';
    }
}
