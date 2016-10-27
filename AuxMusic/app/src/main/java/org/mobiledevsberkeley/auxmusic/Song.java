package org.mobiledevsberkeley.auxmusic;

/**
 * Created by wilbu on 10/22/2016.
 */

public class Song {
    // Generic song class that has the necessary extracted information from an API (Spotify for now, possibly Soundcloud, etc. in the future)
    private String songURI;
    private String imageUrl; // potentially save the image as a different variable
    private String songName;
    private String artistName;
    private String albumName;
    private long trackLength;

    public Song(String songURI, String imageUrl, String songName, String artistName, String albumName, long trackLength) {
        this.songURI = songURI;
        this.imageUrl = imageUrl;
        this.songName = songName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.trackLength = trackLength;
    }

    public String getSongURI() {
        return songURI;
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

    public long getTrackLength() {
        return trackLength;
    }
}
