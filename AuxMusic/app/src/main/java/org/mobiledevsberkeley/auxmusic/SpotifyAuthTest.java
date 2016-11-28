package org.mobiledevsberkeley.auxmusic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class SpotifyAuthTest extends Activity
{

    private static final String CLIENT_ID = "687e297cd52c436eb680444a7b0519f9";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "AuxMusic://callback";


    // Request code that will be used to verify if the result comes from correct activity
// Can be any integer
    private static final int REQUEST_CODE = 1337;

    public static final String LOGIN_ERROR = "LOGIN_ERROR";
    public static final String LOGIN_TERMINATED = "LOGIN_TERMINATED";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_spotify_auth_test);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.e("Spotify Auth", "Results - Request code: " + requestCode + " Result Code: " + resultCode);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {

            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            Intent i;

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.e("SpotifyAuthTest", "successful auth token " + response.getAccessToken());
                    AuxSingleton.getInstance().setSpotifyAuthToken(response.getAccessToken());

                    // TODO: this activity will be opened from the actualstartactivity in the future when pressing the button to auth
                    // if playlist exists, it will redirect, but ensure that it will still work
                    Log.e("SpotifyAuthTest", "starting createPlaylistActivity");
                    Intent startIntent = getIntent();
                    if (startIntent.hasExtra(ActualStartActivity.DO_SKIP_CREATE)) {
                        i = new Intent(this, PlaylistActivity.class);
                    } else {
                        i = new Intent(this, CreatePlaylistActivity.class);
                    }


                    startActivity(i);
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.e("onActivityResult", "Auth error: " + response.getError());
                    i = new Intent(this, ActualStartActivity.class);
                    i.putExtra(LOGIN_ERROR, "Unfortunately, Aux requires Spotify premium to host a playlist. Without it, you will not be able to stream music.");
                    startActivity(i);

                    // requires premium, notify user that it requires premium
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.e("onActivityResult", "Auth result: " + response.getType());
                    i = new Intent(this, ActualStartActivity.class);
                    i.putExtra(LOGIN_TERMINATED, "Your authentication with Spotify was cancelled, please try again.");
                    startActivity(i);
            }

        }
    }


    /**
     * Related for 
     * @param intent
     */
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri != null) {
            AuthenticationResponse response = AuthenticationResponse.fromUri(uri);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    Log.e("SpotifyAuthTest", "Token" + response.toString());
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.e("SpotifyAuthTest", "Error" + response.toString());

                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    Log.e("SpotifyAuthTest", "Default" + response.toString());

            }
        }
    }

}