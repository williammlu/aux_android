package org.mobiledevsberkeley.auxmusic;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {
    ArrayList<Song> songsList = new ArrayList<>();
    AuxSingleton aux = AuxSingleton.getInstance();
    String TAG = "debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        youngShit();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        long i = 0;
        songsList.add(new Song("bob",new ArrayList<String>(), "bobo", "hardcoded", "wheee", i));

        MusicAdapter musicAdapter = new MusicAdapter(this, songsList, MusicAdapter.DISPLAY_PLAYLIST);

        recyclerView.setAdapter(musicAdapter);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void youngShit() {
        /*1. Check if this current playlist and is active
        * If yes:
        * Make leave button active. If click leave:
        * Clear playlist under user. if host, also change isActive under the playlist attribute
        * 2. If no,
        * Make reactivate button active. If active but not current playlist, add user to playlist. add playlist-list
         * and playlsit to user. BUT IF USER HAD AN ACTIVE PLAYLIST, TELL THEM IT WILL LEAVE THAT PLAYLIST FIRST.
         * If inactive and host, clicking reactivate changes isActive to true, sets currentPlaylist
         * If inactive and not host, cannot do anything*/
        Button leaveButton = (Button) findViewById(R.id.leavePlaylist);
        Button activateButton = (Button) findViewById(R.id.activatePlaylist);
        //singleton playlist should reflect whichever playlist is being DISPLAYED. firebase playlist should reflect whichever is ACTIVE

        //replace all of this once merged
        final User user = aux.getCurrentUser();
        final DatabaseReference usersRef = aux.getDataBaseReference().child("users").child(user.getUID()).push();
        boolean isCurrentPlaylist = user.getPlaylistKey().equals("");
        boolean isActive = true;
        final boolean isHost = true;

        if (isCurrentPlaylist && isActive) {
            leaveButton.setVisibility(View.VISIBLE);
            leaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.setPlaylistKey(null);
                    usersRef.setValue(user, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.d(TAG, "Data could not be saved " + databaseError.getMessage());
                        } else {
                            Log.d(TAG, "Data saved successfully.");
                        }
                    }
                });
                    if (isHost) {

                    }





                }
            });

        }



    }

}
