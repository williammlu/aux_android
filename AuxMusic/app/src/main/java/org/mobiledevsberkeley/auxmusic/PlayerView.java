package org.mobiledevsberkeley.auxmusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.spotify.sdk.android.player.Player;

/**
 * TODO: document your custom view class.
 */
public class PlayerView extends RelativeLayout {

    private ImageButton mForwardButton;
    private ImageButton mPlayButton;
    private ImageButton mBackButton;
    private PlayerInterface player;


    public PlayerView(Context context) {
        super(context);
        initializeViews(context);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public PlayerView(Context context,
                       AttributeSet attrs,
                       int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.player_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mForwardButton = (ImageButton) this
                .findViewById(R.id.player_view_skip_button);
        mForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player = AuxSingleton.getInstance().getAuxPlayer();
                if (player != null)
                    player.skip();
            }
        });

        mBackButton = (ImageButton) this
                .findViewById(R.id.player_view_back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player = AuxSingleton.getInstance().getAuxPlayer();
                if (player != null)
                    player.skipBack();

            }
        });


        mPlayButton = (ImageButton) this
                .findViewById(R.id.player_view_play_button);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player = AuxSingleton.getInstance().getAuxPlayer();
                if (player != null) {
                    boolean isPlaying = player.togglePlay();
                    if (!isPlaying) {
                        mPlayButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_48px));
                    } else {
                        mPlayButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_48px));

                    }
                }

                // TODO: actually do the toggle on the button
            }
        });


        mPlayButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {

                int mRequestCode = 5;
                Snackbar snackbar = Snackbar
                        .make(v, "Starting Spotify Auth", Snackbar.LENGTH_SHORT);
                snackbar.show();

                // Go to Spotify test Auth
                Intent myIntent = new Intent(getContext(), SpotifyAuthTest.class);
//                        myIntent.putExtra("key", value); //Optional parameters
                // TODO change requestCode and put somewhere safer, currently matches
                // request code in SpotifyAuthTest
                ((Activity)getContext()).startActivityForResult(myIntent, mRequestCode);
                return false;
            }
        });


    }
}