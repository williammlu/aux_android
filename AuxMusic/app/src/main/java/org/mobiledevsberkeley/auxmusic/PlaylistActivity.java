package org.mobiledevsberkeley.auxmusic;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    AuxSingleton aux = AuxSingleton.getInstance();
    String TAG = "debug";
    private View parentView;
    private boolean isHost;
    Button addSongButton;

    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isHost = aux.checkIsHost(aux.getCurrentUser().getUID());
        if (isHost) {
            setContentView(R.layout.activity_playlist_host);
        }
        else {
            setContentView(R.layout.activity_playlist_guest);
        }
        setTitle(aux.getCurrentPlaylist().getPlaylistName());
        parentView = findViewById(R.id.activity_playlist_layout);

        createSpotifyPlayer(AuxSingleton.getInstance().getSpotifyAuthToken());

        initializeButtons();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                getResources().getConfiguration().orientation);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.horizontal_line));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // set musicadapter/recyclerview to take the same list of songs as the current playlist!
        List<Song> playlistSongList = AuxSingleton.getInstance().getCurrentPlaylist().getSpotifySongList();
        if (isHost) {
            musicAdapter = new MusicAdapter(this, playlistSongList, MusicAdapter.DISPLAY_PLAYLIST, findViewById(R.id.content_playlist));
        } else {
            musicAdapter = new MusicAdapter(this, playlistSongList, MusicAdapter.DISPLAY_PLAYLIST_GUEST, findViewById(R.id.content_playlist));
        }
        AuxSingleton.getInstance().setMusicAdapter(musicAdapter);
        recyclerView.setAdapter(musicAdapter);

        // For creation, replace songs in list with retrieved results
        AuxSingleton.getSongs(aux.getCurrentPlaylist().getSpotifySongIDList(), new AuxSingleton.AuxGetSongTask() {
            @Override
            public void onFinished(List<Song> songs) {
                aux.getCurrentPlaylist().setSpotifySongList(songs);
                musicAdapter.notifyDataSetChanged();
                Log.e("PlaylistActivity", "callback with " + songs.size());
            }
        });
    }

    private void setButtonsEnabledandDisabled() {
        if (aux.isCurrentActive) {
            addSongButton.setVisibility(View.VISIBLE);
            menu.findItem(R.id.joinPlaylist).setVisible(false);
            menu.findItem(R.id.leavePlaylist).setVisible(true);

        }
        else {
            addSongButton.setVisibility(View.GONE);
            menu.findItem(R.id.joinPlaylist).setVisible(true);
            menu.findItem(R.id.leavePlaylist).setVisible(false);

        }
    }

    private void initializeButtons() {
        /*1. Check if this current playlist and is active
        * If yes:
        * Make leave button active. If click leave:
        * Clear playlist under user. if host, also change isActive under the playlist attribute
        * 2. If no,
        * Make reactivate button active. If active but not current playlist, add user to playlist. add playlist-playlists
         * and playlist to user. BUT IF USER HAD AN ACTIVE PLAYLIST, TELL THEM IT WILL LEAVE THAT PLAYLIST FIRST.
         * If inactive and host, clicking reactivate changes isActive to true, sets currentPlaylist
         * If inactive and not host, cannot do anything*/
//        Button leaveButton = (Button) findViewById(R.id.leavePlaylist);
//        Button activateButton = (Button) findViewById(R.id.activatePlaylist);
//        //singleton playlist should reflect whichever playlist is being DISPLAYED. firebase playlist should reflect whichever is ACTIVE

        CurrentSongView currentSongView = (CurrentSongView) findViewById(R.id.currentSongView);
        AuxSingleton.getInstance().setCurrentSongView(currentSongView);

        addSongButton = (Button) findViewById(R.id.playlistAddSongButton);
        addSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchSongsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if (musicAdapter != null) {
            musicAdapter.notifyDataSetChanged();
        }
        // INITIALIZE firebase realtime listeners for the playlist.
        aux.initializePlaylistListeners();
    }


    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Log.e("PlaylistActivity", "calling destroy!!");
        Spotify.destroyPlayer(this);
        aux.detachPlaylistListeners();
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("PlaylistActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("PlaylistActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.e("PlaylistActivity", "User logged in");

    }

    @Override
    public void onLoggedOut() {
        Log.e("PlaylistActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(int i) {
        Log.e("PlaylistActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("PlaylistActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("PlaylistActivity", "Received connection message: " + message);
    }

    private static final String CLIENT_ID = "687e297cd52c436eb680444a7b0519f9";

    public void createSpotifyPlayer(String token) {
        if (token != null && token.length() != 0) {
            Config playerConfig = new Config(this, token, CLIENT_ID);
            SpotifyPlayer p = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer spotifyPlayer) {
                    spotifyPlayer.addConnectionStateCallback(PlaylistActivity.this);
                    spotifyPlayer.addNotificationCallback(PlaylistActivity.this);
                    AuxSingleton.getInstance().setSpotifyPlayer(spotifyPlayer);
                    AuxSingleton.getInstance().createAuxPlayer();
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("SpotifyAuth", "Could not initialize player: " + throwable.getMessage());
                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        this.menu = menu;
        setButtonsEnabledandDisabled();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.joinPlaylist:
                menu.findItem(R.id.leavePlaylist).setVisible(true);
                item.setVisible(false);

                //joining logic

                aux.isCurrentActive = true;
                Playlist playlist = aux.getCurrentPlaylist();
                aux.setCurrentPlaylist(playlist, playlist.getPlaylistKey());
                aux.addPastPlaylist(playlist);


            case R.id.leavePlaylist:
                menu.findItem(R.id.joinPlaylist).setVisible(true);
                item.setVisible(false);
                //leaving logic
                aux.leavePlaylist(this, isHost);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(this, ActualStartActivity.class));
        // do nothing . ha
    }
}
