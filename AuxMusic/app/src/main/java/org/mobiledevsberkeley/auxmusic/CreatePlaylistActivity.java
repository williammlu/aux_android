package org.mobiledevsberkeley.auxmusic;

import android.app.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import static android.R.id.message;

public class CreatePlaylistActivity extends AppCompatActivity implements DialogOutputter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
    public static String TAG = "debug";

    private CheckBox passwordProtectCheckbox;
    private EditText passwordEditText;
    private EditText nameText;
    private Button createPlaylistBtn;
    private TextView passwordAdditionalInformation;
    private TextView locationAdditionalInformation;

    //these are to prevent edge case create playlists (ie no name, no password playlists
    boolean isNameOK = false;
    boolean isPasswordOK = true;

    private AuxSingleton aux = AuxSingleton.getInstance();

    GoogleApiClient mGoogleApiClient;
    GeoLocation mGeoLocation;
    Activity thisActivity;

    LocationRequest locationRequest;

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 69;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mGoogleApiClient == null) createGoogleApiClient();
        setContentView(R.layout.activity_create_playlist);
        setTitle("Host");
        thisActivity = this;

        passwordProtectCheckbox = (CheckBox) findViewById(R.id.passwordProtect);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordEditText.setVisibility(View.GONE);
        nameText = (EditText) findViewById(R.id.nameTextView);
        createPlaylistBtn = (Button) findViewById(R.id.createPlaylistBtn);
        passwordAdditionalInformation = (TextView) findViewById(R.id.passwordAdditionalInformation);
//        locationAdditionalInformation = (TextView) findViewById(R.id.locationAdditionalInformation);

        mGeoLocation = null;

        setBtnListeners();

    }

    private void nameTextListener() {
        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) isNameOK = false;
                else isNameOK = true;
                checkCreatePlaylistButtonEnabled();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkCreatePlaylistButtonEnabled() {
        if (isNameOK && isPasswordOK) createPlaylistBtn.setEnabled(true);
        else createPlaylistBtn.setEnabled(false);
    }

    private void checkedChangeListener() {
        passwordProtectCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordEditText.setVisibility(View.VISIBLE);
                    isPasswordOK = false;
                    checkCreatePlaylistButtonEnabled();
                    passwordEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() == 0) isPasswordOK = false;
                            else isPasswordOK = true;
                            checkCreatePlaylistButtonEnabled();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                }
                else {
                    passwordEditText.setVisibility(View.GONE);
                    isPasswordOK = true;
                    checkCreatePlaylistButtonEnabled();
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setBtnListeners() {
        nameTextListener();
        checkedChangeListener();
        setAdditionalInformation();

        createPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this is where I check if this potential host already has a playlist
                if (aux.hasActive) {
                    aux.checkIfJoinPlaylist((DialogOutputter) thisActivity, null);
                } else {
                    //create this playlist
                    createPlaylist();
                }
            }
        });
    }

    private void createPlaylist() {
        String partyName = nameText.getText().toString();
        String password = "";
        if (passwordProtectCheckbox.isChecked()) {
            password = passwordEditText.getText().toString();
        }
//                boolean locationTrack = ((CheckBox) findViewById(R.id.locationChecker)).isChecked();
//                boolean hostApproval = ((CheckBox) findViewById(R.id.hostApprovalChecker)).isChecked();

//                if (locationTrack) {
//                    // do location track stuff, then mGeoLocation != null
//                }

        aux.createPlaylist(partyName, password, mGeoLocation);


        // do spotify auth when trying to create
        Intent spotifyAuthIntent = new Intent(getApplicationContext(), SpotifyAuthTest.class);
        startActivity(spotifyAuthIntent);

    }

    private void setAdditionalInformation() {
        passwordAdditionalInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(thisActivity)
                        .content(R.string.password_additional_dialog)
                        .show();
            }
        });

//        locationAdditionalInformation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
//                builder.setMessage(R.string.location_additional_dialog)
//                        .create();
//            }
//        });
    }

    @Override
    public void outputDialog(Playlist playlist) {
        new MaterialDialog.Builder(thisActivity)
                .title(R.string.hasCurrentPlaylistDialog)
                .content(R.string.createPlaylistDialogMessage)
                .positiveText(R.string.createAnyways)
                .negativeText(R.string.dontCreate)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        createPlaylist();
                    }
                })
                .show();

    }

    @Override
    public void viewAndJoinActivity(Playlist playlist) {
        createPlaylist();
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
//                if (passwordProtectCheckbox.isChecked()) {
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
//                    public void userOnComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
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
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("debug", "bitch you're connected");
                            PackageManager pm = thisActivity.getPackageManager();
                    int hasPerm = pm.checkPermission(
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            thisActivity.getPackageName());
                    if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                        getLastLocation();
                    } else {
                        ActivityCompat.requestPermissions(thisActivity,
                                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                    }
//        getLastLocation();
    }



    @Override
    public void onConnectionSuspended(int i) {
        Log.d("debug", "connection suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("debug", "connection failed :(");

    }

    public void createGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getLastLocation();

                } else {

                    Toast.makeText(thisActivity, "Unable to track location without permission", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    public void getLastLocation() throws SecurityException{
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,locationRequest,this);

    }

    @Override
    public void onLocationChanged(Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (location != null) {
            mGeoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
        }

    }
}