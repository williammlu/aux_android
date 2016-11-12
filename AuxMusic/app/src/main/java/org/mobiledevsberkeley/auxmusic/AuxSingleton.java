package org.mobiledevsberkeley.auxmusic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.SpotifyPlayer;
import android.util.Log;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by wilbu on 10/22/2016.
 */

public class AuxSingleton {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private static Context context;


    private static String spotifyAuthId = "";
    private static Player musicPlayer;



    private static SpotifyService spotifyService;

    private static SpotifyPlayer spotifyPlayer;
    public static final String TAG = "debug";
    private static AuxSingleton auxSingleton = new AuxSingleton();

    private static DatabaseReference dbReference;
    private static Playlist currentPlaylist;
    private static User currentUser;

    private AuxSingleton() {
        dbReference = FirebaseDatabase.getInstance().getReference();
    }

    public static AuxSingleton getInstance() {
        return auxSingleton;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(final User currentUser) {
        String uid = currentUser.getUID();
        final DatabaseReference usersRef = dbReference.child("users");
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User snapshotUser = dataSnapshot.getValue(User.class);
                if (snapshotUser != null) {
                    auxSingleton.currentUser = snapshotUser;
                    Log.d(TAG, "snapshot was not null, we have old user with uid: " + snapshotUser.getUID() + " and isHost: " + snapshotUser.isHost());
                } else {
                    auxSingleton.currentUser = currentUser;
                    auxSingleton.addUser(currentUser);
                    Log.d(TAG, "snapshot was null, we now have old user with uid: " + currentUser.getUID() + " and isHost: " + currentUser.isHost());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    public void createPlaylist(String partyName, String password, GeoLocation mGeoLocation, Boolean hostApproval) {
        // only host could have called this method. thus, the current user is a host.
        currentUser.setHost(true);
        ArrayList<String> userUIDList = new ArrayList<String>();
        userUIDList.add(currentUser.getUID());

        ArrayList<String> songList = new ArrayList<String>();

        setCurrentPlaylist(new Playlist(userUIDList, songList, partyName, password, currentUser.getUID(),
                0, false, 0, mGeoLocation, hostApproval));

        DatabaseReference playlistRef = dbReference.child("playlists").push();
        currentUser.setPlaylistKey(playlistRef.getKey());
        playlistRef.setValue(currentPlaylist, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.d(TAG, "Data could not be saved " + databaseError.getMessage());
                        } else {
                            Log.d(TAG, "Data saved successfully.");
                        }
                    }
                });
    }

    public void addToPlaylist(String key, String value) {

    }

    public static void addSong(Song song) {
        // add to database using dbReference with the appropriate hashes, child, etc.
    }

    public void addUser(User user) {
        dbReference.child("users").child(user.getUID()).setValue(user);
        // add to database using dbReference with the appropriate hashes, child, etc.
    }

    public void removeSong(Song song) {

    }

    public void removeUser(User user) {

    }

    public DatabaseReference getDataBaseReference() {
        return dbReference;
    }



    public static String getSpotifyAuthId() {
        return spotifyAuthId;
    }

    public static void setSpotifyAuthId(String spotifyAuthId) {
        AuxSingleton.spotifyAuthId = spotifyAuthId;
    }

    public static boolean hasMusicPlayer() {
        return musicPlayer != null;
    }

    public static Player getMusicPlayer() {
        return musicPlayer;
    }

    public static void setMusicPlayer(Player musicPlayer) {
        AuxSingleton.musicPlayer = musicPlayer;
    }

    public static SpotifyService getSpotifyService() {
        if (spotifyService == null) {
            SpotifyApi api = new SpotifyApi();
            // Most (but not all) of the Spotify Web API endpoints require authorisation.
            // If you know you'll only use the ones that don't require authorisation you can skip this step
//                api.setAccessToken(AuxSingleton.getSpotifyAuthId());
            spotifyService = api.getService();
        }
        return spotifyService;
    }



    public static Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
