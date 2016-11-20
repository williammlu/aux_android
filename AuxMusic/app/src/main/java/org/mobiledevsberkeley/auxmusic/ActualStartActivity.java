package org.mobiledevsberkeley.auxmusic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.drive.query.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
    ArrayList<Playlist> myPlaylists;
    ArrayList<Playlist> nearMe;
    SignInCallback callback;
    SearchView searchView;

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
                    Intent playlistIntent = new Intent(getApplicationContext(), PlaylistActivity.class);
                    startActivity(playlistIntent);
                    Log.d(TAG, "hasplaylist");
                } else {
                    setContentView(R.layout.activity_actual_start);
                    searchView = (android.support.v7.widget.SearchView) findViewById(R.id.searchView);
                    setSearchView();
                    setRecyclerViewNearMe();
                    setRecyclerViewMyPlaylist();
                    Log.d(TAG, "doesnthaveplaylist");
                }
            }
        };
        firebaseSignIn();
    }

    private void setSearchView() {
        searchView.setIconified(false);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String regexed = query.replaceAll("[^A-Za-z]","").toLowerCase();
                DatabaseReference playlistRef = aux.getDataBaseReference().child("playlists");
                com.google.firebase.database.Query queryRef = playlistRef.orderByChild("regexedPlaylistName").equalTo(regexed);

                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        System.out.println(dataSnapshot.getKey());
                        Log.d("debug", dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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
//        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
    }
}
