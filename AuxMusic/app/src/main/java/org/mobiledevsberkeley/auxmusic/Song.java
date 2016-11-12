package org.mobiledevsberkeley.auxmusic;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

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

    public Song(Track t) {
        this.songURI = t.uri;
        this.imageUrl = "NO URL RIGHT NOW";
        this.songName = t.name;
        for(ArtistSimple as : t.artists) {
            this.artistName += as.name  + ", ";
        }
        this.artistName = this.artistName.substring(0, this.artistName.length() - 2);
        this.albumName = t.album.name;
        this.trackLength = t.duration_ms;
    }

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
