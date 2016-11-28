package org.mobiledevsberkeley.auxmusic;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by wilbu on 10/22/2016.
 */

public class AuxSingleton {
    private static Context context;
    private static String spotifyAuthToken = "";


    private static CurrentSongView currentSongView;
    private static PlayerView playerView;

    // TODO: create player interface
    private static Player spotifyPlayer;
    private static PlayerInterface auxPlayer;
    private static SpotifyService spotifyService = new SpotifyApi().getService();

    private static MusicAdapter musicAdapter;

    private static Object playerReference;
    public static final String TAG = "debug";
    public static final String PASTPLAYLISTS = "pastplaylists";
    public static final String USERS_NODE = "users";
    public static final String PLAYLISTS_NODE = "playlists";
    public static final String SPOTIFYSONGID_LIST = "spotifySongIDList";
    public static final String USERID_LIST = "userDeviceIDList";
    public static final String PASTPLAYLISTS_LIST = "pastPlaylists";
    public static final String CURRENTSONGINDEX = "currentSongIndex";

    public static HashMap<String, Song> songCache = new HashMap<>();

    private static AuxSingleton auxSingleton = new AuxSingleton();
//TODO: Once we figure out what we're doing with current/active playlists, may need to change methods.
    private DatabaseReference dbReference;
    private Playlist currentPlaylist;
    private User currentUser;
    private DatabaseReference playlistRef;
    private DatabaseReference userRef;

    private ValueEventListener spotifySongIDListener;
    private ValueEventListener currentSongIndexListener;

    private ArrayList<Playlist> myPlaylists;
    /*This boolean is to check if the current playlist is active for the current user.
    Ie the playlist itself has to be active (host didnt leave) and the playlist has to be
    active for this particular user (what this boolean checks)*/
    public boolean hasActive; //this was set in the ActualStartActivity, it checks whether user has a playlistKey and that playlist active
    public boolean isCurrentActive;

//    private ValueEventListener userListener;
    // have song playlists and users playlists right here. this is a weird structure, but i'm not sure how to deal with this yet.

    private AuxSingleton() {
        dbReference = FirebaseDatabase.getInstance().getReference();
   }

    private void updateValue(DatabaseReference ref, String key, Object value) {
        ref.child(key).setValue(value);
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
    public ArrayList<Playlist> getMyPlaylists() {
        if (myPlaylists != null) {
            Log.d(PASTPLAYLISTS, "size is " + myPlaylists.size());
        }
        return myPlaylists;
    }

    public void populateMyPlaylists(final SignInCallback callback) {
        if (currentUser != null) {
            if (myPlaylists == null) {
                myPlaylists = new ArrayList<>();
                final List<String> past = currentUser.getPastPlaylists();
                if (past.size() == 0) {
                    callback.playlistOnComplete(false);
                }
                for (String s: past) {
                    dbReference.child(PLAYLISTS_NODE).child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Playlist playlist = dataSnapshot.getValue(Playlist.class);
                            if (playlist != null) {
                                Log.d("PASTPLAYLISTS", "we populating with a nonnull playlist called " + playlist.getPlaylistKey());
                                myPlaylists.add(playlist);
                                if (myPlaylists.size() == past.size()) {
                                    callback.playlistOnComplete(false);
                                }
//                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                callback.playlistOnComplete(false);
            }
        }
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
//                    Log.d(TAG, "snapshot was null, we now have user with uid: " + currentUser.getUID() + " and isHost: " + currentUser.isHost());
                } else {
                    snapshotUser.setPastPlaylists();
                    auxSingleton.setCurrentUser(snapshotUser);
//                    Log.d(TAG, "snapshot was not null, we have old user with uid: " + snapshotUser.getUID() + " and isHost: " + snapshotUser.isHost());
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
//            callback.playlistOnComplete(false);
            populateMyPlaylists(callback);
        } else {
            dbReference.child(PLAYLISTS_NODE).child(playlistKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Playlist playlist = dataSnapshot.getValue(Playlist.class);
                    hasActive = false;
                    if (playlist != null && playlist.getActive()) {
                        setCurrentPlaylist(playlist, playlistKey);
                        hasActive = true;
                    }
                    callback.playlistOnComplete(hasActive);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
        }
    }

    public void checkIfJoinPlaylist(final DialogOutputter dialogOutputter, final Playlist playlistToJoin) {
        dbReference.child(PLAYLISTS_NODE).child(currentUser.getPlaylistKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Playlist playlist = dataSnapshot.getValue(Playlist.class);
                //playlisttoJoin is null if trying to HOST a playlist
                if (playlistToJoin != null) {
                    if (playlist != null && playlist.getActive() && !playlist.getPlaylistKey().equals(playlistToJoin.getPlaylistKey())) {
                        dialogOutputter.outputDialog(playlistToJoin);
                    } else {
                        hasActive = false;
                        dialogOutputter.viewAndJoinActivity(playlistToJoin);
                    }
                }
                else {
                    if (playlist != null && playlist.getActive()) {
                        dialogOutputter.outputDialog(null);
                    } else {
                        hasActive = false;
                        dialogOutputter.viewAndJoinActivity(null);
                    }

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

    public void setCurrentPlaylistViewOnly(Playlist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist, String playlistKey) {
        this.currentPlaylist = currentPlaylist;
        if (playlistKey != null || playlistRef == null) {
            playlistRef = dbReference.child(PLAYLISTS_NODE).child(playlistKey);
        }
        currentUser.setPlaylistKey(playlistKey);
        updateValue(userRef, "playlistKey", playlistKey);
        playlistRef.setValue(currentPlaylist, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
//                    Log.d(TAG, "Data could not be saved " + databaseError.getMessage());
                } else {
//                    Log.d(TAG, "Data saved successfully.");
                }
            }
        });
    }

    public boolean checkIsHost(String uid) {
        boolean isHost = false;
        if (playlistRef != null && currentPlaylist != null) {
            isHost = currentPlaylist.getHostDeviceID().equals(uid);
        }
        return isHost;
    }

    public void leavePlaylist(Context context) {
        removeUserFromPlaylist(currentUser);
        userRef.child("playlistKey").removeValue();
        if (currentPlaylist.getActive() && checkIsHost(currentUser.getUID())) {
            // change playlist to inactive, remove user, etc.
            currentPlaylist.setActive(false);
//            spotifyPlayer.pause(null);
            DatabaseReference currPlaylistRef = dbReference.child(PLAYLISTS_NODE).child(currentPlaylist.getPlaylistKey());
            updateValue(currPlaylistRef, "active", false);
        }
        context.startActivity(new Intent(context, ActualStartActivity.class));
    }

    public void createPlaylist(String partyName, String password, GeoLocation mGeoLocation) {
        // only host could have called this method. thus, the current user is a host.
        ArrayList<String> userUIDList = new ArrayList<String>();
        userUIDList.add(currentUser.getUID());

        ArrayList<String> songIdList = new ArrayList<String>();

        playlistRef = dbReference.child(PLAYLISTS_NODE).push();
        String key = playlistRef.getKey();
        Playlist playlist = new Playlist(userUIDList, songIdList, partyName, password, currentUser.getUID(),
                0, true, 0, mGeoLocation, "", "");
        playlist.setPlaylistKey(key);
        setCurrentPlaylist(playlist, key);

        addPastPlaylist(playlist);
        // ADD PLAYLIST TO PAST PLAYLISTS FOR THE USER
    }

    public void addSong(Song song) {
        songCache.put(song.getSongId(), song);
        currentPlaylist.addSong(song);
        updateValue(playlistRef, SPOTIFYSONGID_LIST, currentPlaylist.getSpotifySongIDList());
//        if (musicAdapter != null) {
//            musicAdapter.notifyDataSetChanged();
//        }
//        Log.d(TAG, "we added a song (maybe), yay!");
        // add to database using dbReference with the appropriate hashes, child, etc.
    }

    public void removeSong(Song song) {
        currentPlaylist.removeSong(song);
        updateValue(playlistRef, SPOTIFYSONGID_LIST, currentPlaylist.getSpotifySongIDList());
    }

    public void addPastPlaylist(Playlist playlist) {
        if (currentUser != null) {
            boolean added = currentUser.addToPastPlaylists(playlist.getPlaylistKey());
            if (added) {
                if (myPlaylists == null) {
                    Log.d(TAG, "wtf why is myplaylists null?");
                }
                myPlaylists.add(playlist);
                if (userRef == null) {
                    userRef = dbReference.child(USERS_NODE).child(currentUser.getUID());
                }
                updateValue(userRef, PASTPLAYLISTS_LIST, currentUser.getPastPlaylists());
            }
        }
    }

    public void addUser(User user) {
        dbReference.child(USERS_NODE).child(user.getUID()).setValue(user);
        setCurrentUser(user);
        // add to database using dbReference with the appropriate hashes, child, etc.
    }

    public void addUserToPlaylist(User user) {
        currentPlaylist.addUser(user);
        updateValue(playlistRef, USERID_LIST, currentPlaylist.getUserDeviceIDList());
    }

    public void removeUserFromPlaylist(User user) {
        user.setPlaylistKey("");
        currentPlaylist.removeUser(user);
        updateValue(playlistRef, USERID_LIST, currentPlaylist.getUserDeviceIDList());
    }

    public void initializePlaylistListeners() {
        if (spotifySongIDListener == null) {
            spotifySongIDListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                    List<String> spotifySongIDList = dataSnapshot.getValue(t); // Causes bug when spotifySongIDList is null
                    if (spotifySongIDList != null) {
                        currentPlaylist.setSpotifySongIDList(spotifySongIDList);
//                        Log.d(TAG, "we set the new spotifiysongidlist with last songid " + currentPlaylist.getSpotifySongIDList().get(spotifySongIDList.size() - 1));
                        getSongs(spotifySongIDList, new AuxGetSongTask() {
                            @Override
                            public void onFinished(List<Song> songs) {
                                currentPlaylist.setSpotifySongList(songs);
                                musicAdapter.notifyDataSetChanged();
//                                Log.d(TAG, "Finished getting songs, in playlistlistener and last song is " + songs.get(songs.size() - 1).getSongName());
                            }
                        });
                    } else {
                        currentPlaylist.setSpotifySongIDList(new ArrayList<String>());
                        currentPlaylist.setSpotifySongList(new ArrayList<Song>());
                        musicAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            playlistRef.child(SPOTIFYSONGID_LIST).addValueEventListener(spotifySongIDListener);
        }
    }

    public void detachPlaylistListeners() {
        if (spotifySongIDListener != null) {
            playlistRef.child(SPOTIFYSONGID_LIST).removeEventListener(spotifySongIDListener);
            spotifySongIDListener = null;
//            Log.d(TAG, "detach playlist listeners");
        }
    }

    public DatabaseReference getDataBaseReference() {
        return dbReference;
    }

    public static String getSpotifyAuthToken() {
        return spotifyAuthToken;
    }

    public static void setSpotifyAuthToken(String spotifyAuthToken) {
        AuxSingleton.spotifyAuthToken = spotifyAuthToken;
    }

    public static boolean hasAuxPlayer() {
        return auxPlayer != null;
    }

    public PlayerInterface getAuxPlayer() {
//        if (auxPlayer == null) {
//            // TODO: do null checks on spotifyPlayer and Currentplayist
//            auxPlayer = new AuxSpotifyPlayer(spotifyPlayer, currentPlaylist);
//        }
        return auxPlayer;
    }

    public static MusicAdapter getMusicAdapter() {
        return musicAdapter;
    }

    public static void setMusicAdapter(MusicAdapter musicAdapter) {
        AuxSingleton.musicAdapter = musicAdapter;
    }


    public static void setSpotifyPlayer(Player musicPlayer) {
        AuxSingleton.spotifyPlayer = musicPlayer;
    }

    public void createAuxPlayer() {
        auxPlayer = new AuxSpotifyPlayer(getSpotifyPlayer(), currentPlaylist);
    }

    public static Player getSpotifyPlayer() {
        return spotifyPlayer;
    }

    public static SpotifyService getSpotifyService() {
        if (spotifyService == null) {
            SpotifyApi api = new SpotifyApi();
            // Most (but not all) of the Spotify Web API endpoints require authorisation.
            // If you know you'll only use the ones that don't require authorisation you can skip this step
//                api.setAccessToken(AuxSingleton.getSpotifyAuthToken());
            spotifyService = api.getService();
        }
        return spotifyService;
    }

    public void setCoverArtURL(String coverArtURL) {
//        updateValue(dbReference.child(PLAYLISTS_NODE).child(user.getUID()));
    }







    public interface AuxGetSongTask {
        public void onFinished(List<Song> songs);
    }


    /**
     * Reads songs from memoized list of songs, or batch queries them from Spotify.
     *
     * @author Will
     * @param songIds array of songIds, in the format of 6rqhFgbbKwnb9MLmUQDhG6, NOT spotify:track:6rqhFgbbKwnb9MLmUQDhG6
     * @return list of Song objects
     */
    public static List<Song> getSongs(List<String> songIds, final AuxGetSongTask agst) {
        final ArrayList<Song> output = new ArrayList<>();
        StringBuilder builder = null;

        // Spotify batch queries limited to 50 songs max
        int queryCounter = 0;
        ArrayList<String> queries = new ArrayList<>();

        for (String songId : songIds) {
//            Song s = songCache.get(songId);
//            if (s != null) {
//                output.add(s);
//            } else {
            if (builder == null) {
                builder = new StringBuilder();
            } else {
                builder.append(',');
            }
            builder.append(songId);
            queryCounter++;

            if (queryCounter == 50) {
                queryCounter = 0;
                queries.add(builder.toString());
                builder = null;
            }
//            }
        }
        if (builder != null) {
            queries.add(builder.toString());
        }

        for (String q : queries) {
            getSpotifyService().getTracks(q, new Callback<Tracks>(){
                @Override
                public void success(Tracks tracks, Response response) {
//                    ArrayList<Song> tempList = new ArrayList<Song>();
                    for (Track t : tracks.tracks) {
                        Song s = new Song(t);
//                        tempList.add(s);
                        output.add(s);
                    }
                    agst.onFinished(output);

                    Log.e("AuxSingleton", "Called callback on " + tracks.tracks.size() + " tracks");

                }
                @Override
                public void failure(RetrofitError error) {
                    Log.e("Get Song by id failure", error.toString());
                }
            });
        }
        Log.e("AuxSingleton", "Finished getSong");
        return output;

    }

    public void initializeCurrentSongListeners() {
        if (currentSongIndexListener == null) {
            currentSongIndexListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int newIndex = dataSnapshot.getValue(Integer.class);
                    updateCurrentSongView(currentPlaylist.getSpotifySongList().get(newIndex));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            playlistRef.child(CURRENTSONGINDEX).addValueEventListener(currentSongIndexListener);
        }
    }

    public void detachCurrentSongListeners() {
        if (currentSongIndexListener != null) {
            playlistRef.child(CURRENTSONGINDEX).removeEventListener(currentSongIndexListener);
            currentSongIndexListener = null;
//            Log.d(TAG, "detach playlist listeners");
        }
    }

    public void updateCurrentSong(Playlist mPlaylist) {
        if (playlistRef != null) {
            updateValue(playlistRef, CURRENTSONGINDEX, mPlaylist.getCurrentSongIndex());
        } else {
            Log.d(TAG, "terrible error");
        }
    }

    public static Object getPlayerReference() {
        return playerReference;
    }

    public static void setPlayerReference(Object playerReference) {
        AuxSingleton.playerReference = playerReference;
    }

    public static void destroyPlayerReference() {
        Spotify.destroyPlayer(playerReference);
    }

    public static CurrentSongView getCurrentSongView() {
        return currentSongView;
    }

    public static void setCurrentSongView(CurrentSongView currentSongView) {
        AuxSingleton.currentSongView = currentSongView;
    }

    public void updateCurrentSongView(Song song) {
        if (getCurrentSongView() != null) {
            getCurrentSongView().setSong(song);
        }
    }

    public boolean isPlaying() {
        try {
            return getSpotifyPlayer().getPlaybackState().isPlaying;
        } catch (Exception e) {
            return false;
        }
    }

    public Song getCurrentSong() {
        int index = currentPlaylist.getCurrentSongIndex();
        if (index < currentPlaylist.getSpotifySongList().size()) {
            return currentPlaylist.getSpotifySongList().get(index);
        } else {
            return null;
        }
    }

    public static PlayerView getPlayerView() {
        return playerView;
    }

    public static void setPlayerView(PlayerView playerView) {
        AuxSingleton.playerView = playerView;
    }


    public static Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
