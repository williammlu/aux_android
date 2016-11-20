package org.mobiledevsberkeley.auxmusic;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.Exclude;

import java.util.*;
/**
 * Created by wilbu on 10/15/2016.
 */

public class Playlist {
    private List<String> userDeviceIDList;            // contains UIDs from users. we could then convert this to a List<User>, but this may be slow
    private List<String> spotifySongIDList;     // contains spotify track URIs, we could then convert this to a List<Song>, but ^^
    private String playlistName;
    private String password;
    private GeoLocation location;
    private boolean hostApproval;
    private String hostDeviceID;
    private int currentSongIndex;
    private Boolean isActive;
    private long currentSongTime;
    private String coverArtURL;
    public String currentSongID;

    @Exclude
    private List<Song> spotifySongList;
    @Exclude
    private List<User> usersList;

    // potential host approval in the future



    // private List ourownsonglist - possible variable

    public Playlist() {
        spotifySongIDList = new ArrayList<>();
        userDeviceIDList = new ArrayList<>();
        currentSongID = "";
        currentSongTime = 0;
        currentSongIndex = 0;

        // initialize lists so that they're not null. not sure if this is necessary though. this null constructor
        // might be necessary when converting from Firebase stuff to a Java object
    }



    public Playlist(List<String> userDeviceIDList, List<String> spotifySongIDList, String playlistName,
                    String password, String hostDeviceID, int currentSongIndex, Boolean isActive, long currentSongTime, GeoLocation location) {
        this.userDeviceIDList = userDeviceIDList;
        this.spotifySongIDList = spotifySongIDList;
        this.playlistName = playlistName;
        this.password = password;
        this.hostDeviceID = hostDeviceID;
        this.currentSongIndex = currentSongIndex;
        this.isActive = isActive;
        this.currentSongTime = currentSongTime;
    }
    public void setCurrentSongID(String currentSongID) {
        this.currentSongID = currentSongID;
        this.location = location;
        this.hostApproval = hostApproval;
    }

    public void addSong(Song song) {
        spotifySongList.add(song);
        spotifySongIDList.add(song.getSongURI());
    }

    public void addUser(User user) {
        usersList.add(user);
        userDeviceIDList.add(user.getUID());
    }

    public String getHostDeviceID() {
        return hostDeviceID;
    }

    public String getCurrentSongID() {
        if (spotifySongIDList.size() > 0) {
            return spotifySongIDList.get(currentSongIndex);
        }
        return "";

    }
    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getCoverArtURL() {
        return coverArtURL;
    }

    public void setCoverArtURL(String coverArtURL) {
        this.coverArtURL = coverArtURL;
    }


    public long getCurrentSongTime() {
        return currentSongTime;
    }

    public void setCurrentSongTime(long songTime) {
        currentSongTime = songTime;
    }

    public List<String> getSpotifySongIDList() {
        return spotifySongIDList;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getUserDeviceIDList() {
        return userDeviceIDList;
    }

    public String getPlaylistName() {
        return playlistName;
    }


    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }
}
