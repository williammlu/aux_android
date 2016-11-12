package org.mobiledevsberkeley.auxmusic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.spotify.sdk.android.player.Error;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.R.attr.value;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SearchActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    private Button mTestSpotifyAuth;
    private Button mPlayButton;
    private Button mSkipButton;
    private Button mQueueSearchButton;
    private EditText mSongNameTextView;
    private Playlist mPlaylist = new Playlist();

    private AuxSpotifyPlayer auxSpotifyPlayer;

    String mAccessToken = "";

    private int mRequestCode = 5;
    private static final boolean mDisableHide = true;
    private static final String CLIENT_ID = "687e297cd52c436eb680444a7b0519f9";

    private SpotifyPlayer mPlayer;


    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mTestSpotifyAuth = (Button) findViewById(R.id.auth_button);
        mPlayButton = (Button) findViewById(R.id.play_button);
        mSkipButton = (Button) findViewById(R.id.skip_button);
        mQueueSearchButton = (Button) findViewById(R.id.play_songs_button);
        mSongNameTextView = (EditText) findViewById(R.id.search_song_name_text);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });


        // Play button - play if player is not null
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummyButton).setOnTouchListener(mDelayHideTouchListener);

        //Take us to the create-playlist screen
        findViewById(R.id.createPlaylist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createPlaylistIntent = new Intent(getApplicationContext(), CreatePlaylistActivity.class);
                startActivity(createPlaylistIntent);
                //create dialog fragment
            }
        });


        mTestSpotifyAuth.setOnTouchListener(mDelayHideTouchListener);
        mTestSpotifyAuth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(mContentView, "Starting Spotify Auth", Snackbar.LENGTH_SHORT);
                snackbar.show();

                // Go to Spotify test Auth
                Intent myIntent = new Intent(SearchActivity.this, SpotifyAuthTest.class);
                myIntent.putExtra("key", value); //Optional parameters

                // TODO change requestCode and put somewhere safer, currently matches
                // request code in SpotifyAuthTest
                SearchActivity.this.startActivityForResult(myIntent, mRequestCode);


            }
        });


        mPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String msg;

                if(auxSpotifyPlayer == null)
                {
                    msg = "AuxSpotifyPlayer not yet created";
                } else {
                    // Choose to Pause or Play
                    msg = auxSpotifyPlayer.togglePlay();
                }

                Snackbar snackbar = Snackbar
                        .make(mContentView, msg, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String msg;
                if (auxSpotifyPlayer == null) {
                    msg = "AuxSpotifyPlayer not yet created";
                    Snackbar snackbar = Snackbar
                            .make(mContentView, msg, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    long index = auxSpotifyPlayer.skip();
                }
            }
        });



        mQueueSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            SpotifyApi api = new SpotifyApi();

            // Most (but not all) of the Spotify Web API endpoints require authorisation.
            // If you know you'll only use the ones that don't require authorisation you can skip this step
            api.setAccessToken(mAccessToken);

            SpotifyService spotify = api.getService();
            String searchQuery = mSongNameTextView.getText().toString();
            Log.d("SearchActivity", "Search box said: " + searchQuery);
            if (searchQuery.length() > 0) {
                spotify.searchTracks(searchQuery, new Callback<TracksPager>(){
                    @Override
                    public void success(TracksPager tPager, Response response) {
                        String msg;
                        List<Track> tracks = tPager.tracks.items;
                        if (tracks.size() > 0) {
                            Track track = tracks.get(0);
                            Song song = new Song(track);
                            Log.e("SearchActivity", "Added song is " + song.getTrackLength() + "ms");
                            mPlaylist.addSong(song);
                            msg = "Queued " + song.getSongName();

                        } else {
                            msg = "No Songs matched this search!";
                        }
                        Snackbar snackbar = Snackbar
                                .make(mContentView, msg, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("Track Playing failure", error.toString());
                    }
                });
            }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == mRequestCode) {
            String result = "";

            if(resultCode == Activity.RESULT_OK){
                result = data.getStringExtra("result");
                mAccessToken = data.getStringExtra("access_token");
                createSpotifyPlayer(mAccessToken);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                result=data.getStringExtra("result");
            }

            Snackbar snackbar = Snackbar
                    .make(mContentView, result + "\naccess_token=" + mAccessToken, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle user action
                            Log.d("SearchActivity", "Snack bar dismissed");
                        }
                    });
            snackbar.show();
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        if (mDisableHide) {
            return;
        }

        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

//    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("SearchActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

//    @Override
    public void onPlaybackError(Error error) {
        Log.d("SearchActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("SearchActivity", "User logged in");

    }

    @Override
    public void onLoggedOut() {
        Log.d("SearchActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(int i) {
        Log.d("SearchActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("SearchActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("SearchActivity", "Received connection message: " + message);
    }




    public void createSpotifyPlayer(String token) {
        Config playerConfig = new Config(this, token, CLIENT_ID);
        SpotifyPlayer p = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                mPlayer = spotifyPlayer;
                mPlayer.addConnectionStateCallback(SearchActivity.this);
                mPlayer.addNotificationCallback(SearchActivity.this);

            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("SpotifyAuth", "Could not initialize player: " + throwable.getMessage());
            }
        });
        auxSpotifyPlayer = new AuxSpotifyPlayer(p, mPlaylist);

    }

}
