package org.mobiledevsberkeley.auxmusic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.app.Activity;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.sdk.android.player.ConnectionStateCallback;
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
public class SearchActivity extends AppCompatActivity {

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
    public static final String debugTag = "debug";
    Button[] buttonHolder = new Button[4];
    int buttonCount = 0;

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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mTestSpotifyAuth = (Button) findViewById(R.id.auth_button);

        setSearchView();


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
        geoFireQuery();

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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void geoFireQuery() {
        buttonHolder[0] = (Button) findViewById(R.id.query0);
        buttonHolder[1] =(Button) findViewById(R.id.query1);
        buttonHolder[2] =(Button) findViewById(R.id.query2);
        buttonHolder[3] =(Button) findViewById(R.id.query3);

//        findViewById(R.id.queryButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatabaseReference newRef = CreatePlaylistActivity.mDatabase.child(getString(R.string.locationsFirebase));
//                // change this once location works
//                GeoFire geoFireSearch = new GeoFire(newRef);
//                GeoQuery geoQuery = geoFireSearch.queryAtLocation(new GeoLocation(37.7853889, -122.4056973), 0.6);
//                //set this to like 4 closest keys
//                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//                    @Override
//                    public void onKeyEntered(String key, GeoLocation location) {
//                        Toast.makeText(getApplicationContext(), String.format("Key %s entered the search area at [%f,%f]",
//                                key, location.latitude, location.longitude), Toast.LENGTH_SHORT).show();
//                        // once spotify works figure out what wanna do with this playlist
//                        getPlaylist(key);
//                    }
//
//                    @Override
//                    public void onKeyExited(String key) {
//                        Log.d(debugTag, String.format("Key %s is no longer in the search area", key));
//                    }
//
//                    @Override
//                    public void onKeyMoved(String key, GeoLocation location) {
//                        Log.d(debugTag, String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
//                    }
//
//                    @Override
//                    public void onGeoQueryReady() {
//                        Log.d(debugTag, "All initial data has been loaded and events have been fired!");
//                    }
//
//                    @Override
//                    public void onGeoQueryError(DatabaseError error) {
//                        Log.d(debugTag, "There was an error with this query: " + error);
//                    }
//                });
//            }
//        });
//
    }

    private void setSearchView() {
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search for s");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // do something with spotify api

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // do something with spotify api
                return false;
            }
        });

    }

    private void getPlaylist(String key) {
//        DatabaseReference playlistRef = CreatePlaylistActivity.mDatabase.child("playlists").child(key);
//        playlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                final Playlist myPlaylist = dataSnapshot.getValue(Playlist.class);
//                final Button currButton =  buttonHolder[buttonCount];
//                currButton.setText(myPlaylist.getPlaylistName());
//                currButton.setVisibility(View.VISIBLE);
//                currButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        AuxSingleton.getInstance().setCurrentPlaylist(myPlaylist);
//                        Toast.makeText(SearchActivity.this, "Curr Playlist is now" +
//                                currButton.getText(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                buttonCount = (buttonCount + 1) % 4;
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // ...
//            }
//        });
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == mRequestCode) {
//            String result = "";
//            String accessToken = "";
//            if (resultCode == Activity.RESULT_OK) {
//                result = data.getStringExtra("result");
//                mAccessToken = data.getStringExtra("access_token");
//                createSpotifyPlayer(mAccessToken);
//            }
//            if (resultCode == Activity.RESULT_CANCELED) {
//                result = data.getStringExtra("result");
//            }
//
//            Snackbar snackbar = Snackbar
//                    .make(mContentView, result + "\naccess_token=" + mAccessToken, Snackbar.LENGTH_INDEFINITE)
//                    .setAction("Dismiss", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            // Handle user action
//                            Log.d("SearchActivity", "Snack bar dismissed");
//                        }
//                    });
//            snackbar.show();
//        }
//    }


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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Search Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}