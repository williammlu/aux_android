package org.mobiledevsberkeley.auxmusic;

/**
 * Created by wilbu on 10/22/2016.
 */

public class Song {
    private String spotifySongURI;
    private String imageUrl; // potentially save as different variable
    private String songName;
    private String artistName;
    private String albumName;
    private double trackLength;
    // potentially have variables about explicitness for censorship. could also add any other
    // necessary variables in the future, just create them here, create getters, and then
    // initialize from Spotify API in queryAndInitialize()

    public Song(String spotifySongURI) {
        this.spotifySongURI = spotifySongURI;
        queryAndInitialize();
    }

    private void queryAndInitialize() {
        // query from Spotify the information about the track given the spotifySongUri
    }

    public String getSpotifySongURI() {
        return spotifySongURI;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public double getTrackLength() {
        return trackLength;
    }
}
