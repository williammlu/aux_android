package org.mobiledevsberkeley.auxmusic;

/**
 * Created by wilbu on 10/15/2016.
 */

public class User {
    private String UID;
    private boolean isHost;
    private String spotifyAuthKey; // I chose to keep the isHost boolean and not just have a null check on this string because it is possible
                                   // that users can also authenticate in order to add their own playlists
    private String participantName; // Optional, by default this is "anon"

    public User() {
        // swag
    }

    public User(String UID, boolean isHost, String spotifyAuthKey, String participantName) {
        this.UID = UID;
        this.isHost = isHost;
        this.spotifyAuthKey = spotifyAuthKey;
        this.participantName = participantName;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public void setSpotifyAuthKey(String spotifyAuthKey) {
        this.spotifyAuthKey = spotifyAuthKey;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getSpotifyAuthKey() {
        return spotifyAuthKey;
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
