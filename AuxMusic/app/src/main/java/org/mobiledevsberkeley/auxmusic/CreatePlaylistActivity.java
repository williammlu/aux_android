package org.mobiledevsberkeley.auxmusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import com.google.android.gms.location.LocationListener;

public class CreatePlaylistActivity extends AppCompatActivity /*implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener*/{
    public static String TAG = "debug";

    private CheckBox isPrivateCheckbox;
    private TextView passwordText;
    private EditText passwordEditText;
    private EditText nameText;
    private Button createPlaylistBtn;

    private AuxSingleton aux = AuxSingleton.getInstance();

    //    GoogleApiClient mGoogleApiClient;
    GeoLocation mGeoLocation;
    Activity thisActivity;

    LocationRequest locationRequest;

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 69;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (mGoogleApiClient == null) createGoogleApiClient();
        setContentView(R.layout.activity_create_playlist);
        thisActivity = this;

        isPrivateCheckbox = (CheckBox) findViewById(R.id.publicPrivate);
        passwordText = (TextView) findViewById(R.id.passwordTextView);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        nameText = (EditText) findViewById(R.id.nameTextView);
        createPlaylistBtn = (Button) findViewById(R.id.createPlaylistBtn);

        mGeoLocation = null;

        setBtnListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mGoogleApiClient.connect();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setBtnListeners() {
        isPrivateCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        createPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String partyName = nameText.getText().toString();
                String password = "";
                if (isPrivateCheckbox.isChecked()) {
                    password = passwordEditText.getText().toString();
                }
                boolean locationTrack = ((CheckBox) findViewById(R.id.locationChecker)).isChecked();
                boolean hostApproval = ((CheckBox) findViewById(R.id.hostApprovalChecker)).isChecked();

                if (locationTrack) {
                    // do location track stuff, then mGeoLocation != null
                }

                aux.createPlaylist(partyName, password, mGeoLocation, hostApproval);

                Intent searchSongsIntent = new Intent(getApplicationContext(), SearchSongsActivity.class);
                startActivity(searchSongsIntent);
            }
        });
    }

//    private void testingFirebaseStuff(){
//
//        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // test comment
//                DatabaseReference playlistRef = mDatabase.child(getString(R.string.playlistFirebase)).push();
//                playlistKey = playlistRef.getKey(); // store this somewhere?
//
//                String partyName = nameText.getText().toString();
//                String password = null;
//                if (isPrivateCheckbox.isChecked()) {
//                    password = passwordEditText.getText().toString();
//                }
//                ArrayList<String> useridstuff = new ArrayList<>();
//                //^i think that should prolly be an arraylist of users, depending on what we want on participants page
////                useridstuff.add()
//                ArrayList<String> songidstuff = new ArrayList<String>();
//                mGeoLocation = null;
//                boolean locationTrack = ((CheckBox) findViewById(R.id.locationChecker)).isChecked();
//
//                if (locationTrack) {
//                    PackageManager pm = thisActivity.getPackageManager();
//                    int hasPerm = pm.checkPermission(
//                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                            thisActivity.getPackageName());
//                    if (hasPerm == PackageManager.PERMISSION_GRANTED) {
////                        getLastLocation();
//                    } else {
//                        ActivityCompat.requestPermissions(thisActivity,
//                                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
//                                MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
//                    }
//
//                    if (locationTrack && mGeoLocation != null) {
//                        GeoFire geoFire = new GeoFire(playlistRef);
//                        geoFire.setLocation(getString(R.string.locationPlaylistFirebase), mGeoLocation);
//
//                        DatabaseReference locationsRef = mDatabase.child(getString(R.string.locationsFirebase));
//                        GeoFire geoFire1 = new GeoFire(locationsRef);
//                        geoFire1.setLocation(playlistKey, mGeoLocation);
//                    }
//                }
//                boolean hostApproval = ((CheckBox) findViewById(R.id.hostApprovalChecker)).isChecked();
//
//                currentPlaylist = new Playlist(useridstuff, songidstuff, partyName, password, "random", 0, true, 0, mGeoLocation, hostApproval);
////                    currentPlaylist = new Playlist(useridstuff, songidstuff, partyName, password, mGeoLocation, hostApproval);
//
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
//                Intent searchSongsIntent = new Intent(getApplicationContext(), SearchSongsActivity.class);
////                    searchSongsIntent.putExtra("")
//                startActivity(searchSongsIntent);
//            }
//        });
//    }

//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        Log.d("debug", "bitch you're connected");
////        getLastLocation();
//    }
//
//
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.d("debug", "connection suspended");
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.d("debug", "connection failed :(");
//
//    }
//
//    public void createGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        locationRequest = LocationRequest.create();
//        locationRequest.setInterval(1000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                    getLastLocation();
//
//                } else {
//
//                    Toast.makeText(thisActivity, "Unable to track location without permission", Toast.LENGTH_LONG).show();
//                }
//            }
//
//        }
//    }
//
//    public void getLastLocation() throws SecurityException{
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,locationRequest,this);
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
////        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        if (location != null) {
//            mGeoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
//        }
//
//    }
}