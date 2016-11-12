package org.mobiledevsberkeley.auxmusic;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by wilbu on 10/22/2016.
 */

public class AuxSingleton {
    private static AuxSingleton auxSingleton = new AuxSingleton();

    private DatabaseReference dbReference;
    private Playlist currentPlaylist;
    private User currentUser;

    private AuxSingleton() {
        dbReference = FirebaseDatabase.getInstance().getReference();
    }

    public static AuxSingleton getInstance() {
        return auxSingleton;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    public void createPlaylist() {

    }

    public void addToPlaylist(String key, String value) {

    }

    public void addSong(Song song) {
        // add to database using dbReference with the appropriate hashes, child, etc.
        DatabaseReference playlistRef = dbReference.child("playlists").push();
        String playlistKey = playlistRef.getKey(); // store this somewhere?
        currentPlaylist.addSong(song);
    }

    public void addUser(User user) {
        // add to database using dbReference with the appropriate hashes, child, etc.
    }

    public void removeSong(Song song) {

    }

    public void removeUser(User user) {

    }

    public DatabaseReference getDataBaseReference() {
        return dbReference;
    }
}
