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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String USERS_NODE = "users";
    public static final String PLAYLISTS_NODE = "playlists";
    public static final String SPOTIFYSONGID_LIST = "spotifySongIDList";
//    public static final String SPOTIFYSONG_LIST = "spotifySongList";
    public static final String USERID_LIST = "userDeviceIDList";

    private static AuxSingleton auxSingleton = new AuxSingleton();
//TODO: Once we figure out what we're doing with current/active playlists, may need to change methods.
    private DatabaseReference dbReference;
    private Playlist currentPlaylist;
    private User currentUser;
    private DatabaseReference playlistRef;
    private DatabaseReference userRef;

    private ArrayList<Playlist> myPlaylists = new ArrayList<>();

//    private ValueEventListener userListener;
    // have song playlists and users playlists right here. this is a weird structure, but i'm not sure how to deal with this yet.

    private AuxSingleton() {
        dbReference = FirebaseDatabase.getInstance().getReference();
   }

    private void updateValue(DatabaseReference ref, String key, Object value) {
        ref.child(key).setValue(value);
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put(key, value);
//        ref.updateChildren(childUpdates);
        Log.d(TAG, "we added a " + key + ", at " + ref.getKey() + "yay!");
    }

    public static AuxSingleton getInstance() {
        return auxSingleton;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        String uid = user.getUID();
        userRef = dbReference.child(USERS_NODE).child(uid);
        this.currentUser = user;
    }
    public ArrayList<Playlist> getMyPlaylists() { return myPlaylists;}
    public void getPlaylistByIDForMyPlaylists(String playlistID) {
        System.out.println(playlistID);
        DatabaseReference temp = dbReference.child(PLAYLISTS_NODE).child(playlistID);
        temp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Playlist playlist = dataSnapshot.getValue(Playlist.class);
                System.out.println("did i even finish tho");
                myPlaylists.add(playlist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUser(final String uid, final SignInCallback callback) {
        if(userRef == null) {
            userRef = dbReference.child(USERS_NODE).child(uid);
        }
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User snapshotUser = dataSnapshot.getValue(User.class);
                if (snapshotUser == null) {
                    User newUser = new User();
                    newUser.setUID(uid);
                    auxSingleton.addUser(newUser);
                    Log.d(TAG, "snapshot was null, we now have user with uid: " + currentUser.getUID() + " and isHost: " + currentUser.isHost());
                } else {
                    auxSingleton.setCurrentUser(snapshotUser);
                    Log.d(TAG, "snapshot was not null, we have old user with uid: " + snapshotUser.getUID() + " and isHost: " + snapshotUser.isHost());
                }
                callback.userOnComplete();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void hasCurrentPlaylist(final SignInCallback callback) {
        //this will set the playlist variable in the global scope, however we only want to ever use the SINGLETON playlist
        final String playlistKey = currentUser.getPlaylistKey();
        Log.d(TAG, "in hascurrentplaylist singleton, key is: " + playlistKey);
        if (playlistKey == null || playlistKey.equals("")) {
            callback.playlistOnComplete(false);
        } else {
            dbReference.child(PLAYLISTS_NODE).child(playlistKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Playlist playlist = dataSnapshot.getValue(Playlist.class);
                    boolean hasCurrent = false;
                    if (playlist != null && playlist.getActive()) {
                        setCurrentPlaylist(playlist, playlistKey);
                        hasCurrent = true;
                    }
                    callback.playlistOnComplete(hasCurrent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
        }
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist, String playlistKey) {
        // TAKE CARE OF STARTING OFF ON AN OLD PLAYLIST
//        if (playlistListener != null) {
//            playlistRef.removeEventListener(playlistListener);
//        }
        this.currentPlaylist = currentPlaylist;
        if (playlistKey != null) {
            playlistRef = dbReference.child(PLAYLISTS_NODE).child(playlistKey);
        }
//        initializePlaylistListener();
        currentUser.setPlaylistKey(playlistKey);
        updateValue(userRef, "playlistKey", playlistKey);
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

//        initializePlaylistListener();
    }

    public void createPlaylist(String partyName, String password, GeoLocation mGeoLocation) {
        // only host could have called this method. thus, the current user is a host.
        currentUser.setHost(true);
        updateValue(dbReference.child(USERS_NODE).child(currentUser.getUID()), "host", true);
        ArrayList<String> userUIDList = new ArrayList<String>();
        userUIDList.add(currentUser.getUID());

        ArrayList<String> songList = new ArrayList<String>();

        playlistRef = dbReference.child(PLAYLISTS_NODE).push();
        //TODO: Dunno why this is commented out. -Young
//        setCurrentPlaylist(new Playlist(userUIDList, songList, partyName, password, currentUser.getUID(),
//                0, true, 0, mGeoLocation), playlistRef.getKey());
    }

    public void addToPlaylist(String key, String value) {

    }

    public void addSong(Song song) {
        currentPlaylist.addSong(song);
        updateValue(playlistRef, SPOTIFYSONGID_LIST, currentPlaylist.getSpotifySongIDList());
        Log.d(TAG, "we added a song (maybe), yay!");
        // add to database using dbReference with the appropriate hashes, child, etc.
    }

    public void addUser(User user) {
        dbReference.child(USERS_NODE).child(user.getUID()).setValue(user);
        setCurrentUser(user);
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
