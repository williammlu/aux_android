package org.mobiledevsberkeley.auxmusic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {
    private String TAG = "debug";

    AuxSingleton aux = AuxSingleton.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button createPlaylistButton;
    private Button willsSpotifyButton;
    private Button playlistActivityButton;
    private Button addSongActivityButton;
    private Button wilburTestingStuffButton;

    private Button mWill_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mWill_button = (Button) findViewById(R.id.will_button);

//        firebaseSignIn();

        createPlaylistButton = (Button) findViewById(R.id.createPlaylistActivityBtn);
        willsSpotifyButton = (Button) findViewById(R.id.willstuffBtn);
        playlistActivityButton = (Button) findViewById(R.id.playlistActivityBtn);
        addSongActivityButton = (Button) findViewById(R.id.addSongActivityBtn);
        wilburTestingStuffButton = (Button) findViewById(R.id.wilburTestingBtn);
        setBtnListeners();

        mWill_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, SearchSongsActivity.class);
                startActivity(intent);
            }
        });

        mWill_button.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {

                int mRequestCode = 5;


                Snackbar snackbar = Snackbar
                        .make(v, "Starting Spotify Auth", Snackbar.LENGTH_SHORT);
                snackbar.show();

                // Go to Spotify test Auth
                Intent myIntent = new Intent(StartActivity.this, SpotifyAuthTest.class);
//                        myIntent.putExtra("key", value); //Optional parameters
                // TODO change requestCode and put somewhere safer, currently matches
                // request code in SpotifyAuthTest
                StartActivity.this.startActivityForResult(myIntent, mRequestCode);
                return false;
            }
        });
    }



    private void setBtnListeners() {
        wilburTestingStuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aux.getCurrentPlaylist() != null) {
                    aux.addSong(new Song("songuri", "imageurl", "songname", "artistname", "albumname", 0));
                }
            }
        });
        createPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent cpIntent = new Intent(getApplicationContext(), CreatePlaylistActivity.class);
//                startActivity(cpIntent);
                //this is just used for my testing, feel free to delete
                Intent searchSongs = new Intent(getApplicationContext(), ActualStartActivity.class);
                startActivity(searchSongs);
            }
        });
        willsSpotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saIntent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(saIntent);
            }
        });
        playlistActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pIntent = new Intent(getApplicationContext(), PlaylistActivity.class);
                startActivity(pIntent);
            }
        });
        addSongActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ssIntent = new Intent(getApplicationContext(), SearchSongsActivity.class);
                startActivity(ssIntent);
            }
        });
    }

    private void firebaseSignIn() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                User currentUser = aux.getCurrentUser();
                if (firebaseUser == null) {
                    Log.d(TAG, "user not signed in");
                    signInAnon();
                } else if (currentUser == null) {
                    Log.d(TAG, "current user was null");
                    User user = new User();
                    user.setUID(firebaseUser.getUid());
                    aux.setCurrentUser(user);
                } else if (aux.getCurrentPlaylist() != null) {
                    Log.d(TAG, "user signed in anon " + aux.getCurrentUser().getUID());
                    // jump straight to playlist activity, while getting playlist things from Firebase
                } else {
                    Toast.makeText(StartActivity.this, "firebase sign in with uid: " + currentUser.getUID(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "user signed in with uid: " + currentUser.getUID());
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
                        Toast.makeText(StartActivity.this, "log in success: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
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


