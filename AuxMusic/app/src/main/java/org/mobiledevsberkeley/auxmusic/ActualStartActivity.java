package org.mobiledevsberkeley.auxmusic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActualStartActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    AuxSingleton aux = AuxSingleton.getInstance();
    String TAG = "debug";
    Playlist playlist;
    ArrayList<Playlist> myPlaylists;
    ArrayList<Playlist> nearMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseSignIn();
        checkIfCurrentPlaylist();
        setContentView(R.layout.activity_actual_start);
        setRecyclerViewNearMe();
        setRecyclerViewMyPlaylist();
    }

    private void checkIfCurrentPlaylist() {
        Playlist mPlaylist = aux.getCurrentPlaylist();
        if (mPlaylist != null) {
            Intent playlistIntent = new Intent(getApplicationContext(),PlaylistActivity.class);
            startActivity(playlistIntent);
        }
    }

    private void setRecyclerViewMyPlaylist() {
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMyPlaylist);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setLayoutManager(llm);
//        myPlaylists = new ArrayList<>();
    }

    private void setRecyclerViewNearMe() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewNearMe);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);
        nearMe = new ArrayList<>();

        PlaylistAdapter playlistAdapter = new PlaylistAdapter(this, nearMe);
        recyclerView.setAdapter(playlistAdapter);
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
                    getUser(firebaseUser);
                    setPlaylistIfActive(aux.getCurrentUser().getPlaylistKey());
                } else if (firebaseUser != null && aux.getCurrentUser() != null) {
                    setPlaylistIfActive(aux.getCurrentUser().getPlaylistKey());
                }
            }
        };
    }
    private void getUser(FirebaseUser firebaseUser) {

        DatabaseReference usersRef = aux.getDataBaseReference().child("users").child(firebaseUser.getUid());
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                aux.setCurrentUser(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setPlaylistIfActive(String playlistKey) {
        getPlaylist(playlistKey);
        if (playlist != null && playlist.getPlaying()) aux.setCurrentPlaylist(playlist);
    }

    private void getPlaylist(String playlistKey) {
        //this will set the playlist variable in the global scope, however we only want to ever use the SINGLETON playlist
        DatabaseReference playlistRef = aux.getDataBaseReference().child("playlists").child(playlistKey);
        playlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                playlist = dataSnapshot.getValue(Playlist.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
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
}
