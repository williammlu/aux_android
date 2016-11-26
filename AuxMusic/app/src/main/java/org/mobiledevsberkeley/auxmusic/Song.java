package org.mobiledevsberkeley.auxmusic;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by wilbu on 10/22/2016.
 */

public class Song {
    // Generic song class that has the necessary extracted information from an API (Spotify for now, possibly Soundcloud, etc. in the future)
//    private String songURI;
    private String songId;

    private List<String> imageUrl; // potentially save the image as a different variable
    private String songName;
    private String artistName;
    private String albumName;
    private long trackLength;

    public Song(Track t) {
        this.songId = t.id;
        this.imageUrl = new ArrayList<String>();
        for (Image im: t.album.images) {
            imageUrl.add(im.url);
        }
        this.songName = t.name;
        this.artistName = "";
        for(ArtistSimple as : t.artists) {
            this.artistName += as.name  + ", ";
        }
        this.artistName = this.artistName.substring(0, this.artistName.length() - 2);
        this.albumName = t.album.name;
        this.trackLength = t.duration_ms;
    }

    public Song(String songId, ArrayList<String>imageUrl, String songName, String artistName, String albumName, long trackLength) {
        this.songId = songId;
        this.imageUrl = imageUrl;
        this.songName = songName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.trackLength = trackLength;
    }

    /**
     * Empty constructor for Firebase
     */
    public Song() {
        this.songId = "";
        this.imageUrl = new ArrayList<>();
        this.songName = "";
        this.artistName = "";
        this.albumName = "";
        this.trackLength = 0;
    }


    public String getSongId() {
        return songId;
    }


    public String getSongURI() {
        return "spotify:track:" + songId;
    }

    public String getImageUrl(int px) {

        if (this.imageUrl.size() < 3) {
            return "";
        }
        if (px <= 64) {
            return this.imageUrl.get(2);
        } else if (px <= 300) {
            return this.imageUrl.get(1);
        } else {
            return this.imageUrl.get(0);
        }
    }

    /**
     * Creates a test song object (Fake Love by Drake) instead of hardcoding stuff every time
     * @return
     */
    public static Song getTestSong() {
        return new Song("6NMNgWgEAzde5M8U3lc6FN",
                new ArrayList<String>(Arrays.asList("https://i.scdn.co/image/5b14b24dea78b0a14244ccb86f3bfd20bf77326d", "https://i.scdn.co/image/177939e6656bd0ae46d12e1f36e9162016d28a3c", "https://i.scdn.co/image/7b42b976267520f4dbb2f67e1baa63ca13bdbfdb")),
                "Fake Love",
                "Drake",
                "Fake Love",
                207813);
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


    public String toString() {
        return songName + "; " + artistName + "; "  + albumName;
    }


}
