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
    private List<Song> songList; // or Queue<Song> for our own queue
    private List<User> participantList;
    private String playlistKey; // Firebase hash to have easy access to itself in Firebase
    // we can have singleton class reference here for database reference, etc.
    // other playlist settings like tracklocation, explicitness for filtering (future option)
    // WE NEED MORE STUFF HERE BUT I'M TIRED AF SO YEAH

    public Playlist() {
        // initialize lists so that they're not null. not sure if this is necessary though. this null constructor
        // might be necessary when converting from Firebase stuff to a Java object
    }

    public Playlist(List<String> userIDList, List<String> spotifySongIDList, String playlistName, String password) {
        this.userIDList = userIDList;
        this.spotifySongIDList = spotifySongIDList;
        this.playlistName = playlistName;
        this.password = password;
    }

    public String getplaylistKey() {
        return playlistKey;
    }

    public void addSong(Song song) {
        songList.add(song);
        spotifySongIDList.add(song.getSpotifySongURI());
        // no need to update database because the singleton should add to database before calling this.
    }

    public void addUser(User user) {
        participantList.add(user);
        userIDList.add(user.getUID());
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
