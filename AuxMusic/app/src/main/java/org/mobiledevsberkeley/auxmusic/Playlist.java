package org.mobiledevsberkeley.auxmusic;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.Exclude;

import java.util.*;

import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static org.mobiledevsberkeley.auxmusic.AuxSingleton.getSpotifyService;

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
    private String coverArtURL = "";
    private String hostSpotifyName;
    private String regexedPlaylistName;


    private String playlistKey;

    public String currentSongID;

    @Exclude
    private List<Song> spotifySongList;
    @Exclude
    private List<User> usersList;

    // potential host approval in the future


    // private List ourownsonglist - possible variable

    public Playlist() {
        spotifySongList = new ArrayList<>();
        spotifySongIDList = new ArrayList<>();
        userDeviceIDList = new ArrayList<>();
        currentSongID = "";
        currentSongTime = 0;
        currentSongIndex = 0;

        // initialize lists so that they're not null. not sure if this is necessary though. this null constructor
        // might be necessary when converting from Firebase stuff to a Java object
    }



    // Convert to builder in the future for cleaner code
    public Playlist(List<String> userDeviceIDList, List<String> spotifySongIDList, String playlistName,
                    String password, String hostDeviceID, int currentSongIndex, Boolean isActive, long currentSongTime, GeoLocation location,
                    String coverArtURL, String hostSpotifyName) {
        this.userDeviceIDList = userDeviceIDList;
        this.spotifySongIDList = spotifySongIDList;
        if (coverArtURL == null || coverArtURL == "") {
            setImageUrl();
        } else
        {
            this.coverArtURL = coverArtURL;
        }
        this.spotifySongList = new ArrayList<>(); //.getInstance().getSongs(spotifySongIDList);
        this.playlistName = playlistName;
        this.password = password;
        this.hostDeviceID = hostDeviceID;
        this.currentSongIndex = currentSongIndex;
        this.isActive = isActive;
        this.currentSongTime = currentSongTime;
        this.hostSpotifyName = hostSpotifyName;
        this.regexedPlaylistName = playlistName.replaceAll("[^A-Za-z]","").toLowerCase();
    }
    public void setCurrentSongID(String currentSongID) {
        this.currentSongID = currentSongID;
        this.location = location;
        this.hostApproval = hostApproval;
    }

    public void setSpotifySongIDList(List<String> spotifySongIDList) {
        this.spotifySongIDList = spotifySongIDList;
    }

    public void addSong(Song song) {
//        spotifySongList.add(song);
        spotifySongIDList.add(song.getSongId());
    }

    public void removeSong(Song song) {
//        spotifySongList.remove(song);
        spotifySongIDList.remove(song.getSongId());
    }

    public void addUser(User user) {
//        usersList.add(user);
        userDeviceIDList.add(user.getUID());
    }

    public void removeUser(User user) {
//        usersList.remove(user);
        userDeviceIDList.remove(user.getUID());
    }

    public String getHostDeviceID() {
        return hostDeviceID;
    }

    public String getCurrentSongURI() {
        if (spotifySongIDList.size() > 0) {
            return "spotify:track:" + spotifySongIDList.get(currentSongIndex);
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

    public List<Song> getSpotifySongList() {
        return spotifySongList;
    }

    /**
     * Mutates original list
     * @param sl
     */
    public void setSpotifySongList(List<Song> sl) {
        this.spotifySongList.clear();
        this.spotifySongList.addAll(sl);
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

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
        this.regexedPlaylistName = playlistName.replaceAll("[^A-Za-z]","").toLowerCase();
    }
    public String getRegexedPlaylistName() {
        return regexedPlaylistName;
    }


    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }

    public String getHostSpotifyName() {
        return hostSpotifyName;
    }

    public String getPlaylistKey() {
        return playlistKey;
    }

    public void setPlaylistKey(String key) {
        playlistKey = key;
    }

    public void setImageUrl() {
        //TODO: finish once getting a song from ID is finalized
        if (spotifySongIDList.size() > 0) {
            String firstSongId = spotifySongIDList.get(0);
            AuxSingleton.getSpotifyService().getTrack(firstSongId, new Callback<Track>() {
                @Override
                public void success(Track track, Response response) {
                    setCoverArtURL(track.album.images.get(0).url);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }
}
