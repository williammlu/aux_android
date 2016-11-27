package org.mobiledevsberkeley.auxmusic;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by wilbu on 10/15/2016.
 */

public class User {
    private String UID;
    private String spotifyAuthKey; // I chose to keep the isHost boolean and not just have a null check on this string because it is possible
                                   // that users can also authenticate in order to add their own playlists
    // Could add future variables like "AuthKey" to accommodate other APIs (Soundcloud, etc.)
    private String participantName; // Optional, by default this is "Anon"
    private String playlistKey; // Key to the current playlist to check if they're in a playlist right now

    private List<String> pastPlaylists = new ArrayList<>(); // arraylist of either Playlist objects or String (playlistKeys). not sure right now which to use.

    @Exclude
    HashSet<String> pastPlaylistsSet = new HashSet<>();

    public User() {
        // Null constructor might be necessary for Firebase -> Java conversion
    }

    public User(String UID, String spotifyAuthKey, String participantName, String playlistKey, ArrayList<String> pastPlaylists) {
        this.UID = UID;
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

    public List<String> getPastPlaylists() { return pastPlaylists; }

    public void addToPastPlaylists(String playlistKey) {
        if (!pastPlaylistsSet.contains(playlistKey)) {
            Log.d("debug", "creating new past playlists");
            pastPlaylists.add(playlistKey);
            pastPlaylistsSet.add(playlistKey);
        }
    }

    public void removeFromPastPlaylists(String playlistKey) {
        if (pastPlaylists != null) {
            pastPlaylists.remove(playlistKey);
        } else {
            Log.d("debug", "we have a major error, removing past playlist from empty list");
        }
    }



    public String getParticipantName() {
        return participantName;
    }
}
