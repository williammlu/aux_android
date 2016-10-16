package org.mobiledevsberkeley.auxmusic;

import java.util.*;
/**
 * Created by wilbu on 10/15/2016.
 */

public class Playlist {
    private List<String> userIDList;            // contains UIDs from users. we could then convert this to a List<User>, but this may be slow
    private List<String> spotifySongIDList;     // contains spotify track URIs, we could then convert this to a List<Song>, but ^^
    private String playlistName;
    private String password;
    // WE NEED MORE STUFF HERE BUT I'M TIRED AF SO YEAH

    public Playlist() {
        // yolo
    }

    public Playlist(List<String> userIDList, List<String> spotifySongIDList, String playlistName, String password) {
        this.userIDList = userIDList;
        this.spotifySongIDList = spotifySongIDList;
        this.playlistName = playlistName;
        this.password = password;
    }

    public List<String> getSpotifySongIDList() {
        return spotifySongIDList;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getUserIDList() {
        return userIDList;
    }

    public String getPlaylistName() {
        return playlistName;
    }
}
