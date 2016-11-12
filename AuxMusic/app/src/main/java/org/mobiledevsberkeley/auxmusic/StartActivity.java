package org.mobiledevsberkeley.auxmusic;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class StartActivity extends AppCompatActivity {
    private String TAG = "debug";

    AuxSingleton aux = AuxSingleton.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        firebaseSignIn();
    }

//    private void firebaseSignIn() {
//        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();
//                if (firebaseUser == null) {
//                    Log.d(TAG, "user not signed in");
//                    signInAnon();
//                } else if (aux.getCurrentUser() == null) {
//
//                }
//
//                if (firebaseUser != null && aux.getCurrentPlaylist() != null) {
//                    Log.d(TAG, "user signed in anon " + firebaseUser.getUid());
//                } else {
//                    Log.d(TAG, "user not signed in ");
//                    signInAnon();
////                    uid = user.getUid();
//                    aux.addUser(aux.getCurrentUser());
//                }
//            }
//        };
//    }
//
//    private void signInAnon() {
//        mAuth.signInAnonymously()
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "log in: " + task.getException());
//                        Toast.makeText(StartActivity.this, "log in success: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

}
