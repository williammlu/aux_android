package org.mobiledevsberkeley.auxmusic;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class ActualStartActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    AuxSingleton aux = AuxSingleton.getInstance();
    String TAG = "debug";
    SignInCallback callback;

    PlaylistAdapter playlistAdapter;

    RecyclerView pastPlaylistsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = new SignInCallback() {
            @Override
            public void userOnComplete() {
                Log.d(TAG, "on oncomplete");
                aux.hasCurrentPlaylist(this);
            }

            @Override
            public void playlistOnComplete(boolean hasCurrentPlaylist) {
                if (hasCurrentPlaylist) {
                    aux.isCurrentActive = true;
                    Log.d(TAG, "hasplaylist");
                    //might need to change this

                    // jump to spotify auth
                    if (aux.checkIsHost(aux.getCurrentUser().getUID())) { // if host, jump to spotify auth, which will redirect to playlist
                        Intent spotifyAuthIntent = new Intent(getApplicationContext(), SpotifyAuthTest.class);
                        startActivity(spotifyAuthIntent);
                    } else { // else go directly to playlist
                        Intent playlistIntent = new Intent(getApplicationContext(), PlaylistActivity.class);
                        startActivity(playlistIntent);
                    }

                } else {
                    Log.d(TAG, "doesnthaveplaylist");
                    setContentView(R.layout.activity_actual_start);
                    Button createPlaylistButton = (Button) findViewById(R.id.create_playlist_button);

                    createPlaylistButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), CreatePlaylistActivity.class);
                            startActivity(intent);
                        }
                    });
                    setSearchView();
                    setRecyclerViewNearMe();
                    setRecyclerViewMyPlaylist();
                }
            }
        };
        firebaseSignIn();
    }

    private void setSearchView() {
        findViewById(R.id.searchView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchPlaylistsActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setRecyclerViewMyPlaylist() {
        pastPlaylistsRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewMyPlaylist);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        pastPlaylistsRecyclerView.setLayoutManager(llm);
        ArrayList<Playlist> playlists = aux.getMyPlaylists();
//        if (playlists == null) {
//            Log.d(aux.PASTPLAYLISTS, "why is playlists == null?");
//            playlists = new ArrayList<>();
//        }
        if (playlists != null) {
            Log.d("pastplaylists", "in actualstartactivity: size of auxmyplaylists is " + playlists.size());
        }
        playlistAdapter = new PlaylistAdapter(this, playlists, PlaylistAdapter.PASTPLAYLISTS_VIEW);
        pastPlaylistsRecyclerView.setAdapter(playlistAdapter);
    }

    private void setRecyclerViewNearMe() {
//        Playlist currentPlaylist = new Playlist();
//        currentPlaylist.setPlaylistName("Young's Playlist");
//        //to test, must access wills search activity first
//        currentPlaylist.addSong(SearchSongsActivity.youngSongTest);
//
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewNearMe);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setLayoutManager(llm);
//        nearMe = new ArrayList<>();
//
//        nearMe.add(currentPlaylist);
//
//        PlaylistAdapter playlistAdapter = new PlaylistAdapter(this, nearMe);
//        recyclerView.setAdapter(playlistAdapter);
    }

    private void firebaseSignIn() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();
                User currentUser = aux.getCurrentUser();
                if (firebaseUser == null && currentUser == null) {
                    signInAnon();
                } else if (currentUser == null) {
                    aux.getUser(firebaseUser.getUid(), callback);
                    Log.d(TAG, "second else if");
                } else if (firebaseUser != null && currentUser != null) {
                    Log.d(TAG, "3rd elseif");
                    callback.userOnComplete();
//                    setPlaylistIfActive(aux.getCurrentUser().getPlaylistKey());
                }
            }
        };
    }

    private void signInAnon() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "log in: " + task.getException());
                        Toast.makeText(ActualStartActivity.this, "log in success: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (playlistAdapter != null) {
            playlistAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing ha.
    }
    // EVENTUALLY OVERRIDE ONBACKPRESSED SO THAT WE DON'T ACCIDENTALLY GO BACK INTO THE PLAYLIST AFTER
    // THEY LEAVE THE PLAYLIST
}
