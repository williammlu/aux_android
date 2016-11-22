package org.mobiledevsberkeley.auxmusic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilbu on 10/15/2016.
 */

public class User {
    private String UID;
    private boolean isHost;
    private String spotifyAuthKey; // I chose to keep the isHost boolean and not just have a null check on this string because it is possible
                                   // that users can also authenticate in order to add their own playlists
    // Could add future variables like "AuthKey" to accommodate other APIs (Soundcloud, etc.)
    private String participantName; // Optional, by default this is "Anon"
    private String playlistKey; // Key to the current playlist to check if they're in a playlist right now

    private List<String> pastPlaylists; // arraylist of either Playlist objects or String (playlistKeys). not sure right now which to use.

    public User() {
        // Null constructor might be necessary for Firebase -> Java conversion
    }

    public User(String UID, boolean isHost, String spotifyAuthKey, String participantName, String playlistKey, ArrayList<String> pastPlaylists) {
        this.UID = UID;
        this.isHost = isHost;
        this.spotifyAuthKey = spotifyAuthKey;
        this.participantName = participantName;
        this.playlistKey = playlistKey;
        this.pastPlaylists = pastPlaylists;
    }

    public String getPlaylistKey() {
        return playlistKey;
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

    public void setPlaylistKey(String playlistKey) { this.playlistKey = playlistKey; }

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

    public List<String> getPastPlaylists() {
        return pastPlaylists;
    }

    public String getParticipantName() {
        return participantName;
    }
}
