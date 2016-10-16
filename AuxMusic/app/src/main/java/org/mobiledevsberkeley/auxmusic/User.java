package org.mobiledevsberkeley.auxmusic;

/**
 * Created by wilbu on 10/15/2016.
 */

public class User {
    private String UID;
    private boolean isHost;
    private String participantName; // Optional

    public User() {
        // swag
    }

    public String getUID() {
        return UID;
    }

    public boolean isHost() {
        return isHost;
    }

    public String getParticipantName() {
        return participantName;
    }
}
