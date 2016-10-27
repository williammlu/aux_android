package org.mobiledevsberkeley.auxmusic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CreatePlaylistActivity extends AppCompatActivity {
    private String TAG = "debug";
    private String uid = "";

    private CheckBox publicPrivate;
    private TextView passwordText;
    private EditText passwordEditText;
    private EditText nameText;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public Playlist currentPlaylist; // PROBABLY STORE THIS IN A DATAHOLDER, SINGLETON CLASS.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);

        publicPrivate = (CheckBox) findViewById(R.id.publicPrivate);
        passwordText = (TextView) findViewById(R.id.passwordTextView);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        nameText = (EditText) findViewById(R.id.nameTextView);

        firebaseSignIn();
        testingFirebaseStuff();

        publicPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordText.setVisibility(View.VISIBLE);
                    passwordEditText.setVisibility(View.VISIBLE);
                }
                else {
                    passwordText.setVisibility(View.GONE);
                    passwordEditText.setVisibility(View.GONE);
                }
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

    private void firebaseSignIn() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                    Log.d(TAG, "user signed in anon " + user.getUid());
                } else {
                    Log.d(TAG, "user not signed in ");
                    signInAnon();
                    uid = user.getUid();
                    mDatabase.child("users").child(uid).setValue("recent playlists or something like dat");
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
                        Toast.makeText(CreatePlaylistActivity.this, "log in success: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void testingFirebaseStuff() {
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // test comment
                Log.d(TAG, "work please");
                DatabaseReference playlistRef = mDatabase.child("playlists").push();
                String playlistKey = playlistRef.getKey(); // store this somewhere?

                String partyName = nameText.getText().toString();
                String password = "";
                if (publicPrivate.isChecked()) {
                    password = passwordEditText.getText().toString();
                }

                // TESTING STUFF. NOT WORKING YET...

//                ArrayList<String> useridstuff = new ArrayList<>();
//                ArrayList<String> songidstuff = new ArrayList<String>();
//
////                currentPlaylist = new Playlist(useridstuff, songidstuff, partyName, password);
//                playlistRef.setValue(currentPlaylist, new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                        if (databaseError != null) {
//                            Log.d(TAG, "Data could not be saved " + databaseError.getMessage());
//                        } else {
//                            Log.d(TAG, "Data saved successfully.");
//                        }
//                    }
//                });
//                DatabaseReference nameRef = firebase.child(getString(R.string.playlistName));
//                DatabaseReference password = firebase.child(getString(R.string.playlistPassword));
//                DatabaseReference passwordProtect = firebase.child(getString(R.string.playlistPasswordProtect));
//                String name = nameText.getText().toString();
//                nameRef.setValue(name);
//                passwordProtect.setValue(publicPrivate.isChecked());
//                if (publicPrivate.isChecked()) {
//                    password.setValue(passwordEditText.getText().toString());
//                }
//                Intent searchSongsIntent = new Intent(getApplicationContext(), SearchSongsActivity.class);
//                startActivity(searchSongsIntent);

            }
        });
    }
}
