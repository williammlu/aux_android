package org.mobiledevsberkeley.auxmusic;

import java.util.*;
/**
 * Created by wilbu on 10/15/2016.
 */

public class Playlist {
    private List<String> userDeviceIDList;            // contains UIDs from users. we could then convert this to a List<User>, but this may be slow
    private List<String> spotifySongIDList;     // contains spotify track URIs, we could then convert this to a List<Song>, but ^^
    private String playlistName;
    private String password;
    private String hostDeviceID;
    private String currentSongID;
    private Boolean isPlaying;
    private long currentSongTime;

    public Playlist() {
        // initialize lists so that they're not null. not sure if this is necessary though. this null constructor
        // might be necessary when converting from Firebase stuff to a Java object
    }

    public Playlist(List<String> userDeviceIDList, List<String> spotifySongIDList, String playlistName,
                    String password, String hostDeviceID, String currentSongID, Boolean isPlaying, long currentSongTime) {
        this.userDeviceIDList = userDeviceIDList;
        this.spotifySongIDList = spotifySongIDList;
        this.playlistName = playlistName;
        this.password = password;
        this.hostDeviceID = hostDeviceID;
        this.currentSongID = currentSongID;
        this.isPlaying = isPlaying;
        this.currentSongTime = currentSongTime;
    }

    public void addSong(Song song) {
        spotifySongIDList.add(song.getSongURI());
    }

    public void addUser(User user) {
        userDeviceIDList.add(user.getUID());
    }

    public List<String> getUserDeviceIDList() {
        return userDeviceIDList;
    }

    public String getHostDeviceID() {
        return hostDeviceID;
    }

    public String getCurrentSongID() {
        return currentSongID;
    }

    public Boolean getPlaying() {
        return isPlaying;
    }

    public long getCurrentSongTime() {
        return currentSongTime;
    }

    public List<String> getSpotifySongIDList() {
        return spotifySongIDList;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getuserDeviceIDList() {
        return userDeviceIDList;
    }

    public String getPlaylistName() {
        return playlistName;
    }
}
